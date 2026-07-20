package com.games.theory.tictactoe.processor;

import com.games.theory.tictactoe.model.GameCell;
import com.google.common.io.Resources;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ProcessorExecutorTest {
  @ParameterizedTest
  @MethodSource("cases")
  void executeAndCollect(List<GameCell> cells, Map<String, Integer> expectedPoints, String points) {
    var result = new ProcessorExecutor()
        .add(new RowProcessor(3))
        .add(new ColumnProcessor(3))
        .add(new DiagonalProcessor(3))
        .execute(cells)
        .collect();
    assertEquals(expectedPoints, result.awardedPoints(), "Expect " + points + " for current map");
    assertEquals(expectedPoints.values().stream().mapToInt(Integer::intValue).sum(), result.winningSequences().size());
  }

  private static Stream<Arguments> cases() throws IOException {
    return Stream.of(
        Arguments.of(readMap("map1"), Map.of("X", 4, "O", 0), "4:0"),
        Arguments.of(readMap("map2"), Map.of("X", 2, "O", 2), "2:2")
    );
  }

  private static List<GameCell> readMap(String mapName) throws IOException {
    List<GameCell> cells = new LinkedList<>();
    List<String> text = Resources.readLines(Resources.getResource(mapName), StandardCharsets.UTF_8);
    for (int row = 0; row < text.size(); row++) {
      for (int column = 0; column < text.get(row).length(); column++) {
        cells.add(cell(column, row, String.valueOf(text.get(row).charAt(column))));
      }
    }
    return cells;
  }

  private static GameCell cell(int column, int row, String mark) {
    GameCell cell = new GameCell(column, row);
    cell.place(mark);
    return cell;
  }
}
