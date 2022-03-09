package com.hawolt.core.model;

import com.hawolt.core.data.Match;
import com.hawolt.core.data.Participant;
import com.hawolt.core.data.Player;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created: 04/03/2022 13:38
 * Author: Twitter @hawolt
 **/

public class MatchWrapper {

    private final Map<String, ParticipantWrapper> map = new HashMap<>();
    private final long matchId, gameCreation;
    private final short gameDuration, queueId;
    private JSONObject json;

    public MatchWrapper(JSONObject json, short queueId) {
        this(json, queueId, false);
    }

    public MatchWrapper(JSONObject json, short queueId, boolean maintain) {
        if (maintain) this.json = json;
        this.queueId = queueId;
        this.matchId = json.getLong("gameId");
        this.gameCreation = json.getLong("gameCreation");
        this.gameDuration = (short) json.getInt("gameDuration");
        JSONArray participants = json.getJSONArray("participants");
        for (int i = 0; i < participants.length(); i++) {
            ParticipantWrapper wrapper = new ParticipantWrapper(participants.getJSONObject(i));
            map.put(wrapper.getPUUID(), wrapper);
        }
    }

    public JSONObject getRawJson() {
        return json;
    }

    public ParticipantWrapper getWrapper(String puuid) {
        return map.getOrDefault(puuid, null);
    }

    public short getQueueId() {
        return queueId;
    }

    public long getMatchId() {
        return matchId;
    }

    public long getGameCreationTimestamp() {
        return gameCreation;
    }

    public short getGameDurationInSeconds() {
        return gameDuration;
    }

    public long getGameEndTimestamp() {
        return gameCreation + TimeUnit.SECONDS.toMillis(gameDuration);
    }

    public Player[] join(Match match) {
        Participant[] participants = match.getJson().getFrames().getParticipantFrames().getParticipants();
        Player[] players = new Player[participants.length];
        for (int i = 0; i < participants.length; i++) {
            Participant participant = participants[i];
            players[i] = new Player(participant, map.get(participant.getPUUID()));
        }
        return players;
    }
}
