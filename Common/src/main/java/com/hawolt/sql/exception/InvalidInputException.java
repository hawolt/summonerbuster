package com.hawolt.sql.exception;

/**
 * Created: 04/03/2022 14:39
 * Author: Twitter @hawolt
 **/

public class InvalidInputException extends Exception {
    private final String name;

    public InvalidInputException(String type, String name) {
        super("Unknown " + type + " " + name);
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
