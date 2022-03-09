package com.hawolt.http.handler;

import com.hawolt.Logger;
import com.hawolt.http.Server;
import com.hawolt.http.handler.query.Argument;
import com.hawolt.http.handler.query.Pair;
import com.hawolt.http.handler.query.Value;
import com.hawolt.sql.InfoManager;
import com.hawolt.sql.QueryManager;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;

import java.util.LinkedList;
import java.util.List;

/**
 * Created: 06/03/2022 17:33
 * Author: Twitter @hawolt
 **/

public class QueryHandler implements Handler {
    @Override
    public void handle(@NotNull Context context) throws Exception {
        String parameters = context.queryString();
        if (parameters == null) {
            context.result(Server.jsonify("NOT_ENOUGH_SEARCH_ARGUMENTS"));
        } else {
            String[] arguments = parameters.split("&");
            List<Pair<Value, Argument>> list = new LinkedList<>();
            for (int i = 0; i < arguments.length; i++) {
                String argument = arguments[i];
                String[] data = argument.split("=");
                if (data.length != 2) continue;
                Value value = Value.findByName(data[0]);
                list.add(Pair.of(value, Argument.get(i + 1, data[1].replaceAll("%20", " ").replaceAll("%27", "'"))));
            }
            if (list.size() < 3) {
                context.result(Server.jsonify("NOT_ENOUGH_SEARCH_ARGUMENTS"));
            } else {
                try {
                    StringBuilder builder = new StringBuilder("SELECT M.REGION,I.NAME,I.PUUID,I.GAME_ID,M.GAME_CREATION FROM INFO I INNER JOIN MATCHES M ON I.GAME_ID=M.GAME_ID WHERE");
                    for (int i = 0; i < list.size(); i++) {
                        Value value = list.get(i).getK();
                        if (i > 0) builder.append(" AND");
                        builder.append(" ").append(value.getTable()).append(".").append(value.name()).append("=?");
                    }
                    String query = builder.append(" ").append("ORDER BY GAME_CREATION DESC").toString();

                    JSONArray array = InfoManager.find(query, list);
                    QueryManager.increment();
                    context.result(array.toString());
                } catch (Exception e) {
                    Logger.error(e);
                    context.result(Server.jsonify(e.getMessage()));
                }
            }
        }
    }
}
