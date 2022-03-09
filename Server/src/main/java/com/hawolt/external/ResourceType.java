package com.hawolt.external;

/**
 * Created: 05/03/2022 23:09
 * Author: Twitter @hawolt
 **/

public enum ResourceType {
    CHAMPION("https://raw.communitydragon.org/latest/plugins/rcp-be-lol-game-data/global/default/v1/champion-summary.json"),
    ITEM("https://raw.communitydragon.org/latest/plugins/rcp-be-lol-game-data/global/en_gb/v1/items.json"),
    PRIMARY_RUNE("resources/primary.rune"),
    SECONDARY_RUNE("resources/secondary.rune"),
    SPELL("https://raw.communitydragon.org/latest/plugins/rcp-be-lol-game-data/global/en_gb/v1/summoner-spells.json");
    private final String location;

    ResourceType(String location) {
        this.location = location;
    }

    public String getLocation() {
        return location;
    }
}
