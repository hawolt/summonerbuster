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

import java.io.IOException;

/**
 * Created: 07/03/2022 18:43
 * Author: Twitter @hawolt
 **/

public class GetEntitlement extends AbstractTask {

    private static final RequestBody REQUEST_BODY = RequestBody.create("", MediaType.parse("application/json"));
    private final AdvancedAccount account;

    public GetEntitlement(TaskCallback callback, AdvancedAccount account) {
        super(callback);
        this.account = account;
    }

    @Override
    public String getLock() {
        return "Entitlement_"+account.getAccount().getLock();
    }

    @Override
    public void run() {
        try {
            Account account = this.account.getAccount();
            Request request = new Request.Builder()
                    .url("https://entitlements.auth.riotgames.com/api/token/v1")
                    .addHeader("Authorization", String.format("Bearer %s", account.get(Token.ACCESS)))
                    .addHeader("Content-Type", "application/json")
                    .post(REQUEST_BODY)
                    .build();
            Call call = Client.perform(request);
            try (Response response = call.execute()) {
                try (ResponseBody body = response.body()) {
                    if (body == null) throw new IOException("no response body");
                    this.account.setEntitlement(body.string());
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
