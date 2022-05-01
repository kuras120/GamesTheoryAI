package com.games.theory.chess.util;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@UtilityClass
public class DataReader {

    public static Map<String, String> readModel(String model) {
        try (BufferedReader reader = Files.newBufferedReader(Paths.get(model))) {
            Map<String, String> map = new HashMap<>();
            String line = reader.readLine();
            while (line != null) {
                var keyValue = line.split(":");
                map.put(keyValue[0], keyValue[1]);
                line = reader.readLine();
            }
            return map;
        }
        catch (Exception ex) {
            log.error("{}", ex.getMessage());
            return null;
        }
    }
}
