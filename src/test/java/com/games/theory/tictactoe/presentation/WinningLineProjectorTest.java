package com.games.theory.tictactoe.presentation;

import com.games.theory.tictactoe.model.BoardCoordinate;
import com.games.theory.tictactoe.model.WinningSequence;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class WinningLineProjectorTest {
  private final WinningLineProjector projector = new WinningLineProjector();
  private final Map<BoardCoordinate, CellBounds> geometry = geometry();

  @Test
  void projectsAContinuousRowBetweenOuterCellCenters() {
    assertEquals(
        new LineSegment(50, 150, 250, 150),
        projector.project(sequence(cell(0, 1), cell(1, 1), cell(2, 1)), geometry)
    );
  }

  @Test
  void projectsAContinuousColumnBetweenOuterCellCenters() {
    assertEquals(
        new LineSegment(150, 50, 150, 250),
        projector.project(sequence(cell(1, 0), cell(1, 1), cell(1, 2)), geometry)
    );
  }

  @Test
  void projectsBothDiagonalDirectionsBetweenOuterCellCenters() {
    assertEquals(
        new LineSegment(50, 50, 250, 250),
        projector.project(sequence(cell(0, 0), cell(1, 1), cell(2, 2)), geometry)
    );
    assertEquals(
        new LineSegment(50, 250, 250, 50),
        projector.project(sequence(cell(0, 2), cell(1, 1), cell(2, 0)), geometry)
    );
  }

  private WinningSequence sequence(BoardCoordinate... coordinates) {
    return new WinningSequence("X", List.of(coordinates));
  }

  private BoardCoordinate cell(int column, int row) {
    return new BoardCoordinate(column, row);
  }

  private Map<BoardCoordinate, CellBounds> geometry() {
    Map<BoardCoordinate, CellBounds> result = new LinkedHashMap<>();
    for (int row = 0; row < 4; row++) {
      for (int column = 0; column < 4; column++) {
        result.put(cell(column, row), new CellBounds(column * 100, row * 100, 100, 100));
      }
    }
    return result;
  }
}
