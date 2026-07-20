package com.games.theory.tictactoe.presentation;

public record CellBounds(double minX, double minY, double width, double height) {
  public double maxX() {
    return minX + width;
  }

  public double maxY() {
    return minY + height;
  }

  public double centerX() {
    return minX + width / 2;
  }

  public double centerY() {
    return minY + height / 2;
  }
}
