package com.hawolt.http.handler.history;

import com.hawolt.bulk.Region;

/**
 * Created: 06/03/2022 17:44
 * Author: Twitter @hawolt
 **/

public enum RegionalHistoryRegion {
    AMERICA(Region.NA),
    EUROPE(Region.EUW);
    private final Region region;

    RegionalHistoryRegion(Region region) {
        this.region = region;
    }

    public Region getRegion() {
        return region;
    }

    public String getHost() {
        return Region.getHost(region);
    }

    public static final RegionalHistoryRegion[] REGIONS = RegionalHistoryRegion.values();
}