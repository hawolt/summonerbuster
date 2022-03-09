package com.hawolt.core.data;

import org.json.JSONObject;

/**
 * Created: 04/03/2022 10:36
 * Author: Twitter @hawolt
 **/

public class MetaData {
    private final String product, data_version, info_type, match_id;
    private final String[] participants, tags;
    private final long timestamp;
    private final boolean _private;

    public MetaData(JSONObject object) {
        this.product = object.getString("product");
        this.tags = object.getJSONArray("tags").toList().stream().map(Object::toString).toArray(String[]::new);
        this.participants = object.getJSONArray("participants").toList().stream().map(Object::toString).toArray(String[]::new);
        this.timestamp = object.getLong("timestamp");
        this.data_version = object.getString("data_version");
        this.info_type = object.getString("info_type");
        this.match_id = object.getString("match_id");
        this._private = object.getBoolean("private");
    }

    public boolean isTagged(String tag) {
        for (String t : tags) {
            if (t.equals(tag)) {
                return true;
            }
        }
        return false;
    }

    public String getDataVersion() {
        return data_version;
    }

    public String getInfoType() {
        return info_type;
    }

    public String getMatchId() {
        return match_id;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public boolean isPrivate() {
        return _private;
    }

    public String getProduct() {
        return product;
    }

    public String[] getTags() {
        return tags;
    }

    public String[] getParticipants() {
        return participants;
    }
}
