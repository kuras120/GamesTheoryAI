package com.games.theory.tictactoe.integration;

import com.games.theory.tictactoe.exception.AiException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class WheelhouseManifestTest {
    private static final String HASH = "a".repeat(64);

    @Test
    void parsesRequirementAndWheels() {
        WheelhouseManifest manifest = WheelhouseManifest.parse(
            "requirement\tgames-theory==0.0.3\n"
                + "wheel\t" + HASH + "\tgames_theory-0.0.3-py3-none-any.whl\n"
        );

        assertEquals("games-theory==0.0.3", manifest.requirement());
        assertEquals(
            new WheelhouseManifest.WheelArtifact("games_theory-0.0.3-py3-none-any.whl", HASH),
            manifest.wheels().getFirst()
        );
    }

    @Test
    void rejectsPathTraversalAndInvalidHash() {
        assertThrows(AiException.class, () -> WheelhouseManifest.parse(
            "requirement\tgames-theory==0.0.3\nwheel\tinvalid\t../package.whl\n"
        ));
    }

    @Test
    void rejectsDuplicateWheelNames() {
        String wheel = "wheel\t" + HASH + "\tpackage-1.0-py3-none-any.whl\n";

        assertThrows(AiException.class, () -> WheelhouseManifest.parse(
            "requirement\tgames-theory==0.0.3\n" + wheel + wheel
        ));
    }
}
