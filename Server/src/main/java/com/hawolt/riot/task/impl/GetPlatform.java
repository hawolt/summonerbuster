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

public class GetPlatform extends AbstractTask {
    private static final MediaType type = MediaType.parse("application/json");

    private final AdvancedAccount account;

    public GetPlatform(TaskCallback callback, AdvancedAccount account) {
        super(callback);
        this.account = account;
    }

    @Override
    public String getLock() {
        return "Platform_" + account.getAccount().getLock();
    }

    @Override
    public void run() {
        try {
            Account account = this.account.getAccount();
            String destination = String.format("https://%s/login-queue/v2/login/products/lol/regions/%s", this.account.getHost(), this.account.getRegion().getPlatform());
            JSONObject o = new JSONObject();
            o.put("clientName", "lcu");
            o.put("entitlements", this.account.getEntitlement());
            o.put("userinfo", this.account.getIdentifier());
            RequestBody post = RequestBody.create(o.toString(), type);
            Request request = new Request.Builder()
                    .url(destination)
                    .addHeader("Authorization", String.format("Bearer %s", account.get(Token.ACCESS)))
                    .addHeader("Content-Type", "application/json")
                    .post(post)
                    .build();
            Call call = Client.perform(request);
            try (Response response = call.execute()) {
                try (ResponseBody body = response.body()) {
                    if (body == null) throw new IOException("no response body");
                    this.account.setPlatform(body.string());
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
