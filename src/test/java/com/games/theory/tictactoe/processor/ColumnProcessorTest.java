package com.games.theory.tictactoe.processor;

import com.games.theory.tictactoe.model.GameCell;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ColumnProcessorTest {
  private final ColumnProcessor columnProcessor = new ColumnProcessor(3);

  @AfterEach
  void afterEach() {
    columnProcessor.reset();
  }

  @ParameterizedTest
  @MethodSource("cases")
  void scoresColumns(List<GameCell> cells, Map<String, Integer> expectedPoints, String match) {
    cells.forEach(columnProcessor::process);
    assertEquals(expectedPoints, columnProcessor.getPoints(), "Expect " + match + " for current map");
  }

  private static Stream<Arguments> cases() {
    return Stream.of(
        Arguments.of(
            List.of(cell(0, 0, "X"), cell(0, 1, "X"), cell(0, 2, "X")),
            Map.of("X", 1, "O", 0),
            "match"
        ),
        Arguments.of(
            List.of(cell(0, 0, "X"), cell(1, 0, "X"), cell(0, 2, "X")),
            Map.of("X", 0, "O", 0),
            "no match"
        )
    );
  }

  private static GameCell cell(int column, int row, String mark) {
    GameCell cell = new GameCell(column, row);
    cell.place(mark);
    return cell;
  }
}
