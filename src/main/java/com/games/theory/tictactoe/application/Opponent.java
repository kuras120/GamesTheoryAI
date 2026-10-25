package com.games.theory.tictactoe.application;

import com.games.theory.tictactoe.model.GameSnapshot;

public interface Opponent {
  OpponentAvailability prepare();
  OpponentMove chooseMove(GameSnapshot snapshot);
  void disable();
}
