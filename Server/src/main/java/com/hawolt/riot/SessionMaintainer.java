package com.hawolt.riot;

import com.hawolt.bulk.exception.PermitExpiredException;
import com.hawolt.bulk.task.TaskCallback;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created: 03/03/2022 17:45
 * Author: Twitter @hawolt
 **/

public abstract class SessionMaintainer implements SessionResponse, TaskCallback {

    private final long minutesInMillis = TimeUnit.MINUTES.toMillis(10);

    private long lastRefresh = System.currentTimeMillis() - TimeUnit.MINUTES.toMillis(3);
    private final String host;
    private String session;

    public SessionMaintainer(String host, String session) {
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(this::refresh, 0, 1, TimeUnit.MINUTES);
        this.session = session;
        this.host = host;
    }

    public String getHost() {
        return host;
    }

    public long getLastRefresh() {
        return lastRefresh;
    }

    private boolean isExpired() {
        return (System.currentTimeMillis() - lastRefresh >= minutesInMillis);
    }

    public void refresh() {
        if (isExpired()) {
            try {
                throw new PermitExpiredException(lastRefresh + minutesInMillis);
            } catch (PermitExpiredException e) {
                onError(e);
            }
        }
    }

    public String get() {
        this.refresh();
        return session;
    }

    protected String getSession() {
        return session;
    }

    public void setup(String response) {
        this.session = response.substring(1, response.length() - 1);
        long last = lastRefresh;
        this.lastRefresh = System.currentTimeMillis() - TimeUnit.MINUTES.toMillis(3);
        this.onRefresh(response, last);
    }

}
