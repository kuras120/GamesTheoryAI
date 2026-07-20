package com.games.theory.tictactoe.integration;

import com.games.theory.tictactoe.application.Opponent;
import com.games.theory.tictactoe.application.OpponentAvailability;
import com.games.theory.tictactoe.application.OpponentMove;
import com.games.theory.tictactoe.exception.AiException;
import com.games.theory.tictactoe.model.GameSnapshot;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class ExternalOpponent implements Opponent {
  private final PythonExecutor executor;
  private final PythonRuntimeManager runtimeManager;
  private volatile PythonRuntime runtime;

  @Override
  public OpponentAvailability prepare() {
    PythonRuntimeResult result = runtimeManager.prepare();
    runtime = result.runtime();
    return new OpponentAvailability(result.isAvailable(), result.statusMessage());
  }

  @Override
  public OpponentMove chooseMove(GameSnapshot snapshot) {
    PythonRuntime readyRuntime = runtime;
    if (readyRuntime == null) {
      throw new AiException("Opponent runtime is unavailable");
    }
    AiMove move = executor.processState(
        readyRuntime,
        Integer.toString(snapshot.pointsX()),
        Integer.toString(snapshot.pointsO()),
        snapshot.board().toArray(String[]::new)
    );
    return new OpponentMove(move.x(), move.y());
  }

  @Override
  public void disable() {
    runtime = null;
  }
}
