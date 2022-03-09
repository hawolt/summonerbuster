package com.hawolt.core.model;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created: 04/03/2022 13:45
 * Author: Twitter @hawolt
 **/

public class ParticipantWrapper {
    private final boolean won;
    private final String puuid, name;
    private final short championId, teamId, spell1Id, spell2Id, wards, primary, secondary;
    private final byte kills, deaths, assists, champLevel;
    private final int[] items = new int[7];

    public ParticipantWrapper(JSONObject object) {
        this.won = object.getBoolean("win");
        this.puuid = object.getString("puuid");
        this.championId = (short) object.getInt("championId");
        this.teamId = (short) object.getInt("teamId");
        this.spell1Id = (short) object.getInt("spell1Id");
        this.spell2Id = (short) object.getInt("spell2Id");
        this.name = object.getString("summonerName");
        this.kills = (byte) object.getInt("kills");
        this.deaths = (byte) object.getInt("deaths");
        this.assists = (byte) object.getInt("assists");
        this.champLevel = (byte) object.getInt("champLevel");
        JSONObject perks = object.getJSONObject("perks");
        JSONArray styles = perks.getJSONArray("styles");
        Map<Byte, JSONObject> map = new HashMap<>();
        for (int i = 0; i < styles.length(); i++) {
            JSONObject style = styles.getJSONObject(i);
            int index = style.getString("description").equals("primaryStyle") ? 0 : 1;
            map.put((byte) index, style);
        }
        this.primary = (short) map.get((byte) 0).getJSONArray("selections").getJSONObject(0).getInt("perk");
        this.secondary = (short) map.get((byte) 1).getInt("style");
        this.wards = (short) object.getInt("visionWardsBoughtInGame");
        for (int i = 0; i < items.length; i++) {
            items[i] = object.getInt("item" + i);
        }
    }

    public boolean isWon() {
        return won;
    }

    public String getPUUID() {
        return puuid;
    }

    public String getName() {
        return name;
    }

    public short getChampionId() {
        return championId;
    }

    public short getTeamId() {
        return teamId;
    }

    public short getSpell1Id() {
        return spell1Id;
    }

    public short getSpell2Id() {
        return spell2Id;
    }

    public short getWards() {
        return wards;
    }

    public short getPrimary() {
        return primary;
    }

    public short getSecondary() {
        return secondary;
    }

    public byte getKills() {
        return kills;
    }

    public byte getDeaths() {
        return deaths;
    }

    public byte getAssists() {
        return assists;
    }

    public byte getChampLevel() {
        return champLevel;
    }

    public int getItem(int i) {
        return items[i];
    }
}
