package com.hawolt.core.data;

import org.json.JSONObject;

/**
 * Created: 04/03/2022 10:41
 * Author: Twitter @hawolt
 **/

public class JSON {
    private final Frames frames;

    public JSON(JSONObject object) {
        this.frames = new Frames(object.getJSONArray("frames"));
    }

    public Frames getFrames() {
        return frames;
    }
}
