package com.hawolt.core;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;

import java.net.Proxy;
import java.util.concurrent.TimeUnit;

/**
 * Created by: Niklas
 * Date: 31.05.2019
 * Time: 22:48
 */
public class Client {

    public static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/74.0.3729.169 Safari/537.36";

    private static final OkHttpClient NO_PROXY = new OkHttpClient();

    private static OkHttpClient getClient() {
        return getClient(null, null);
    }

    private static OkHttpClient getClient(Proxy proxy, Authentication authentication) {
        if (proxy == null) return NO_PROXY;
        OkHttpClient.Builder builder = new OkHttpClient().newBuilder()
                .connectTimeout(15L, TimeUnit.SECONDS)
                .writeTimeout(15L, TimeUnit.SECONDS)
                .readTimeout(15L, TimeUnit.SECONDS)
                .callTimeout(15L, TimeUnit.SECONDS)
                .proxy(proxy);
        return authentication == null ? builder.build() : builder.proxyAuthenticator(authentication).build();
    }

    public static Call get(String uri) {
        OkHttpClient client = getClient();
        Request request = new Request.Builder()
                .addHeader("user-agent", USER_AGENT)
                .url(uri)
                .build();
        return client.newCall(request);
    }

    public static Call perform(Request request) {
        return perform(request, null, null);
    }

    public static Call perform(Request request, Proxy proxy) {
        return perform(request, proxy, null);
    }

    public static Call perform(Request request, Proxy proxy, Authentication authentication) {
        return getClient(proxy, authentication).newCall(request);
    }

}
