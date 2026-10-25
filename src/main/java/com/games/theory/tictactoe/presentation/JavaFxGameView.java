package com.games.theory.tictactoe.presentation;

import atlantafx.base.controls.ToggleSwitch;
import com.games.theory.tictactoe.GameActivityFeed;
import com.games.theory.tictactoe.application.GameView;
import com.games.theory.tictactoe.exception.GameException;
import com.games.theory.tictactoe.model.BoardCoordinate;
import com.games.theory.tictactoe.model.WinningSequence;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public final class JavaFxGameView implements GameView {
  private final GridPane table;
  private final Pane winningLineOverlay;
  private final ToggleSwitch opponentToggle;
  private final Label opponentStatus;
  private final TextArea pointsField;
  private final ListView<String> activityFeedView;
  private final GameActivityFeed activityFeed;
  private final WinningLineProjector lineProjector;
  private final Map<BoardCoordinate, StackPane> cells = new LinkedHashMap<>();
  private final List<WinningSequence> winningSequences = new ArrayList<>();
  private int boardSize;
  private boolean lineRedrawScheduled;

  public JavaFxGameView(
      GridPane table,
      Pane winningLineOverlay,
      ToggleSwitch opponentToggle,
      Label opponentStatus,
      TextArea pointsField,
      ListView<String> activityFeedView,
      WinningLineProjector lineProjector
  ) {
    this.table = table;
    this.winningLineOverlay = winningLineOverlay;
    this.opponentToggle = opponentToggle;
    this.opponentStatus = opponentStatus;
    this.pointsField = pointsField;
    this.activityFeedView = activityFeedView;
    this.lineProjector = lineProjector;
    activityFeed = new GameActivityFeed(activityFeedView.getItems());
    activityFeedView.getItems().addListener((ListChangeListener<String>) change -> scrollActivity());
    winningLineOverlay.setMouseTransparent(true);
    winningLineOverlay.widthProperty().addListener((observable, previous, current) -> requestLineRedraw());
    winningLineOverlay.heightProperty().addListener((observable, previous, current) -> requestLineRedraw());
  }

  @Override
  public void initializeBoard(int size, Consumer<BoardCoordinate> moveHandler) {
    if (table.getColumnCount() != size || table.getRowCount() != size) {
      throw new GameException("Board constraints do not match game size " + size);
    }
    table.getChildren().clear();
    cells.clear();
    boardSize = size;
    for (int row = 0; row < size; row++) {
      for (int column = 0; column < size; column++) {
        BoardCoordinate coordinate = new BoardCoordinate(column, row);
        StackPane cell = new StackPane();
        cell.getStyleClass().add("game-cell");
        cell.setOnMouseClicked(event -> moveHandler.accept(coordinate));
        cells.put(coordinate, cell);
        table.add(cell, column, row);
      }
    }
  }

  @Override
  public void resetBoard() {
    cells.values().forEach(cell -> cell.getChildren().clear());
    winningSequences.clear();
    winningLineOverlay.getChildren().clear();
  }

  @Override
  public void showMove(BoardCoordinate coordinate, String mark) {
    StackPane cell = requireCell(coordinate);
    Text text = new Text(mark);
    text.getStyleClass().add("game-mark");
    cell.getChildren().add(text);
  }

  @Override
  public void showPoints(Map<String, Integer> points) {
    pointsField.setText(
        "Player X has " + points.getOrDefault("X", 0) + " points\n"
            + "Player O has " + points.getOrDefault("O", 0) + " points"
    );
  }

  @Override
  public void showWinningSequences(List<WinningSequence> sequences) {
    winningSequences.addAll(sequences);
    requestLineRedraw();
  }

  @Override
  public void setBoardDisabled(boolean disabled) {
    table.setDisable(disabled);
  }

  @Override
  public boolean isOpponentSelected() {
    return opponentToggle.isSelected();
  }

  @Override
  public void setOpponentAvailability(boolean available, String statusMessage) {
    opponentStatus.setText(statusMessage);
    if (!available) {
      opponentToggle.setSelected(false);
    }
    opponentToggle.setDisable(!available);
  }

  @Override
  public void startNewGame() {
    activityFeed.startNewGame();
  }

  @Override
  public void addMove(String participant, String mark, BoardCoordinate coordinate) {
    activityFeed.addMove(participant, mark, coordinate.row(), coordinate.column());
  }

  @Override
  public void addPoints(String participant, int awardedPoints, Map<String, Integer> totalPoints) {
    activityFeed.addPoints(
        participant,
        awardedPoints,
        totalPoints.getOrDefault("X", 0),
        totalPoints.getOrDefault("O", 0)
    );
  }

  @Override
  public void addOpponentFailure() {
    activityFeed.addAiFailure();
  }

  private void redrawLines() {
    if (winningSequences.isEmpty()
        || winningLineOverlay.getScene() == null
        || winningLineOverlay.getWidth() <= 0
        || winningLineOverlay.getHeight() <= 0) {
      return;
    }
    Map<BoardCoordinate, CellBounds> geometry = captureGeometry();
    winningLineOverlay.getChildren().clear();
    for (WinningSequence sequence : winningSequences) {
      LineSegment segment = lineProjector.project(sequence, geometry);
      Line line = new Line(segment.startX(), segment.startY(), segment.endX(), segment.endY());
      line.setManaged(false);
      line.setMouseTransparent(true);
      line.getStyleClass().add("winning-line");
      winningLineOverlay.getChildren().add(line);
    }
  }

  private void requestLineRedraw() {
    if (lineRedrawScheduled) {
      return;
    }
    lineRedrawScheduled = true;
    Platform.runLater(() -> {
      lineRedrawScheduled = false;
      redrawLines();
    });
  }

  private Map<BoardCoordinate, CellBounds> captureGeometry() {
    Map<BoardCoordinate, CellBounds> geometry = new LinkedHashMap<>();
    double cellWidth = winningLineOverlay.getWidth() / boardSize;
    double cellHeight = winningLineOverlay.getHeight() / boardSize;
    for (int row = 0; row < boardSize; row++) {
      for (int column = 0; column < boardSize; column++) {
        geometry.put(
            new BoardCoordinate(column, row),
            new CellBounds(column * cellWidth, row * cellHeight, cellWidth, cellHeight)
        );
      }
    }
    return geometry;
  }

  private StackPane requireCell(BoardCoordinate coordinate) {
    StackPane cell = cells.get(coordinate);
    if (cell == null) {
      throw new GameException("Board cell does not exist: " + coordinate);
    }
    return cell;
  }

  private void scrollActivity() {
    if (!activityFeedView.getItems().isEmpty()) {
      activityFeedView.scrollTo(activityFeedView.getItems().size() - 1);
    }
  }
}
