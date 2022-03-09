package com.hawolt.core.data;

import org.json.JSONObject;

/**
 * Created: 04/03/2022 10:42
 * Author: Twitter @hawolt
 **/

public class Participant {

    private final int totalGold, totalDamageDone;
    private final short creepScore;
    private final byte level, participantId;
    private String puuid;

    public Participant(JSONObject object) {
        this.creepScore = (short) (object.getInt("minionsKilled") + object.getInt("jungleMinionsKilled"));
        this.level = (byte) object.getInt("level");
        this.participantId = (byte) object.getInt("participantId");
        this.totalGold = object.getInt("totalGold");
        JSONObject damageStats = object.getJSONObject("damageStats");
        this.totalDamageDone = damageStats.getInt("totalDamageDone");
    }

    public String getPUUID() {
        return puuid;
    }

    public void setPUUID(String puuid) {
        this.puuid = puuid;
    }

    public int getTotalGold() {
        return totalGold;
    }

    public int getTotalDamageDone() {
        return totalDamageDone;
    }

    public short getCreepScore() {
        return creepScore;
    }

    public byte getLevel() {
        return level;
    }

    public byte getParticipantId() {
        return participantId;
    }
}
