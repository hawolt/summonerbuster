package com.hawolt.http.handler.profile;

import com.hawolt.bulk.Region;

/**
 * Created: 03/03/2022 15:42
 * Author: Twitter @hawolt
 **/

public enum SummonerProfileRegion {
    EUW(Region.EUW),
    NA(Region.NA),
    EUNE(Region.EUNE),
    OCE(Region.OC1),
    LAN(Region.LA1),
    BR(Region.BR),
    TR(Region.TR),
    RU(Region.RU),
    JP(Region.JP),
    LAS(Region.LA2);
    private final Region region;

    SummonerProfileRegion(Region region) {
        this.region = region;
    }

    public Region getRegion() {
        return region;
    }

    public static final SummonerProfileRegion[] REGIONS = SummonerProfileRegion.values();

}
