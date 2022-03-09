package com.hawolt.bulk.exception;

/**
 * Created: 04/03/2022 21:14
 * Author: Twitter @hawolt
 **/

public class PermitExpiredException extends Exception {

    private final long expiredAt;

    public PermitExpiredException(long expiredAt) {
        this.expiredAt = expiredAt;
    }

    public long getExpiredAt() {
        return expiredAt;
    }
}
