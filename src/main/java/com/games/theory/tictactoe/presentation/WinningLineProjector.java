package com.games.theory.tictactoe.presentation;

import com.games.theory.tictactoe.model.BoardCoordinate;
import com.games.theory.tictactoe.model.WinningSequence;

import java.util.Comparator;
import java.util.Map;

public final class WinningLineProjector {
  public LineSegment project(WinningSequence sequence, Map<BoardCoordinate, CellBounds> cells) {
    var coordinates = sequence.coordinates();
    int minColumn = coordinates.stream().mapToInt(BoardCoordinate::column).min().orElseThrow();
    int maxColumn = coordinates.stream().mapToInt(BoardCoordinate::column).max().orElseThrow();
    int minRow = coordinates.stream().mapToInt(BoardCoordinate::row).min().orElseThrow();
    int maxRow = coordinates.stream().mapToInt(BoardCoordinate::row).max().orElseThrow();

    if (minRow == maxRow) {
      CellBounds left = bounds(cells, new BoardCoordinate(minColumn, minRow));
      CellBounds right = bounds(cells, new BoardCoordinate(maxColumn, minRow));
      return new LineSegment(left.centerX(), left.centerY(), right.centerX(), right.centerY());
    }
    if (minColumn == maxColumn) {
      CellBounds top = bounds(cells, new BoardCoordinate(minColumn, minRow));
      CellBounds bottom = bounds(cells, new BoardCoordinate(minColumn, maxRow));
      return new LineSegment(top.centerX(), top.centerY(), bottom.centerX(), bottom.centerY());
    }
    if (maxColumn - minColumn != maxRow - minRow) {
      throw new IllegalArgumentException("Winning sequence is not a straight line: " + coordinates);
    }

    BoardCoordinate leftCoordinate = coordinates.stream()
        .min(Comparator.comparingInt(BoardCoordinate::column))
        .orElseThrow();
    BoardCoordinate rightCoordinate = coordinates.stream()
        .max(Comparator.comparingInt(BoardCoordinate::column))
        .orElseThrow();
    CellBounds left = bounds(cells, leftCoordinate);
    CellBounds right = bounds(cells, rightCoordinate);
    return new LineSegment(left.centerX(), left.centerY(), right.centerX(), right.centerY());
  }

  private CellBounds bounds(Map<BoardCoordinate, CellBounds> cells, BoardCoordinate coordinate) {
    CellBounds bounds = cells.get(coordinate);
    if (bounds == null) {
      throw new IllegalArgumentException("Missing cell geometry for " + coordinate);
    }
    return bounds;
  }
}
