package com.hawolt.riot;

import com.hawolt.Logger;
import com.hawolt.bulk.Account;
import com.hawolt.bulk.Detail;
import com.hawolt.bulk.TokenProvider;
import com.hawolt.bulk.Tokenizer;

/**
 * Created: 07/03/2022 16:35
 * Author: Twitter @hawolt
 **/

public class SessionProvider implements NotificationInterface, SessionPreparationCallback {
    private final SessionCallback callback;
    private final int required;

    private final SessionAccount[] accounts;
    private int index = -1;
    private int count;

    private SessionProvider(SessionCallback callback, TokenProvider provider) {
        this.accounts = new SessionAccount[provider.getAccounts().length];
        this.required = this.accounts.length;
        this.callback = callback;
        for (Account account : provider.getAccounts()) {
            AdvancedAccount.init(this, account);
        }
    }

    public static void init(SessionCallback callback, TokenProvider provider) {
        new SessionProvider(callback, provider);
    }

    public String next() {
        return accounts[index = ++index % accounts.length].get();
    }

    @Override
    public void ready(SessionAccount account) {
        this.accounts[count++] = account;
        if (count >= required) {
            callback.onSetup(this);
        }
    }

    @Override
    public void onError(Exception e) {
        Logger.error(e);
    }

    @Override
    public void onPrepared(AdvancedAccount account) {
        SessionAccount.init(account.getHost(), account.getSession(), this);
    }
}
