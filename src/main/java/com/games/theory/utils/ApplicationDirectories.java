package com.games.theory.utils;

import java.nio.file.Path;
import java.util.Locale;
import java.util.Map;

public record ApplicationDirectories(Path root, Path runtime, Path data) {
    private static final String APPLICATION_NAME = "GamesTheoryAI";

    public static ApplicationDirectories resolve() {
        return resolve(System.getProperty("os.name"), System.getenv(), System.getProperty("user.home"));
    }

    public static ApplicationDirectories resolve(String osName, Map<String, String> environment, String userHome) {
        String normalizedOs = osName.toLowerCase(Locale.ROOT);
        Path rootDirectory;
        if (normalizedOs.contains("win")) {
            String localAppData = environment.get("LOCALAPPDATA");
            Path base = isBlank(localAppData)
                ? Path.of(userHome, "AppData", "Local")
                : Path.of(localAppData);
            rootDirectory = base.resolve(APPLICATION_NAME);
        } else if (normalizedOs.contains("mac")) {
            rootDirectory = Path.of(userHome, "Library", "Application Support", APPLICATION_NAME);
        } else {
            String xdgDataHome = environment.get("XDG_DATA_HOME");
            Path base = isBlank(xdgDataHome)
                ? Path.of(userHome, ".local", "share")
                : Path.of(xdgDataHome);
            rootDirectory = base.resolve(APPLICATION_NAME);
        }
        return new ApplicationDirectories(
            rootDirectory,
            rootDirectory.resolve("runtime"),
            rootDirectory.resolve("data")
        );
    }

    private static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
