package com.games.theory.tictactoe.model;

import java.util.List;

public record WinningSequence(String mark, List<BoardCoordinate> coordinates) {
  public WinningSequence {
    if (mark == null || mark.isBlank()) {
      throw new IllegalArgumentException("Winning sequence mark is required");
    }
    coordinates = List.copyOf(coordinates);
    if (coordinates.size() < 2) {
      throw new IllegalArgumentException("Winning sequence requires at least two coordinates");
    }
  }
}
