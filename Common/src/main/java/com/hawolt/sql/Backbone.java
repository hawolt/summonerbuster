package com.hawolt.sql;

import com.hawolt.JsonSource;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Created: 04/03/2022 12:33
 * Author: Twitter @hawolt
 **/

public class Backbone {

    private static HikariDataSource source;

    public static void setup(JsonSource source, int pool) {
        HikariConfig hikari = new HikariConfig();
        hikari.setUsername(source.get("database.user"));
        hikari.setPassword(source.get("database.password"));
        String connection = String.format("jdbc:postgresql://%s:%s/%s", source.get("database.host"), source.get("database.port"), source.get("database.database"));
        hikari.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        hikari.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        hikari.addDataSourceProperty("prepStmtCacheSize", "250");
        hikari.addDataSourceProperty("cachePrepStmts", "true");
        hikari.addDataSourceProperty("port", source.get("database.port"));
        hikari.setConnectionTimeout(10000);
        hikari.setMaximumPoolSize(pool);
        hikari.setJdbcUrl(connection);
        Backbone.source = new HikariDataSource(hikari);
    }

    public static Connection getConnection() throws SQLException {
        return source.getConnection();
    }
}
