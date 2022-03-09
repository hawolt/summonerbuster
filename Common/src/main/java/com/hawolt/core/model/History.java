package com.hawolt.core.model;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Created: 04/03/2022 13:37
 * Author: Twitter @hawolt
 **/

public class History implements Iterable<MatchWrapper> {

    private final List<MatchWrapper> list = new ArrayList<>();

    public History(JSONObject object) {
        this(object, false);
    }

    public History(JSONObject object, boolean maintain) {
        JSONArray games = object.getJSONArray("games");
        for (int i = 0; i < games.length(); i++) {
            JSONObject game = games.getJSONObject(i);
            if (!game.has("json")) continue;
            JSONObject json = game.getJSONObject("json");
            short queueId = (short) json.getInt("queueId");
            if (queueId != 420 && queueId != 440) continue;
            MatchWrapper wrapper = new MatchWrapper(json, queueId, maintain);
            list.add(wrapper);
        }
    }

    public History(MatchWrapper[]... collections) {
        for (MatchWrapper[] wrappers : collections) {
            this.list.addAll(Arrays.asList(wrappers));
        }
    }

    public List<MatchWrapper> getList() {
        return list;
    }

    @Override
    public Iterator<MatchWrapper> iterator() {
        return list.iterator();
    }
}
