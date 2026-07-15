package com.games.theory.tictactoe.integration;

import com.games.theory.tictactoe.exception.AiException;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

record WheelhouseManifest(String requirement, List<WheelArtifact> wheels) {
    private static final Pattern SHA_256 = Pattern.compile("[a-f0-9]{64}");
    private static final Pattern REQUIREMENT = Pattern.compile("[A-Za-z0-9_.-]+==[A-Za-z0-9_.+!-]+");

    WheelhouseManifest {
        wheels = List.copyOf(wheels);
    }

    static WheelhouseManifest parse(String content) {
        String requirement = null;
        List<WheelArtifact> wheels = new ArrayList<>();
        Set<String> filenames = new HashSet<>();
        for (String line : content.lines().toList()) {
            if (line.isBlank()) {
                continue;
            }
            String[] fields = line.split("\\t", -1);
            if (fields.length == 2 && "requirement".equals(fields[0])) {
                if (requirement != null || !REQUIREMENT.matcher(fields[1]).matches()) {
                    throw invalidManifest();
                }
                requirement = fields[1];
            } else if (fields.length == 3 && "wheel".equals(fields[0])) {
                String filename = fields[2];
                if (!SHA_256.matcher(fields[1]).matches()
                    || !isSafeFilename(filename)
                    || !filenames.add(filename)) {
                    throw invalidManifest();
                }
                wheels.add(new WheelArtifact(filename, fields[1]));
            } else {
                throw invalidManifest();
            }
        }
        if (requirement == null || wheels.isEmpty()) {
            throw invalidManifest();
        }
        return new WheelhouseManifest(requirement, wheels);
    }

    private static boolean isSafeFilename(String filename) {
        Path path = Path.of(filename);
        return filename.endsWith(".whl")
            && !filename.contains("/")
            && !filename.contains("\\")
            && path.getNameCount() == 1
            && path.getFileName().toString().equals(filename);
    }

    private static AiException invalidManifest() {
        return new AiException("Invalid Python wheelhouse manifest");
    }

    record WheelArtifact(String filename, String sha256) {
    }
}
