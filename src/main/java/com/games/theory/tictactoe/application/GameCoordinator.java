package com.games.theory.tictactoe.application;

import com.games.theory.tictactoe.exception.AiException;
import com.games.theory.tictactoe.exception.GameException;
import com.games.theory.tictactoe.model.BoardCoordinate;
import com.games.theory.tictactoe.model.GameSession;
import com.games.theory.tictactoe.model.MoveResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Executor;

@Slf4j
@RequiredArgsConstructor
public final class GameCoordinator {
  private final GameSession session;
  private final GameView view;
  private final Opponent opponent;
  private final Executor backgroundExecutor;
  private final Executor presentationExecutor;
  private long generation;
  private boolean opponentAvailable;

  public void initialize() {
    view.initializeBoard(session.size(), this::playHumanMove);
    reset();
    prepareOpponent();
  }

  public void reset() {
    generation++;
    session.reset();
    view.resetBoard();
    view.showPoints(session.points());
    view.setBoardDisabled(false);
    view.startNewGame();
  }

  public void playHumanMove(BoardCoordinate coordinate) {
    if (!session.isEmpty(coordinate)) {
      return;
    }
    boolean opponentMode = view.isOpponentSelected();
    MoveResult result = session.placeMove(coordinate);
    String participant = opponentMode && "X".equals(result.mark())
        ? "You"
        : "Player " + result.mark();
    applyMove(result, participant);
    if (opponentMode && opponentAvailable && "O".equals(result.nextMark())) {
      requestOpponentMove();
    }
  }

  private void prepareOpponent() {
    view.setOpponentAvailability(false, "Preparing AI…");
    backgroundExecutor.execute(() -> {
      OpponentAvailability availability = opponent.prepare();
      presentationExecutor.execute(() -> {
        opponentAvailable = availability.available();
        view.setOpponentAvailability(availability.available(), availability.statusMessage());
      });
    });
  }

  private void requestOpponentMove() {
    view.setBoardDisabled(true);
    long requestedGeneration = generation;
    var snapshot = session.snapshot();
    backgroundExecutor.execute(() -> {
      try {
        OpponentMove move = opponent.chooseMove(snapshot);
        presentationExecutor.execute(() -> applyOpponentMove(requestedGeneration, move));
      } catch (AiException exception) {
        log.error("Unable to calculate opponent move", exception);
        presentationExecutor.execute(() -> handleOpponentFailure(requestedGeneration, exception));
      }
    });
  }

  private void applyOpponentMove(long requestedGeneration, OpponentMove move) {
    if (requestedGeneration != generation) {
      return;
    }
    try {
      MoveResult result = session.placeMove(new BoardCoordinate(move.column(), move.row()));
      if (!"O".equals(result.mark())) {
        throw new AiException("Opponent move was requested outside the O turn");
      }
      view.setBoardDisabled(false);
      applyMove(result, "AI");
    } catch (GameException | AiException exception) {
      handleOpponentFailure(requestedGeneration, exception);
    }
  }

  private void applyMove(MoveResult result, String participant) {
    view.showMove(result.coordinate(), result.mark());
    view.addMove(participant, result.mark(), result.coordinate());
    view.showPoints(result.totalPoints());
    view.showWinningSequences(result.scoring().winningSequences());
    int awarded = result.scoring().awardedPoints().getOrDefault(result.mark(), 0);
    if (awarded > 0) {
      view.addPoints(participant, awarded, result.totalPoints());
    }
  }

  private void handleOpponentFailure(long requestedGeneration, RuntimeException exception) {
    if (requestedGeneration != generation) {
      return;
    }
    log.error("Opponent move failed", exception);
    opponent.disable();
    opponentAvailable = false;
    view.setOpponentAvailability(false, "AI runtime error. Restart the application.");
    view.setBoardDisabled(false);
    view.addOpponentFailure();
  }
}
