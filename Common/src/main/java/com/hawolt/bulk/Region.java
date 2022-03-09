package com.hawolt.bulk;

/**
 * Created: 03/03/2022 15:42
 * Author: Twitter @hawolt
 **/

public enum Region {
    EUW("EUW1"),
    NA("NA1"),
    EUNE("EUN1", "EUN"),
    OC1("OC1", "OC", "OCE"),
    LA1("LA1", "LAN"),
    BR("BR1"),
    TR("TR1"),
    RU("RU"),
    JP("JP1"),
    KR("KR"),
    LA2("LA2", "LAS"),
    PBE("PBE1");
    private final String platform;
    private final String[] names;

    Region(String platform, String... names) {
        this.platform = platform;
        this.names = names;
    }

    public String[] getNames() {
        return names;
    }

    public String getPlatform() {
        return platform;
    }

    public static String getHost(Region region) {
        switch (region) {
            case NA:
            case LA1:
            case LA2:
            case OC1:
            case BR:
                return "usw.pp.riotgames.com";
            case EUW:
            case EUNE:
            case TR:
            case RU:
                return "euc.pp.riotgames.com";
            case JP:
            case KR:
                return "apne.pp.riotgames.com";
        }
        return null;
    }

    private static final Region[] REGIONS = Region.values();

    public static Region findByOrdinal(int ordinal) {
        return REGIONS[ordinal];
    }


    public static Region findByName(String name) {
        for (Region region : REGIONS) {
            if (region.name().equalsIgnoreCase(name)) {
                return region;
            }
            if (region.platform.equalsIgnoreCase(name)) {
                return region;
            }
            for (String tmp : region.getNames()) {
                if (tmp.equalsIgnoreCase(name)) {
                    return region;
                }
            }
        }
        return null;
    }
}
