package com.hawolt.riot.task.impl;

import com.hawolt.Logger;
import com.hawolt.bulk.DispatchQueue;
import com.hawolt.bulk.task.AbstractTask;
import com.hawolt.bulk.task.TaskCallback;
import com.hawolt.core.Client;
import okhttp3.*;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created: 04/03/2022 17:21
 * Author: Twitter @hawolt
 **/

public class SessionRefresh extends AbstractTask {

    private static final MediaType MEDIA_TYPE = MediaType.parse("application/json");

    private final String session, platform;
    private final TaskCallback callback;

    public SessionRefresh(TaskCallback callback, String platform, String session) {
        super(callback);
        this.callback = callback;
        this.platform = platform;
        this.session = session;
    }

    @Override
    public void run() {
        try {
            String destination = String.format("https://%s/session-external/v1/session/refresh", platform);
            JSONObject object = new JSONObject();
            object.put("lst", session);
            RequestBody post = RequestBody.create(object.toString(), MEDIA_TYPE);
            Request request = new Request.Builder()
                    .url(destination)
                    .addHeader("Authorization", String.format("Bearer %s", session))
                    .addHeader("Content-Type", "application/json")
                    .post(post)
                    .build();
            Call call = Client.perform(request);
            try (Response response = call.execute()) {
                try (ResponseBody body = response.body()) {
                    if (body == null) throw new IOException("no response body");
                    this.callback.onTaskCompletion(body.string());
                }
            }
        } catch (Exception e) {
            Logger.error(e);
        } finally {
            DispatchQueue.unlock(session);
        }
    }

    @Override
    public String getLock() {
        return session;
    }
}
