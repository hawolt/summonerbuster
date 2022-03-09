package com.hawolt.external.impl;

import com.hawolt.external.ExternalUpdatingDataResource;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created: 05/03/2022 22:54
 * Author: Twitter @hawolt
 **/

public class CommunityDragonResource extends ExternalUpdatingDataResource<String, Integer> {
    public CommunityDragonResource(String location) {
        super(location);
    }

    @Override
    public Integer getValue(String s) {
        return map.getOrDefault(s, -1);
    }

    @Override
    public void declare(int code, String in) {
        JSONArray array = new JSONArray(in);
        for (int i = 0; i < array.length(); i++) {
            JSONObject object = array.getJSONObject(i);
            String name = object.getString("name").toLowerCase();
            if (name.startsWith(" ") || name.startsWith("<") || name.length() == 0) continue;
            map.put(name, object.getInt("id"));
        }
    }
}
