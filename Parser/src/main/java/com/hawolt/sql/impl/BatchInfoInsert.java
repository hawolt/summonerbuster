package com.hawolt.sql.impl;

import com.hawolt.core.data.Player;
import com.hawolt.sql.Backbone;
import com.hawolt.sql.Insertion;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Created: 04/03/2022 16:27
 * Author: Twitter @hawolt
 **/

public class BatchInfoInsert implements Insertion {

    private final Player[] players;
    private final long matchId;
    private final byte region;

    public BatchInfoInsert(long matchId, byte region, Player[] players) {
        this.players = players;
        this.matchId = matchId;
        this.region = region;
    }

    @Override
    public void insert() throws SQLException {
        try (Connection connection = Backbone.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("INSERT INTO INFO VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)")) {
                for (Player player : players) {
                    String name = player.getWrapper().getName();
                    statement.setLong(1, matchId);
                    statement.setInt(2, region);
                    statement.setString(3, player.getParticipant().getPUUID());
                    statement.setInt(4, player.getWrapper().getChampionId());
                    statement.setInt(5, player.getWrapper().getSpell1Id());
                    statement.setInt(6, player.getWrapper().getSpell2Id());
                    statement.setInt(7, player.getWrapper().getPrimary());
                    statement.setInt(8, player.getWrapper().getSecondary());
                    statement.setInt(9, player.getWrapper().getKills());
                    statement.setInt(10, player.getWrapper().getDeaths());
                    statement.setInt(11, player.getWrapper().getAssists());
                    statement.setInt(12, player.getWrapper().getChampLevel());
                    statement.setInt(13, player.getParticipant().getCreepScore());
                    statement.setInt(14, player.getWrapper().getTeamId());
                    statement.setInt(15, player.getWrapper().getItem(0));
                    statement.setInt(16, player.getWrapper().getItem(1));
                    statement.setInt(17, player.getWrapper().getItem(2));
                    statement.setInt(18, player.getWrapper().getItem(3));
                    statement.setInt(19, player.getWrapper().getItem(4));
                    statement.setInt(20, player.getWrapper().getItem(5));
                    statement.setInt(21, player.getWrapper().getItem(6));
                    statement.setInt(22, player.getWrapper().getWards());
                    statement.setBoolean(23, player.getWrapper().isWon());
                    statement.setInt(24, player.getParticipant().getTotalGold());
                    statement.setInt(25, player.getParticipant().getTotalDamageDone());
                    statement.setString(26, name.length() > 16 ? "" : name);
                    statement.addBatch();
                }
                statement.executeBatch();
            }
        }
    }
}
