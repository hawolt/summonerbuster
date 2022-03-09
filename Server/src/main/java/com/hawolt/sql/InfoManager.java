package com.hawolt.sql;

import com.hawolt.external.Dragon;
import com.hawolt.external.ResourceType;
import com.hawolt.http.handler.query.Argument;
import com.hawolt.http.handler.query.Pair;
import com.hawolt.http.handler.query.Value;
import com.hawolt.sql.exception.InvalidInputException;
import com.hawolt.sql.model.Result;
import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.*;
import java.util.LinkedList;
import java.util.List;

/**
 * Created: 04/03/2022 18:36
 * Author: Twitter @hawolt
 **/

public class InfoManager {
    public static JSONArray find(String query, List<Pair<Value, Argument>> list) throws SQLException, InvalidInputException {
        try (Connection connection = Backbone.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                int id;
                for (Pair<Value, Argument> pair : list) {
                    Value value = pair.getK();
                    Argument argument = pair.getV();
                    switch (value) {
                        case WINNER:
                            boolean won = "win".equalsIgnoreCase(argument.getValue());
                            statement.setBoolean(argument.getIndex(), won);
                            break;
                        case GOLD:
                        case TOTAL_DAMAGE:
                        case GAME_ID:
                            statement.setLong(argument.getIndex(), Long.parseLong(argument.getValue()));
                            break;
                        case CHAMP_ID:
                            String champion = argument.getValue();
                            id = Dragon.retrieve(ResourceType.CHAMPION, champion.toLowerCase());
                            if (id == -1) throw new InvalidInputException("champion", champion);
                            statement.setInt(argument.getIndex(), id);
                            break;
                        case SPELL1_ID:
                        case SPELL2_ID:
                            String spell = argument.getValue();
                            id = Dragon.retrieve(ResourceType.SPELL, spell.toLowerCase());
                            if (id == -1) throw new InvalidInputException("summoner spell", spell);
                            statement.setInt(argument.getIndex(), id);
                            break;
                        case PRIMARY_RUNE:
                            String rune1 = argument.getValue();
                            id = Dragon.retrieve(ResourceType.PRIMARY_RUNE, rune1.toLowerCase());
                            if (id == -1) throw new InvalidInputException("rune", rune1);
                            statement.setInt(argument.getIndex(), id);
                            break;
                        case SECONDARY_RUNE:
                            String rune2 = argument.getValue();
                            id = Dragon.retrieve(ResourceType.SECONDARY_RUNE, rune2.toLowerCase());
                            if (id == -1) throw new InvalidInputException("secondary", rune2);
                            statement.setInt(argument.getIndex(), id);
                            break;
                        case ITEM_0:
                        case ITEM_1:
                        case ITEM_2:
                        case ITEM_3:
                        case ITEM_4:
                        case ITEM_5:
                        case ITEM_6:
                            String item = argument.getValue();
                            id = Dragon.retrieve(ResourceType.ITEM, item.toLowerCase());
                            if (id == -1) throw new InvalidInputException("item", item);
                            statement.setInt(argument.getIndex(), id);
                            break;
                        case GAME_DURATION:
                        case KILLS:
                        case DEATHS:
                        case ASSISTS:
                        case LEVEL:
                        case MINIONS:
                        case TEAM:
                        case WARDS:
                            statement.setInt(argument.getIndex(), Integer.parseInt(argument.getValue()));
                            break;
                        case PUUID:
                        case NAME:
                            statement.setString(argument.getIndex(), argument.getValue());
                            break;
                    }
                }
                try (ResultSet set = statement.executeQuery()) {
                    LinkedList<Object[]> results = fetchAll(set);
                    if (results.isEmpty()) return new JSONArray();
                    JSONObject[] objects = results.stream().filter(o -> o != null && o.length > 1).map(Result::new).map(Result::toJson).toArray(JSONObject[]::new);
                    JSONArray array = new JSONArray();
                    for (JSONObject object : objects) {
                        array.put(object);
                    }
                    return array;
                }
            }
        }
    }

    private static LinkedList<Object[]> fetchAll(ResultSet set) throws SQLException {
        ResultSetMetaData meta = set.getMetaData();
        final int columns = meta.getColumnCount();
        LinkedList<Object[]> list = new LinkedList<>();
        while (set.next()) {
            Object[] values = new Object[columns];
            for (int i = 1; i <= columns; i++) {
                values[i - 1] = set.getObject(i);
            }
            list.add(values);
        }
        return list;
    }
}
