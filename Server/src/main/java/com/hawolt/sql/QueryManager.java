package com.hawolt.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created: 04/03/2022 18:23
 * Author: Twitter @hawolt
 **/

public class QueryManager {
    public static long getQueries() throws SQLException {
        try (Connection connection = Backbone.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("SELECT TOTAL FROM QUERIES")) {
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

    public static void increment() throws SQLException {
        try (Connection connection = Backbone.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("UPDATE QUERIES SET TOTAL=TOTAL+1")) {
                statement.execute();
            }
        }
    }
}
