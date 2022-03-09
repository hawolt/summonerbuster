package com.hawolt.external;

import com.hawolt.external.impl.CommunityDragonResource;
import com.hawolt.external.impl.SummonerSpellResource;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created: 04/03/2022 14:33
 * Author: Twitter @hawolt
 **/

public class Dragon {

    private final static Map<ResourceType, DataResource<String, Integer>> RESOURCE_MAP = new HashMap<>();

    public static JSONObject mapping = new JSONObject();

    static {
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
            JSONObject mapping = new JSONObject();
            for (ResourceType type : ResourceType.values()) {
                mapping.put(type.name(), RESOURCE_MAP.get(type).getMapping());
            }
            Dragon.mapping = mapping;
        }, 1, TimeUnit.MINUTES.toMillis(60), TimeUnit.MINUTES);
    }

    public static void awake() {
        RESOURCE_MAP.put(ResourceType.SPELL, new SummonerSpellResource(ResourceType.SPELL.getLocation()));
        RESOURCE_MAP.put(ResourceType.CHAMPION, new CommunityDragonResource(ResourceType.CHAMPION.getLocation()));
        RESOURCE_MAP.put(ResourceType.ITEM, new CommunityDragonResource(ResourceType.ITEM.getLocation()));
        RESOURCE_MAP.put(ResourceType.PRIMARY_RUNE, new LocalDataResource(ResourceType.PRIMARY_RUNE.getLocation()));
        RESOURCE_MAP.put(ResourceType.SECONDARY_RUNE, new LocalDataResource(ResourceType.SECONDARY_RUNE.getLocation()));
    }

    public static int retrieve(ResourceType type, String in) {
        return RESOURCE_MAP.get(type).getValue(in.toLowerCase());
    }
}
