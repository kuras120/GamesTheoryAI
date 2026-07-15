package com.games.theory.tictactoe;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GameActivityFeedTest {
    @Test
    void formatsMovesAndPointsForPlayer() {
        List<String> entries = new ArrayList<>();
        GameActivityFeed feed = new GameActivityFeed(entries);

        feed.startNewGame();
        feed.addMove("You", "X", 1, 2);
        feed.addMove("AI", "O", 3, 0);
        feed.addPoints("You", 1, 1, 0);
        feed.addPoints("AI", 2, 1, 2);

        assertEquals(List.of(
            "New game started. Score: X 0–0 O.",
            "You placed X at row 2, column 3.",
            "AI placed O at row 4, column 1.",
            "You scored 1 point. Score: X 1–0 O.",
            "AI scored 2 points. Score: X 1–2 O."
        ), entries);
    }

    @Test
    void resetClearsPreviousActivity() {
        List<String> entries = new ArrayList<>(List.of("old event"));
        GameActivityFeed feed = new GameActivityFeed(entries);

        feed.startNewGame();

        assertEquals(List.of("New game started. Score: X 0–0 O."), entries);
    }

    @Test
    void reportsAiFailureWithoutTechnicalDetails() {
        List<String> entries = new ArrayList<>();

        new GameActivityFeed(entries).addAiFailure();

        assertEquals(List.of("AI could not complete its move. Continue manually."), entries);
    }
}
