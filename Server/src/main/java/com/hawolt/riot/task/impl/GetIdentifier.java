package com.hawolt.riot.task.impl;

import com.hawolt.Logger;
import com.hawolt.bulk.Account;
import com.hawolt.bulk.DispatchQueue;
import com.hawolt.bulk.Token;
import com.hawolt.bulk.task.AbstractTask;
import com.hawolt.bulk.task.TaskCallback;
import com.hawolt.core.Client;
import com.hawolt.riot.AdvancedAccount;
import okhttp3.Call;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created: 07/03/2022 18:43
 * Author: Twitter @hawolt
 **/

public class GetIdentifier extends AbstractTask {

    private final AdvancedAccount account;

    public GetIdentifier(TaskCallback callback, AdvancedAccount account) {
        super(callback);
        this.account = account;
    }

    @Override
    public String getLock() {
        return "Identifier_" + account.getAccount().getLock();
    }

    @Override
    public void run() {
        try {
            Account account = this.account.getAccount();
            Request request = new Request.Builder()
                    .url("https://auth.riotgames.com/userinfo")
                    .addHeader("Authorization", String.format("Bearer %s", account.get(Token.ACCESS)))
                    .build();
            Call call = Client.perform(request);
            try (Response response = call.execute()) {
                try (ResponseBody body = response.body()) {
                    if (body == null) throw new IOException("no response body");
                    this.account.setIdentifier(body.string());
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
