package com.hawolt.riot.task.impl;

import com.hawolt.Logger;
import com.hawolt.bulk.Account;
import com.hawolt.bulk.DispatchQueue;
import com.hawolt.bulk.Token;
import com.hawolt.bulk.task.AbstractTask;
import com.hawolt.bulk.task.TaskCallback;
import com.hawolt.core.Client;
import com.hawolt.riot.AdvancedAccount;
import okhttp3.*;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created: 07/03/2022 18:43
 * Author: Twitter @hawolt
 **/

public class GetSession extends AbstractTask {
    private static final MediaType type = MediaType.parse("application/json");

    private final AdvancedAccount account;

    public GetSession(TaskCallback callback, AdvancedAccount account) {
        super(callback);
        this.account = account;
    }

    @Override
    public String getLock() {
        return "Session_" + account.getAccount().getLock();
    }

    @Override
    public void run() {
        try {
            String destination = String.format("https://%s/session-external/v1/session/create", this.account.getHost());
            JSONObject object = new JSONObject();
            object.put("product", "lol");
            object.put("puuid", this.account.getPUUID());
            object.put("region", this.account.getRegion().getPlatform());
            object.put("claims", new JSONObject().put("cname", "lcum"));
            JSONObject platform = new JSONObject(this.account.getPlatform());

            /*
             * We just assume it works.
                if (!platform.has("token")) {
                    throw new AccountException(ExceptionType.BANNED_ACCOUNT, "CANNOT CREATE SESSION FOR BANNED USER " + account.getLogin().getUsername());
                }
            */

            RequestBody post = RequestBody.create(object.toString(), type);
            Request request = new Request.Builder()
                    .url(destination)
                    .addHeader("Authorization", String.format("Bearer %s", platform.getString("token")))
                    .addHeader("Content-Type", "application/json")
                    .post(post)
                    .build();
            Call call = Client.perform(request);
            try (Response response = call.execute()) {
                try (ResponseBody body = response.body()) {
                    if (body == null) throw new IOException("no response body");
                    this.account.setSession(body.string());
                }
            }
        } catch (Exception e) {
            this.account.repeat();
        } finally {
            DispatchQueue.unlock(getLock());
            this.account.next();
        }
    }
}
