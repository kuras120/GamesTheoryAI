package com.games.theory.tictactoe.model;

import java.util.List;
import java.util.Map;

public record ScoringResult(Map<String, Integer> awardedPoints, List<WinningSequence> winningSequences) {
  public ScoringResult {
    awardedPoints = Map.copyOf(awardedPoints);
    winningSequences = List.copyOf(winningSequences);
  }
}
