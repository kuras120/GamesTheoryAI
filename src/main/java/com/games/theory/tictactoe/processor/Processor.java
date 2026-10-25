package com.games.theory.tictactoe.processor;

import com.games.theory.tictactoe.model.GameCell;
import com.games.theory.tictactoe.model.WinningSequence;

import java.util.List;
import java.util.Map;

public interface Processor {
  void process(GameCell cell);
  Map<String, Integer> getPoints();
  List<WinningSequence> getWinningSequences();
  void reset();
}
