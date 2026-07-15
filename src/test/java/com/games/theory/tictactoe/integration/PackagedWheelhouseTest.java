package com.games.theory.tictactoe.integration;

import com.games.theory.utils.DataReaderUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.util.HexFormat;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PackagedWheelhouseTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    void packagedManifestReferencesAvailableWheelsWithMatchingHashes() throws Exception {
        String resourceRoot = WheelhouseInstaller.WHEELHOUSE_RESOURCE + "/";
        WheelhouseManifest manifest = WheelhouseManifest.parse(
            DataReaderUtils.readResource(resourceRoot + WheelhouseInstaller.MANIFEST_FILENAME)
        );

        for (WheelhouseManifest.WheelArtifact wheel : manifest.wheels()) {
            Path destination = temporaryDirectory.resolve(wheel.filename());
            DataReaderUtils.copyResource(resourceRoot + wheel.filename(), destination);
            assertEquals(wheel.sha256(), sha256(destination));
        }
    }

    private static String sha256(Path file) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        try (InputStream input = Files.newInputStream(file)) {
            byte[] buffer = new byte[8192];
            int read;
            while ((read = input.read(buffer)) != -1) {
                digest.update(buffer, 0, read);
            }
        }
        return HexFormat.of().formatHex(digest.digest());
    }
}
