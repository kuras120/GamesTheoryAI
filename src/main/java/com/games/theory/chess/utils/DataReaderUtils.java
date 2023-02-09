package com.games.theory.chess.utils;

import com.google.common.io.Resources;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@UtilityClass
public class DataReaderUtils {

    public static Map<String, String> readModel(String model) {
        try {
            String text = Resources.toString(Resources.getResource(model), StandardCharsets.UTF_8);
            Map<String, String> map = new HashMap<>();
            String[] lines = text.split("\n");
            for (var line : lines) {
                var keyValue = line.split(":");
                map.put(keyValue[0], keyValue[1]);
            }
            return map;
        }
        catch (Exception ex) {
            log.error("{}", ex.getMessage());
            return Collections.emptyMap();
        }
    }
}
