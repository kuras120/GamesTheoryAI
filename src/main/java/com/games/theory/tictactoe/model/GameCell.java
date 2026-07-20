package com.games.theory.tictactoe.model;

public final class GameCell {
  private final BoardCoordinate coordinate;
  private String markName = "";
  private boolean checked;

  public GameCell(int column, int row) {
    coordinate = new BoardCoordinate(column, row);
  }

  public BoardCoordinate coordinate() {
    return coordinate;
  }

  public int column() {
    return coordinate.column();
  }

  public int row() {
    return coordinate.row();
  }

  public String markName() {
    return markName;
  }

  public void place(String markName) {
    if (!this.markName.isEmpty()) {
      throw new IllegalStateException("Cell is already occupied: " + coordinate);
    }
    this.markName = markName;
  }

  public boolean checked() {
    return checked;
  }

  public void markChecked() {
    checked = true;
  }

  public void reset() {
    markName = "";
    checked = false;
  }
}
