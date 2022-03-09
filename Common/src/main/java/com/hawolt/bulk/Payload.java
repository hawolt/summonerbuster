package com.hawolt.bulk;

/**
 * Created: 04/03/2022 19:33
 * Author: Twitter @hawolt
 **/

public class Payload {

    public static String getPost() {
        return "{\"acr_values\":\"urn:riot:bronze\",\"claims\":\"\",\"client_id\":\"lol\",\"nonce\":\"oYnVwCSrlS5IHKh7iI17oQ\",\"redirect_uri\":\"http://localhost/redirect\",\"response_type\":\"token id_token\",\"scope\":\"openid link ban lol_region\"}";
    }

    public static String getPut(String username, String password) {
        String tmp = "{\"type\":\"auth\",\"username\":\"%s\",\"password\":\"%s\",\"remember\":false,\"language\":\"en_GB\",\"region\":null}";
        return String.format(tmp, username, password);
    }
}
