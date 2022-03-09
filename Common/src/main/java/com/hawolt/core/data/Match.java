package com.hawolt.core.data;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created: 04/03/2022 10:36
 * Author: Twitter @hawolt
 **/

public class Match {

    private final JSON json;
    private final long matchId;
    private MetaData metaData;

    public Match(long matchId, JSONObject object) {
        this.matchId = matchId;
        JSONObject json = object.getJSONObject("json");
        this.json = new JSON(json);
        ParticipantFrames frames = this.json.getFrames().getParticipantFrames();
        if (frames == null || !json.has("participants")) return;
        JSONArray participants = json.getJSONArray("participants");
        this.metaData = new MetaData(object.getJSONObject("metadata"));
        for (int i = 0; i < participants.length(); i++) {
            JSONObject participant = participants.getJSONObject(i);
            byte id = (byte) participant.getInt("participantId");
            String puuid = participant.getString("puuid");
            frames.setPUUID(id, puuid);
        }
    }

    public long getMatchId() {
        return matchId;
    }

    public JSON getJson() {
        return json;
    }

    public MetaData getMetaData() {
        return metaData;
    }
}
