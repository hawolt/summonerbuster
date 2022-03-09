package com.hawolt.bulk;

import com.hawolt.Logger;
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

public class RiotOAuth extends AbstractTask {

    private static final MediaType mediaType = MediaType.parse("application/json");

    private final OAuth auth;

    public RiotOAuth(OAuth auth) {
        super(auth);
        this.auth = auth;
    }

    public OAuth getAuth() {
        return auth;
    }

    @Override
    public void run() {
        try {
            String payload = Payload.getPut(auth.getUsername(), auth.getPassword());
            RequestBody login = RequestBody.create(payload, mediaType);
            String cookie = SessionCreator.getCookie();
            if (cookie == null) throw new IOException("Failed to create session");
            okhttp3.Request request = new Request.Builder()
                    .url("https://auth.riotgames.com/api/v1/authorization")
                    .addHeader("Content-Type", "application/json")
                    .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/80.0.3987.149 Safari/537.36")
                    .addHeader("Pragma", "no-cache")
                    .addHeader("Accept", "*/*")
                    .addHeader("Cookie", cookie)
                    .put(login)
                    .build();
            Call call = Client.perform(request);
            try (Response response = call.execute()) {
                try (ResponseBody body = response.body()) {
                    if (body == null) throw new IOException("no response body");
                    String content = body.string();
                    if (content.contains("invalid_session_id")) {
                        return;
                    }
                    if (content.contains("auth_failure") || content.length() == 0) {
                        return;
                    }
                    if (content.contains("rate_limited")) {
                        return;
                    }
                    JSONObject object = new JSONObject(content);
                    if (object.has("response")) {
                        JSONObject nestOne = object.getJSONObject("response");
                        if (nestOne.has("parameters")) {
                            JSONObject nestTwo = nestOne.getJSONObject("parameters");
                            if (nestTwo.has("uri")) {
                                String values = nestTwo.getString("uri");
                                String client = values.split("#")[1];
                                JSONObject data = new JSONObject();
                                String[] parameters = client.split("&");
                                for (String parameter : parameters) {
                                    String[] pair = parameter.split("=");
                                    data.put(pair[0], pair[1]);
                                }
                                this.callback.onTaskCompletion(data.toString());
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            Logger.error(e);
        } finally {
            DispatchQueue.unlock(auth.getLock());
        }
    }

    @Override
    public String getLock() {
        return auth.getLock();
    }
}
