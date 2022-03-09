package com.hawolt.bulk;

/**
 * Created: 03/03/2022 15:30
 * Author: Twitter @hawolt
 **/

public interface Notifier {
    void ready(Account account);

    void onError(Account account, Exception e);
}
