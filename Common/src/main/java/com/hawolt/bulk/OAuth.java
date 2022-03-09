package com.hawolt.bulk;

import com.hawolt.SHA256;
import com.hawolt.bulk.exception.PermitExpiredException;
import com.hawolt.bulk.task.TaskCallback;
import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

/**
 * Created: 03/03/2022 17:45
 * Author: Twitter @hawolt
 **/

public abstract class OAuth implements ResponseInterface, TaskCallback {
    private final long hourInMillis = TimeUnit.SECONDS.toMillis(3600);

    private final String username, password, lock;
    private JSONObject response;
    private long lastRefresh;

    public OAuth(String username, String password) {
        this.lock = SHA256.hash(username + password + System.currentTimeMillis());
        this.username = username;
        this.password = password;
        this.refresh();
    }

    private boolean isExpired() {
        return response == null || lastRefresh == 0L || (System.currentTimeMillis() - lastRefresh >= hourInMillis);
    }

    public void refresh() {
        if (isExpired()) {
            try {
                throw new PermitExpiredException(lastRefresh == 0L ? -1 : lastRefresh + hourInMillis);
            } catch (PermitExpiredException e) {
                onError(e);
            }
        }
    }

    public String get(Token token) throws Exception {
        this.refresh();
        if (response == null) throw new Exception("OAuth not initialized");
        return response.getString(token.getName());
    }

    public void setup(JSONObject response) {
        boolean isNull = this.response == null;
        long last = lastRefresh;
        this.lastRefresh = System.currentTimeMillis() - TimeUnit.MINUTES.toMillis(10L);
        this.response = response;
        if (isNull) onReady();
        this.onRefresh(response, last);
    }

    public long getLastRefresh() {
        return lastRefresh;
    }

    public String getLock() {
        return lock;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
