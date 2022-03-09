package com.hawolt.bulk;

/**
 * Created: 04/03/2022 10:19
 * Author: Twitter @hawolt
 **/

public class Detail {
    private final String username, password;

    private Detail(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public static Detail generate(String username, String password) {
        return new Detail(username, password);
    }

    public static Detail generate(String line) {
        String[] data = line.split(":", 2);
        String username = data[0];
        String password = data[1];
        return new Detail(username, password);
    }
}
