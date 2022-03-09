package com.hawolt.bulk;

/**
 * Created: 04/03/2022 16:52
 * Author: Twitter @hawolt
 **/

public enum Token {
    ACCESS("access_token"), ID("id_token");
    private final String name;

    Token(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
