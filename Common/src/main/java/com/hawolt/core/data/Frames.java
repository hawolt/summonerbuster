package com.hawolt.core.data;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created: 04/03/2022 10:42
 * Author: Twitter @hawolt
 **/

public class Frames {

    private ParticipantFrames participantFrames;

    public Frames(JSONArray array) {
        JSONObject last = array.getJSONObject(array.length() - 1);
        if (last.isNull("participantFrames")) return;
        this.participantFrames = new ParticipantFrames(last.getJSONObject("participantFrames"));
    }

    public ParticipantFrames getParticipantFrames() {
        return participantFrames;
    }
}
