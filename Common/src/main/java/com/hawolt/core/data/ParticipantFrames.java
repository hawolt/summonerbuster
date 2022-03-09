package com.hawolt.core.data;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created: 04/03/2022 10:42
 * Author: Twitter @hawolt
 **/

public class ParticipantFrames {

    private final Map<Byte, Participant> map = new HashMap<>();

    public ParticipantFrames(JSONObject participantFrames) {
        for (String key : participantFrames.keySet()) {
            Participant participant = new Participant(participantFrames.getJSONObject(key));
            map.put(participant.getParticipantId(), participant);
        }
    }

    public void setPUUID(byte id, String puuid) {
        map.get(id).setPUUID(puuid);
    }

    public Participant[] getParticipants() {
        return map.values().toArray(new Participant[0]);
    }
}
