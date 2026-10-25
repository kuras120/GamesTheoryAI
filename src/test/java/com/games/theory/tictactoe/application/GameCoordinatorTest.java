package com.games.theory.tictactoe.application;

import com.games.theory.tictactoe.model.BoardCoordinate;
import com.games.theory.tictactoe.model.GameSession;
import com.games.theory.tictactoe.model.GameSnapshot;
import com.games.theory.tictactoe.model.WinningSequence;
import com.games.theory.tictactoe.processor.ColumnProcessor;
import com.games.theory.tictactoe.processor.DiagonalProcessor;
import com.games.theory.tictactoe.processor.ProcessorExecutor;
import com.games.theory.tictactoe.processor.RowProcessor;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GameCoordinatorTest {
  private static final Executor DIRECT_EXECUTOR = Runnable::run;

  @Test
  void orchestratesHumanAndExternalOpponentMoves() {
    FakeView view = new FakeView();
    view.opponentSelected = true;
    FakeOpponent opponent = new FakeOpponent(new OpponentMove(1, 0));
    GameCoordinator coordinator = coordinator(view, opponent, DIRECT_EXECUTOR, DIRECT_EXECUTOR);
    coordinator.initialize();

    coordinator.playHumanMove(new BoardCoordinate(0, 0));

    assertEquals(List.of("X@0,0", "O@1,0"), view.moves);
    assertEquals(List.of("You", "AI"), view.participants);
    assertFalse(view.boardDisabled);
    assertTrue(view.opponentAvailable);
  }

  @Test
  void resetInvalidatesAnOutstandingOpponentResult() {
    FakeView view = new FakeView();
    view.opponentSelected = true;
    FakeOpponent opponent = new FakeOpponent(new OpponentMove(1, 0));
    QueueExecutor background = new QueueExecutor();
    GameCoordinator coordinator = coordinator(view, opponent, background, DIRECT_EXECUTOR);
    coordinator.initialize();
    background.runNext();

    coordinator.playHumanMove(new BoardCoordinate(0, 0));
    coordinator.reset();
    background.runNext();

    assertTrue(view.moves.isEmpty());
    assertFalse(view.boardDisabled);
  }

  @Test
  void invalidOpponentMoveDisablesOpponentAndRestoresBoard() {
    FakeView view = new FakeView();
    view.opponentSelected = true;
    FakeOpponent opponent = new FakeOpponent(new OpponentMove(9, 9));
    GameCoordinator coordinator = coordinator(view, opponent, DIRECT_EXECUTOR, DIRECT_EXECUTOR);
    coordinator.initialize();

    coordinator.playHumanMove(new BoardCoordinate(0, 0));

    assertFalse(view.opponentAvailable);
    assertFalse(view.boardDisabled);
    assertEquals(1, view.opponentFailures);
  }

  private GameCoordinator coordinator(
      FakeView view,
      Opponent opponent,
      Executor background,
      Executor presentation
  ) {
    GameSession session = new GameSession(
        4,
        new ProcessorExecutor()
            .add(new RowProcessor(3))
            .add(new ColumnProcessor(3))
            .add(new DiagonalProcessor(3))
    );
    return new GameCoordinator(session, view, opponent, background, presentation);
  }

  private static final class FakeOpponent implements Opponent {
    private final OpponentMove move;

    private FakeOpponent(OpponentMove move) {
      this.move = move;
    }

    @Override
    public OpponentAvailability prepare() {
      return new OpponentAvailability(true, "AI available.");
    }

    @Override
    public OpponentMove chooseMove(GameSnapshot snapshot) {
      return move;
    }

    @Override
    public void disable() {
    }
  }

  private static final class QueueExecutor implements Executor {
    private final List<Runnable> tasks = new ArrayList<>();

    @Override
    public void execute(Runnable command) {
      tasks.add(command);
    }

    private void runNext() {
      tasks.removeFirst().run();
    }
  }

  private static final class FakeView implements GameView {
    private final List<String> moves = new ArrayList<>();
    private final List<String> participants = new ArrayList<>();
    private boolean opponentSelected;
    private boolean opponentAvailable;
    private boolean boardDisabled;
    private int opponentFailures;

    @Override
    public void initializeBoard(int size, Consumer<BoardCoordinate> moveHandler) {
    }

    @Override
    public void resetBoard() {
      moves.clear();
      participants.clear();
    }

    @Override
    public void showMove(BoardCoordinate coordinate, String mark) {
      moves.add(mark + "@" + coordinate.column() + "," + coordinate.row());
    }

    @Override
    public void showPoints(Map<String, Integer> points) {
    }

    @Override
    public void showWinningSequences(List<WinningSequence> sequences) {
    }

    @Override
    public void setBoardDisabled(boolean disabled) {
      boardDisabled = disabled;
    }

    @Override
    public boolean isOpponentSelected() {
      return opponentSelected;
    }

    @Override
    public void setOpponentAvailability(boolean available, String statusMessage) {
      opponentAvailable = available;
    }

    @Override
    public void startNewGame() {
    }

    @Override
    public void addMove(String participant, String mark, BoardCoordinate coordinate) {
      participants.add(participant);
    }

    @Override
    public void addPoints(String participant, int awardedPoints, Map<String, Integer> totalPoints) {
    }

    @Override
    public void addOpponentFailure() {
      opponentFailures++;
    }
  }
}
