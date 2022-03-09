package com.hawolt.external;

import com.hawolt.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * Created: 05/03/2022 22:51
 * Author: Twitter @hawolt
 **/

public class LocalDataResource extends DataResource<String, Integer> {

    public LocalDataResource(String location) {
        Path path = Paths.get(location);
        try {
            List<String> list = Files.readAllLines(path);
            for (String line : list) {
                String[] data = line.split("=");
                mapping.put(data[0], Integer.parseInt(data[1]));
            }
        } catch (IOException e) {
            Logger.error(e);
        }
    }

    @Override
    public Integer getValue(String s) {
        return map.getOrDefault(s, -1);
    }
}
