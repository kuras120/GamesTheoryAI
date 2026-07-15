package com.games.theory.tictactoe;

import java.util.List;

public class GameActivityFeed {
    private final List<String> entries;

    public GameActivityFeed(List<String> entries) {
        this.entries = entries;
    }

    public void startNewGame() {
        entries.clear();
        entries.add("New game started. Score: X 0–0 O.");
    }

    public void addMove(String participant, String mark, int rowIndex, int columnIndex) {
        entries.add(
            participant + " placed " + mark + " at row " + (rowIndex + 1)
                + ", column " + (columnIndex + 1) + "."
        );
    }

    public void addPoints(String participant, int awardedPoints, int pointsX, int pointsO) {
        String unit = awardedPoints == 1 ? "point" : "points";
        entries.add(
            participant + " scored " + awardedPoints + " " + unit
                + ". Score: X " + pointsX + "–" + pointsO + " O."
        );
    }

    public void addAiFailure() {
        entries.add("AI could not complete its move. Continue manually.");
    }
}
