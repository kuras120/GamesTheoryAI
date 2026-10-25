package com.games.theory.tictactoe.model;

import com.games.theory.tictactoe.exception.GameException;
import com.games.theory.tictactoe.processor.ProcessorExecutor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class GameSession {
  private final int size;
  private final ProcessorExecutor processorExecutor;
  private final List<GameCell> cells;
  private final Map<String, Integer> points = new HashMap<>();
  private String nextMark;

  public GameSession(int size, ProcessorExecutor processorExecutor) {
    if (size < 1) {
      throw new IllegalArgumentException("Board size must be positive");
    }
    this.size = size;
    this.processorExecutor = processorExecutor;
    cells = new ArrayList<>(size * size);
    for (int row = 0; row < size; row++) {
      for (int column = 0; column < size; column++) {
        cells.add(new GameCell(column, row));
      }
    }
    reset();
  }

  public int size() {
    return size;
  }

  public MoveResult placeMove(BoardCoordinate coordinate) {
    GameCell cell = cellAt(coordinate);
    if (!cell.markName().isEmpty()) {
      throw new GameException("Cell is already occupied: " + coordinate);
    }

    String mark = nextMark;
    cell.place(mark);
    ScoringResult scoring = processorExecutor.execute(cells).collect();
    scoring.awardedPoints().forEach((player, awarded) -> points.merge(player, awarded, Integer::sum));
    nextMark = "X".equals(mark) ? "O" : "X";
    return new MoveResult(coordinate, mark, scoring, points, nextMark);
  }

  public boolean isEmpty(BoardCoordinate coordinate) {
    return cellAt(coordinate).markName().isEmpty();
  }

  public String nextMark() {
    return nextMark;
  }

  public Map<String, Integer> points() {
    return Map.copyOf(points);
  }

  public GameSnapshot snapshot() {
    List<String> board = cells.stream()
        .map(GameCell::markName)
        .toList();
    return new GameSnapshot(points.get("X"), points.get("O"), board);
  }

  public void reset() {
    cells.forEach(GameCell::reset);
    processorExecutor.reset();
    points.clear();
    points.put("X", 0);
    points.put("O", 0);
    nextMark = "X";
  }

  private GameCell cellAt(BoardCoordinate coordinate) {
    if (coordinate == null
        || coordinate.column() < 0
        || coordinate.column() >= size
        || coordinate.row() < 0
        || coordinate.row() >= size) {
      throw new GameException("Cell is outside the board: " + coordinate);
    }
    return cells.get(coordinate.row() * size + coordinate.column());
  }
}
