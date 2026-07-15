package com.games.theory.utils;

import com.google.common.io.Resources;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@UtilityClass
public class DataReaderUtils {
    public String readResource(String resource) throws IOException {
        try (InputStream input = openResource(resource)) {
            return new String(input.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    public void copyResource(String resource, Path destination) throws IOException {
        try (InputStream input = openResource(resource)) {
            Path parent = destination.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            Files.copy(input, destination, StandardCopyOption.REPLACE_EXISTING);
        }
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

    private InputStream openResource(String resource) throws IOException {
        InputStream input = DataReaderUtils.class.getClassLoader().getResourceAsStream(resource);
        if (input == null) {
            throw new IOException("Resource not found: " + resource);
        }
        return input;
    }
}
