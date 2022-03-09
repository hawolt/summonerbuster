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

    public static long getFirstGame(Region region) throws SQLException {
        try (Connection connection = Backbone.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("SELECT MIN(GAME_CREATION) FROM MATCHES WHERE REGION=?")) {
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

    public static long getLastGame(Region region) throws SQLException {
        try (Connection connection = Backbone.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("SELECT MAX(GAME_CREATION) FROM MATCHES WHERE REGION=?")) {
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

    public static long getGames(Region region) throws SQLException {
        try (Connection connection = Backbone.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("SELECT COUNT(*) FROM MATCHES WHERE REGION=?")) {
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

    public static long getTotalGames() throws SQLException {
        try (Connection connection = Backbone.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("SELECT COUNT(*) FROM MATCHES")) {
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
