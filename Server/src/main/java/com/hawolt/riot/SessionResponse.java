package com.hawolt.riot;

import com.hawolt.bulk.exception.PermitExpiredException;

/**
 * Created: 07/03/2022 18:05
 * Author: Twitter @hawolt
 **/

public interface SessionResponse {
    void onError(PermitExpiredException e);

    void onRefresh(String response, long last);

    void onReady();
}
