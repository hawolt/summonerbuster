package com.hawolt.http.handler.query;

/**
 * Created: 04/03/2022 18:34
 * Author: Twitter @hawolt
 **/

public class Argument {
    private final int index;
    private final String value;

    public static Argument get(int index, String value) {
        return new Argument(index, value);
    }

    private Argument(int index, String value) {
        this.value = value;
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    public String getValue() {
        return value;
    }
}
