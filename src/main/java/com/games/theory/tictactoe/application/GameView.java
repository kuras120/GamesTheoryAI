package com.games.theory.tictactoe.application;

import com.games.theory.tictactoe.model.BoardCoordinate;
import com.games.theory.tictactoe.model.WinningSequence;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public interface GameView {
  void initializeBoard(int size, Consumer<BoardCoordinate> moveHandler);
  void resetBoard();
  void showMove(BoardCoordinate coordinate, String mark);
  void showPoints(Map<String, Integer> points);
  void showWinningSequences(List<WinningSequence> sequences);
  void setBoardDisabled(boolean disabled);
  boolean isOpponentSelected();
  void setOpponentAvailability(boolean available, String statusMessage);
  void startNewGame();
  void addMove(String participant, String mark, BoardCoordinate coordinate);
  void addPoints(String participant, int awardedPoints, Map<String, Integer> totalPoints);
  void addOpponentFailure();
}
