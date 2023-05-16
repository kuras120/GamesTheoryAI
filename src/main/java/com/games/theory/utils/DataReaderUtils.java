package com.games.theory.utils;

import com.google.common.io.Resources;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@UtilityClass
public class DataReaderUtils {
    public static File getScript(String script) {
        return new File(Resources.getResource(script).getPath());
    }

    public static Map<String, String> readModel(String model) {
        try {
            List<String> text = Resources.readLines(Resources.getResource(model), StandardCharsets.UTF_8);
            Map<String, String> map = new HashMap<>();
            for (var line : text) {
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
