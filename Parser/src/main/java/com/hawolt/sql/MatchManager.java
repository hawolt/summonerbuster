package com.hawolt.sql;

import com.hawolt.bulk.Region;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created: 04/03/2022 16:49
 * Author: Twitter @hawolt
 **/

public class MatchManager {

    public static long getLastGameID(Region region) throws SQLException {
        try (Connection connection = Backbone.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("SELECT GAME_ID FROM MATCHES WHERE REGION=? ORDER BY GAME_ID DESC LIMIT 1")) {
                statement.setInt(1, region.ordinal());
                try (ResultSet set = statement.executeQuery()) {
                    if (set.next()) {
                        return set.getLong(1);
                    } else {
                        return 0L;
                    }
                }
            }
        }
    }

    public static long getFirstGameID(Region region) throws SQLException {
        try (Connection connection = Backbone.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("SELECT GAME_ID FROM MATCHES WHERE REGION=? ORDER BY GAME_ID ASC LIMIT 1")) {
                statement.setInt(1, region.ordinal());
                try (ResultSet set = statement.executeQuery()) {
                    if (set.next()) {
                        return set.getLong(1);
                    } else {
                        return 0L;
                    }
                }
            }
        }
    }
}
