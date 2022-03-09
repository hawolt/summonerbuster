package com.hawolt.external;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created: 05/03/2022 22:51
 * Author: Twitter @hawolt
 **/

public abstract class DataResource<Object, V> {

    protected final Map<Object, V> map = new HashMap<>();
    protected JSONObject mapping = new JSONObject();

    public abstract V getValue(Object k);

    public JSONObject getMapping() {
        return mapping;
    }
}
