package com.games.theory.tictactoe.storage;

import com.games.theory.tictactoe.model.Node;
import io.github.palexdev.materialfx.utils.StringUtils;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Line;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class LinePredictor {
  public static Line createLine(StackPane pane, Node node1, Node node2) {
    return switch (predictPattern(node1, node2)) {
      case "column" -> new Line(pane.getLayoutX() + pane.getWidth(), pane.getLayoutY(), pane.getLayoutX() +
          pane.getWidth(), pane.getLayoutY() - pane.getHeight());
      case "row" -> new Line(pane.getLayoutX(), pane.getLayoutY() - pane.getHeight(), pane.getLayoutX() +
          pane.getWidth(), pane.getLayoutY() - pane.getHeight());
      case "diagonal-to-right" ->
          new Line(pane.getLayoutX() + pane.getWidth(), pane.getLayoutY(), pane.getLayoutX(),
              pane.getLayoutY() - pane.getHeight());
      case "diagonal-to-left" -> new Line(pane.getLayoutX(), pane.getLayoutY(), pane.getLayoutX() +
          pane.getWidth(), pane.getLayoutY() - pane.getHeight());
      default -> new Line(pane.getLayoutX(), pane.getLayoutY(), pane.getLayoutX() +
          pane.getWidth() / 2, pane.getLayoutY() - pane.getHeight() / 2);
    };
  }

  public static String predictPattern(Node node1, Node node2) {
    if (node1 == null || node2 == null) return StringUtils.EMPTY;
    int columnDifference = node2.getColIndex() - node1.getColIndex();
    int rowDifference = node2.getRowIndex() - node1.getRowIndex();
    String difference = String.valueOf(columnDifference) + rowDifference;
    return switch (difference) {
      case "01" -> "column";
      case "10" -> "row";
      case "11" -> "diagonal-to-right";
      case "-11" -> "diagonal-to-left";
      default -> throw new IllegalStateException("Unexpected value: " + difference);
    };
  }
}
