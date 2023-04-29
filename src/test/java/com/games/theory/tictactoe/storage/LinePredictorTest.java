package com.games.theory.tictactoe.storage;

import com.games.theory.tictactoe.model.Node;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LinePredictorTest {

  @ParameterizedTest
  @MethodSource("cases")
  void predictPattern(List<Integer> cols, List<Integer> rows, String predictedPattern) {
    Node node1 = new Node(cols.get(0), rows.get(0));
    Node node2 = new Node(cols.get(1), rows.get(1));
    String pattern = LinePredictor.predictPattern(node1, node2);
    assertEquals(predictedPattern, pattern);
  }

  private static Stream<Arguments> cases() {
    return Stream.of(
        Arguments.of(List.of(0, 0), List.of(1, 2), "column"),
        Arguments.of(List.of(0, 1), List.of(2, 2), "row"),
        Arguments.of(List.of(0, 1), List.of(0, 1), "diagonal-to-right"),
        Arguments.of(List.of(2, 1), List.of(0, 1), "diagonal-to-left")
    );
  }
}
