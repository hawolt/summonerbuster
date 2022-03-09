package com.hawolt.riot;

import com.hawolt.Logger;
import com.hawolt.bulk.DispatchQueue;
import com.hawolt.bulk.exception.PermitExpiredException;
import com.hawolt.riot.task.impl.SessionRefresh;

import java.util.concurrent.TimeUnit;

/**
 * Created: 04/03/2022 15:14
 * Author: Twitter @hawolt
 **/

public class SessionAccount extends SessionMaintainer {
    private final NotificationInterface notification;

    public SessionAccount(String host, String session, NotificationInterface notification) {
        super(host, session);
        this.notification = notification;
        this.onReady();
    }

    public static SessionAccount init(String platform, String session, NotificationInterface notification) {
        return new SessionAccount(platform, session, notification);
    }

    @Override
    public void onError(PermitExpiredException e) {
        DispatchQueue.submit(new SessionRefresh(this, getHost(), getSession()));
    }

    @Override
    public void onRefresh(String response, long lastRefresh) {
        long expiresAt = lastRefresh + TimeUnit.MINUTES.toMillis(3);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(expiresAt - System.currentTimeMillis());
        if (seconds >= 0) {
            Logger.debug("Session refreshed in time (remaining: {})", seconds);
        } else {
            Logger.debug("Session refreshed to late (overdue: {})", seconds * -1);
        }
    }

    @Override
    public void onReady() {
        if (notification != null) {
            notification.ready(this);
        }
    }

    @Override
    public void onTaskCompletion(String response) {
        setup(response);
    }
}
