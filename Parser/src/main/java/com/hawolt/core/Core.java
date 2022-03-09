package com.hawolt.core;

import com.hawolt.Logger;
import com.hawolt.Response;
import com.hawolt.bulk.*;
import com.hawolt.core.data.Match;
import com.hawolt.core.data.Participant;
import com.hawolt.core.model.History;
import com.hawolt.core.model.MatchWrapper;
import com.hawolt.sql.Insertion;
import com.hawolt.sql.MatchManager;
import com.hawolt.sql.impl.BatchInfoInsert;
import com.hawolt.sql.impl.MatchInsert;
import org.json.JSONObject;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created: 03/03/2022 16:02
 * Author: Twitter @hawolt
 **/

public class Core extends Daemon implements GameCallback, Tokenizer {

    public static final ExecutorService CACHED_EXECUTOR = Executors.newCachedThreadPool();

    private final ExecutorService GAME_EXECUTOR = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() << 4);
    private final ExecutorService HISTORY_EXECUTOR = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() << 4);

    private final Direction direction;
    private final Region region;

    private long timestamp;
    private TokenProvider provider;

    public static Core launch(Region region, Direction direction, Detail... details) throws SQLException {
        return new Core(region, direction, details);
    }

    private Core(Region region, Direction direction, Detail... details) throws SQLException {
        super(region);
        this.region = region;
        this.direction = direction;
        this.current = new AtomicLong(getStartIndex(region, direction));
        TokenProvider.init(this, details);
    }

    private long getStartIndex(Region region, Direction direction) throws SQLException {
        long index;
        if (direction == Direction.ASC) {
            index = MatchManager.getLastGameID(region);
        } else {
            index = MatchManager.getFirstGameID(region);
        }
        return index != 0 ? index : DEFAULTS[region.ordinal()];
    }

    // GAME-ID TO START WITH IF THERE IS NO DATABASE REFERENCE
    private static final long[] DEFAULTS = new long[]{
            5761936057L,    //EUW
            4237086556L,    //NA
            3076875265L,    //EUNE
            0L,             //OCE
            0L,             //LAN
            0L,             //BR
            1274568788L,    //TR
            378635879L,     //RU
            0L,             //JP
            0L,             //KR
            0L,             //LAS
            0L,             //PBE
    };

    @Override
    public void onReady(TokenProvider provider) {
        this.provider = provider;
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
            if (EXECUTOR.getQueue().size() > 10000) return;
            try {
                if (System.currentTimeMillis() - lastFetch >= TimeUnit.HOURS.toMillis(6)) {
                    this.current = new AtomicLong(getStartIndex(region, direction));
                    this.lastFetch = System.currentTimeMillis();
                } else {
                    String token = provider.next();
                    if (!isEmpty()) {
                        Resubmission resubmission = get();
                        switch (resubmission.getType()) {
                            case GAME:
                                GAME_EXECUTOR.execute(GameParser.fetch(this, region, (long) resubmission.getO(), token));
                                break;
                            case HISTORY:
                                HISTORY_EXECUTOR.execute(GameParser.fetch(this, region, resubmission.getO().toString(), provider.next()));
                                break;
                        }
                    } else {
                        if (direction == Direction.DESC || (System.currentTimeMillis() - timestamp) >= TimeUnit.MINUTES.toMillis(60)) {
                            long nextId = direction == Direction.ASC ? current.incrementAndGet() : current.decrementAndGet();
                            GAME_EXECUTOR.execute(GameParser.fetch(this, region, nextId, token));
                        }
                    }
                }
            } catch (Exception e) {
                Logger.error(e);
            }
        }, 0, 15, TimeUnit.MILLISECONDS);
    }

    public TokenProvider getProvider() {
        return provider;
    }

    private final Object lock = new Object();

    private boolean isEmpty() {
        synchronized (lock) {
            return list.isEmpty();
        }
    }

    private Resubmission get() {
        synchronized (lock) {
            return list.remove(0);
        }
    }

    private void resubmit(SubmissionType type, final Object o) {
        Resubmission resubmission = new Resubmission(type, o);
        HISTORY_EXECUTOR.execute(() -> {
            synchronized (lock) {
                if (!list.contains(resubmission)) list.add(resubmission);
            }
        });
    }

    public boolean isSetup() {
        return provider != null;
    }

    private final Object timer = new Object();

    private void update(long gameCreation) {
        synchronized (timer) {
            if (gameCreation >= timestamp) this.timestamp = gameCreation;
        }
    }

    private long lastFetch = System.currentTimeMillis();

    private final Object MATCH_LOCK = new Object();

    private long getLowestMatchIdFromCache() {
        synchronized (MATCH_LOCK) {
            List<Long> list = new ArrayList<>(MATCH_CACHE.keySet());
            list.sort(Long::compare);
            return list.get(0);
        }
    }

    @Override
    public void onGameFetch(long id, Response response) {
        if (response == null) return;
        if (response.getCode() == 429 || response.getCode() >= 500) {
            Logger.info("RESUBMIT {} - {} - {} : {}", region.name(), SubmissionType.GAME.name(), response.getCode(), response.getBodyAsString());
            resubmit(SubmissionType.GAME, id);
        } else if (response.getCode() == 200) {
            this.lastFetch = System.currentTimeMillis();
            Match match = new Match(id, new JSONObject(response.getBodyAsString()));
            if (match.getMetaData() == null || (!match.getMetaData().isTagged("q_420") && !match.getMetaData().isTagged("q_440")))
                return;
            if (HISTORY_CACHE.containsKey(id)) {
                MatchWrapper wrapper = HISTORY_CACHE.get(id);
                synchronized (HISTORY_CACHE) {
                    HISTORY_CACHE.remove(id);
                }
                complete(wrapper, match);
            } else {
                synchronized (MATCH_LOCK) {
                    MATCH_CACHE.put(id, match);
                }
                Participant participant = match.getJson().getFrames().getParticipantFrames().getParticipants()[0];
                try {
                    HISTORY_EXECUTOR.execute(GameParser.fetch(this, region, participant.getPUUID(), provider.next()));
                } catch (Exception e) {
                    Logger.error(e);
                    resubmit(SubmissionType.GAME, id);
                }
            }
        }
    }

    private final Object HISTORY_LOCK = new Object();

    @Override
    public void onHistoryFetch(String puuid, Response response) {
        if (response == null) return;
        if (response.getCode() == 429 || response.getCode() >= 500) {
            Logger.info("RESUBMIT {} - {} - {} : {}", region.name(), SubmissionType.HISTORY.name(), response.getCode(), response.getBodyAsString());
            resubmit(SubmissionType.HISTORY, puuid);
        } else if (response.getCode() == 200) {
            this.lastFetch = System.currentTimeMillis();
            History history = new History(new JSONObject(response.getBodyAsString()));
            long lowestMatchId = getLowestMatchIdFromCache();
            List<MatchWrapper> list = history.getList();
            for (MatchWrapper wrapper : list) {
                if (wrapper.getMatchId() < lowestMatchId) break;
                if (MATCH_CACHE.containsKey(wrapper.getMatchId())) {
                    Match match = MATCH_CACHE.get(wrapper.getMatchId());
                    synchronized (MATCH_LOCK) {
                        MATCH_CACHE.remove(wrapper.getMatchId());
                    }
                    complete(wrapper, match);
                } else {
                    synchronized (HISTORY_LOCK) {
                        HISTORY_CACHE.put(wrapper.getMatchId(), wrapper);
                    }
                }
            }
        }
    }

    private void complete(MatchWrapper wrapper, Match match) {
        CACHED_EXECUTOR.execute(() -> {
            long gameCreation = wrapper.getGameCreationTimestamp();
            int gameDuration = wrapper.getGameDurationInSeconds();
            if (direction == Direction.ASC) update(gameCreation);
            MatchInsert matchInsert = new MatchInsert(match.getMatchId(), (byte) region.ordinal(), gameCreation, gameDuration, wrapper.getQueueId());
            BatchInfoInsert infoInsert = new BatchInfoInsert(match.getMatchId(), (byte) region.ordinal(), wrapper.join(match));
            final Insertion[] insertions = new Insertion[]{matchInsert, infoInsert};
            EXECUTOR.execute(() -> {
                for (Insertion insertion : insertions) {
                    if (insertion == null) continue;
                    try {
                        insertion.insert();
                    } catch (SQLException e) {
                        if (!e.getMessage().contains("duplicate")) INSERTION_CACHE.add(insertion);
                        Logger.error(e);
                    }
                }
            });
        });
    }
}
