package com.games.theory.tictactoe.model;

import java.util.List;

public record GameSnapshot(int pointsX, int pointsO, List<String> board) {
  public GameSnapshot {
    board = List.copyOf(board);
  }
}
