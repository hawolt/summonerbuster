package com.hawolt.external;

import com.hawolt.Logger;
import com.hawolt.Request;
import com.hawolt.Response;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created: 05/03/2022 22:51
 * Author: Twitter @hawolt
 **/

public abstract class ExternalUpdatingDataResource<Object, V> extends DataResource<Object, V> implements Runnable {

    protected final String location;

    public ExternalUpdatingDataResource(String location) {
        this.location = location;
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(this, 0, 1, TimeUnit.HOURS);
    }

    @Override
    public void run() {
        try {
            Request request = new Request(location);
            Response response = request.execute();
            if (response.getCode() != 200) return;
            declare(response.getCode(), response.getBodyAsString());
            map();
        } catch (IOException e) {
            Logger.error(e);
        }
    }

    private void map() {
        JSONObject mapping = new JSONObject();
        List<Map.Entry<Object, V>> list = new ArrayList<>(map.entrySet());
        for (Map.Entry<Object, V> entry : list) {
            mapping.put(entry.getKey().toString(), entry.getValue());
        }
        this.mapping = mapping;
    }

    public abstract void declare(int code, String in);
}
