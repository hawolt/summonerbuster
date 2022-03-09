package com.hawolt.core.model;

import com.hawolt.Logger;
import com.hawolt.Request;
import com.hawolt.Response;

import java.io.IOException;

/**
 * Created: 04/03/2022 13:22
 * Author: Twitter @hawolt
 **/

public abstract class CommonExecution implements Runnable {

    protected Response execute(String destination, String bearer) {
        Response response = null;
        try {
            Request request = new Request(destination);
            request.addHeader("User-Agent", "LeagueOfLegendsClient/12.4.423.2790 (rcp-be-lol-match-history)");
            request.addHeader("Authorization", "Bearer " + bearer);
            response = request.execute();
        } catch (IOException e) {
            Logger.error(e);
        }
        return response;
    }
}
