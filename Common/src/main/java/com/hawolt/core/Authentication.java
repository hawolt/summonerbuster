package com.hawolt.core;

import okhttp3.*;

/**
 * Created: 04/03/2022 20:38
 * Author: Twitter @hawolt
 **/

public class Authentication implements Authenticator {
    private final String username, password;

    public Authentication(String username, String password) {
        this.username = username;
        this.password = password;
    }

    @Override
    public Request authenticate(Route route, Response response) {
        String credential = Credentials.basic(username, password);
        return response.request().newBuilder()
                .header("Proxy-Authorization", credential)
                .build();
    }

}
