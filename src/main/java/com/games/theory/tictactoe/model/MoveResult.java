package com.games.theory.tictactoe.model;

import java.util.Map;

public record MoveResult(
    BoardCoordinate coordinate,
    String mark,
    ScoringResult scoring,
    Map<String, Integer> totalPoints,
    String nextMark
) {
  public MoveResult {
    totalPoints = Map.copyOf(totalPoints);
  }
}
