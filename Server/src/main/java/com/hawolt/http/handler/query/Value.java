package com.hawolt.http.handler.query;

import com.hawolt.Logger;

/**
 * Created: 04/03/2022 18:30
 * Author: Twitter @hawolt
 **/

public enum Value {
    GAME_DURATION("M"),
    GAME_ID("I"),
    CHAMP_ID("I"),
    WINNER("I"),
    SPELL1_ID("I"),
    SPELL2_ID("I"),
    PRIMARY_RUNE("I"),
    SECONDARY_RUNE("I"),
    KILLS("I"),
    DEATHS("I"),
    ASSISTS("I"),
    LEVEL("I"),
    MINIONS("I"),
    TEAM("I"),
    ITEM_0("I"),
    ITEM_1("I"),
    ITEM_2("I"),
    ITEM_3("I"),
    ITEM_4("I"),
    ITEM_5("I"),
    ITEM_6("I"),
    WARDS("I"),
    NAME("I"),
    GOLD("I"),
    TOTAL_DAMAGE("I"),
    PUUID("I");
    private final String table;

    Value(String table) {
        this.table = table;
    }

    public String getTable() {
        return table;
    }

    public static final Value[] VALUES = Value.values();

    public static Value findByName(String name) {
        for (Value value : VALUES) {
            if (value.name().equalsIgnoreCase(name)) {
                return value;
            }
        }
        Logger.debug("NO_MATCH {}", name);
        return null;
    }
}
