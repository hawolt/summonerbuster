package com.hawolt.riot;

import com.hawolt.bulk.Account;

/**
 * Created: 07/03/2022 18:08
 * Author: Twitter @hawolt
 **/

public interface NotificationInterface {
    void ready(SessionAccount account);

    void onError(Exception e);
}
