package com.hawolt.sql.impl;

import com.hawolt.sql.Backbone;
import com.hawolt.sql.Insertion;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Created: 04/03/2022 16:27
 * Author: Twitter @hawolt
 **/

public class MatchInsert implements Insertion {
    private final long gameId, gameCreation;
    private final int region, gameDuration, queueId;

    public MatchInsert(long gameId, int region, long gameCreation, int gameDuration, int queueId) {
        this.gameDuration = gameDuration;
        this.gameCreation = gameCreation;
        this.queueId = queueId;
        this.region = region;
        this.gameId = gameId;
    }

    @Override
    public void insert() throws SQLException {
        try (Connection connection = Backbone.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("INSERT INTO MATCHES VALUES(?,?,?,?,?)")) {
                statement.setLong(1, gameId);
                statement.setInt(2, region);
                statement.setLong(3, gameCreation);
                statement.setInt(4, gameDuration);
                statement.setInt(5, queueId);
                statement.execute();
            }
        }
    }
}
