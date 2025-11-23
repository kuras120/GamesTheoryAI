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
    public File getScript(String script) {
        return new File(Resources.getResource(script).getPath());
    }

    public File getScript(FileType fileType) {
        String path = determinePathByOS(fileType);
        return new File(Resources.getResource(path).getPath());
    }

    public Map<String, String> readModel(String model) {
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

    private String determinePathByOS(FileType fileType) {
        String os = System.getProperty("os.name").toLowerCase();
        switch (os) {
            case String s when s.contains("win") -> {
                return "venv/Scripts/" + fileType.getCommand() + ".exe";
            }
            case String s when s.contains("mac") -> {
                return "venv/bin/" + fileType.getCommand();
            }
            default -> throw new IllegalStateException("Unexpected value: " + os);
        }
    }
}
