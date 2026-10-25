package com.games.theory.tictactoe.model;

import com.games.theory.tictactoe.processor.ColumnProcessor;
import com.games.theory.tictactoe.processor.DiagonalProcessor;
import com.games.theory.tictactoe.processor.ProcessorExecutor;
import com.games.theory.tictactoe.processor.RowProcessor;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GameSessionTest {
  @Test
  void placesMovesAndReturnsNewWinningSequencesWithoutJavaFxState() {
    GameSession session = session();

    session.placeMove(cell(0, 0));
    session.placeMove(cell(0, 3));
    session.placeMove(cell(1, 0));
    session.placeMove(cell(1, 3));
    MoveResult result = session.placeMove(cell(2, 0));

    assertEquals("X", result.mark());
    assertEquals(1, result.scoring().awardedPoints().get("X"));
    assertEquals(1, result.scoring().winningSequences().size());
    assertEquals(1, result.totalPoints().get("X"));
    assertEquals("O", result.nextMark());
  }

  @Test
  void preservesOverlappingThreeCellWindowScoring() {
    GameSession session = session();

    session.placeMove(cell(0, 0));
    session.placeMove(cell(0, 3));
    session.placeMove(cell(1, 0));
    session.placeMove(cell(1, 3));
    session.placeMove(cell(2, 0));
    session.placeMove(cell(2, 3));
    MoveResult result = session.placeMove(cell(3, 0));

    assertEquals(1, result.scoring().awardedPoints().get("X"));
    assertEquals(2, result.totalPoints().get("X"));
  }

  @Test
  void resetClearsBoardScoreAndTurn() {
    GameSession session = session();
    session.placeMove(cell(0, 0));
    session.reset();

    assertTrue(session.isEmpty(cell(0, 0)));
    assertEquals("X", session.nextMark());
    assertEquals(0, session.points().get("X"));
    assertEquals("", session.snapshot().board().getFirst());
  }

  @Test
  void snapshotIsRowMajorAndContainsMarks() {
    GameSession session = session();
    session.placeMove(cell(2, 1));

    GameSnapshot snapshot = session.snapshot();

    assertFalse(snapshot.board().isEmpty());
    assertEquals("X", snapshot.board().get(6));
  }

  private GameSession session() {
    return new GameSession(
        4,
        new ProcessorExecutor()
            .add(new RowProcessor(3))
            .add(new ColumnProcessor(3))
            .add(new DiagonalProcessor(3))
    );
  }

  private BoardCoordinate cell(int column, int row) {
    return new BoardCoordinate(column, row);
  }
}
