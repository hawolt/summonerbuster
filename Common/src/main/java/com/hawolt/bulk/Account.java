package com.hawolt.bulk;

import com.hawolt.Logger;
import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

/**
 * Created: 04/03/2022 15:14
 * Author: Twitter @hawolt
 **/

public class Account extends OAuth {
    private final Notifier notifier;

    public Account(String username, String password, Notifier notifier) {
        super(username, password);
        this.notifier = notifier;
    }

    public static Account init(String username, String password, Notifier notifier) {
        return new Account(username, password, notifier);
    }

    @Override
    public void onError(Exception e) {
        DispatchQueue.submit(new RiotOAuth(this));
    }

    @Override
    public void onRefresh(JSONObject object, long lastRefresh) {
        long expiresAt = lastRefresh + TimeUnit.SECONDS.toMillis(4200);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(expiresAt - System.currentTimeMillis());
        if (lastRefresh != 0) {
            if (seconds >= 0) {
                Logger.debug("OAuth for {} refreshed in time (remaining: {})", getUsername(), seconds);
            } else {
                Logger.debug("OAuth for {} refreshed to late (overdue: {})", getUsername(), seconds * -1);
            }
        } else {
            Logger.debug("OAuth for {} setup", getUsername());
        }
    }

    @Override
    public void onReady() {
        Logger.debug("{} is setup", getUsername());
        if (notifier != null) {
            notifier.ready(this);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Account)) return false;
        Account account = (Account) o;
        return account.getUsername().equals(getUsername()) && account.getPassword().equals(getPassword());
    }

    @Override
    public void onTaskCompletion(String response) {
        setup(new JSONObject(response));
    }
}
