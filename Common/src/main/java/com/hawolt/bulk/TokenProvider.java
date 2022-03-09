package com.hawolt.bulk;

import com.hawolt.Logger;

/**
 * Created: 04/03/2022 14:35
 * Author: Twitter @hawolt
 **/

public class TokenProvider implements Notifier {

    private final Tokenizer tokenizer;
    private final Account[] accounts;

    private final int required;
    private int index = -1;
    private int count;

    public static void init(Tokenizer tokenizer, Detail... details) {
        new TokenProvider(tokenizer, details);
    }

    public TokenProvider(Tokenizer tokenizer, Detail... details) {
        this.accounts = new Account[details.length];
        this.required = details.length;
        this.tokenizer = tokenizer;
        for (Detail detail : details) {
            Account.init(detail.getUsername(), detail.getPassword(), this);
        }
    }

    public Account[] getAccounts() {
        return accounts;
    }

    public String next() throws Exception {
        return accounts[index = ++index % accounts.length].get(Token.ACCESS);
    }

    @Override
    public void ready(Account account) {
        this.accounts[count++] = account;
        if (count >= required) {
            tokenizer.onReady(this);
        }
    }

    @Override
    public void onError(Account account, Exception e) {
        Logger.error(e);
    }
}
