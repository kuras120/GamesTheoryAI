package com.games.theory.tictactoe.integration;

import atlantafx.base.controls.ToggleSwitch;
import com.games.theory.tictactoe.GameActivityFeed;
import com.games.theory.tictactoe.exception.AiException;
import com.games.theory.tictactoe.exception.GameException;
import com.games.theory.tictactoe.model.Node;
import com.games.theory.tictactoe.processor.ProcessorExecutor;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

@Slf4j
@RequiredArgsConstructor
public class IntegrationService {
    private final GridPane table;
    private final ToggleSwitch aiCheckbox;
    private final Label aiStatusLabel;
    private final TextArea pointsField;
    private final PythonExecutor pythonExecutor;
    private final PythonRuntimeManager pythonRuntimeManager;
    private final ProcessorExecutor processorExecutor;
    private final GameActivityFeed activityFeed;

    private Map<String, Integer> points;
    private String[][] aiMap;
    private boolean turn;
    private int size;
    private long stateGeneration;
    private volatile PythonRuntime pythonRuntime;

    public void initialize() {
        if (table.getColumnCount() != table.getRowCount()) {
            throw new GameException("Number of columns and rows must be equal");
        }
        size = table.getColumnCount();
        for (int i = 0 ; i < size ; i++) {
            for (int j = 0; j < size; j++) {
                addPane(i, j);
            }
        }
        setInitialState();
        prepareAiRuntime();
        log.info("Initialization completed");
    }

    private void prepareAiRuntime() {
        aiCheckbox.setSelected(false);
        aiCheckbox.setDisable(true);
        aiStatusLabel.setText("Preparing AI…");
        Thread.startVirtualThread(() -> {
            PythonRuntimeResult result = pythonRuntimeManager.prepare();
            Platform.runLater(() -> applyRuntimeResult(result));
        });
    }

    private void applyRuntimeResult(PythonRuntimeResult result) {
        aiStatusLabel.setText(result.statusMessage());
        pythonRuntime = result.runtime();
        aiCheckbox.setSelected(false);
        aiCheckbox.setDisable(!result.isAvailable());
    }

    public void setInitialState() {
        stateGeneration++;
        points = new HashMap<>();
        points.put("X", 0);
        points.put("O", 0);
        pointsField.setText("Player X has " + points.get("X") + " points\nPlayer O has " + points.get("O") + " points");
        aiMap = new String[size][size];
        table.setDisable(false);
        turn = true;
        activityFeed.startNewGame();
    }

    private void addPane(int rowIndex, int colIndex) {
        Pane pane = new StackPane();
        pane.getStyleClass().add("game-cell");
        pane.setUserData(new Node(colIndex, rowIndex));
        pane.setOnMouseClicked(e -> {
            Node node = (Node) pane.getUserData();
            if (node.getMarkName().isEmpty()) {
                placeMark(pane, false);
            }
        });
        table.add(pane, colIndex, rowIndex);
    }

    private void placeMark(Pane pane, boolean aiMove) {
        Node node = (Node) pane.getUserData();
        String mark = turn ? "X" : "O";
        String participant = participant(mark, aiMove);
        Text text = new Text(mark);
        text.getStyleClass().add("game-mark");
        aiMap[node.getRowIndex()][node.getColIndex()] = mark;
        node.setMarkName(mark);
        pane.getChildren().add(text);
        activityFeed.addMove(participant, mark, node.getRowIndex(), node.getColIndex());
        observe(mark, participant);
        changeTurn();
    }

    private String participant(String mark, boolean aiMove) {
        if (aiMove) {
            return "AI";
        }
        if (aiCheckbox.isSelected() && "X".equals(mark)) {
            return "You";
        }
        return "Player " + mark;
    }

    private void changeTurn() {
        turn = !turn;
        PythonRuntime readyRuntime = pythonRuntime;
        if (!turn && aiCheckbox.isSelected() && readyRuntime != null) {
            table.setDisable(true);
            long requestedGeneration = stateGeneration;
            String pointsX = points.get("X").toString();
            String pointsO = points.get("O").toString();
            String[] board = Arrays
                .stream(aiMap)
                .flatMap(Arrays::stream)
                .toArray(String[]::new);
            Thread.startVirtualThread(() -> {
                try {
                    AiMove move = pythonExecutor.processState(
                    readyRuntime,
                    pointsX,
                    pointsO,
                    board
                    );
                    Platform.runLater(() -> {
                        if (requestedGeneration != stateGeneration) {
                            return;
                        }
                        try {
                            playAiMove(move);
                        } catch (AiException exception) {
                            log.error("Unable to play AI move", exception);
                            disableAiAfterFailure();
                        }
                    });
                } catch (AiException exception) {
                    log.error("Unable to play AI move", exception);
                    Platform.runLater(this::disableAiAfterFailure);
                }
            });
        }
    }

    private void disableAiAfterFailure() {
        pythonRuntime = null;
        aiCheckbox.setSelected(false);
        aiCheckbox.setDisable(true);
        aiStatusLabel.setText("AI runtime error. Restart the application.");
        activityFeed.addAiFailure();
        table.setDisable(false);
    }

    private void playAiMove(AiMove move) {
        if (move.x() < 0 || move.x() >= size || move.y() < 0 || move.y() >= size) {
            table.setDisable(false);
            throw new AiException("AI move is outside the board: " + move);
        }
        Pane pane = table.getChildren().stream()
            .filter(Pane.class::isInstance)
            .map(Pane.class::cast)
            .filter(cell -> {
                Node node = (Node) cell.getUserData();
                return node.getColIndex() == move.x() && node.getRowIndex() == move.y();
            })
            .findFirst()
            .orElseThrow(() -> new AiException("AI cell does not exist: " + move));
        Node node = (Node) pane.getUserData();
        if (!node.getMarkName().isEmpty()) {
            table.setDisable(false);
            throw new AiException("AI selected an occupied cell: " + move);
        }
        table.setDisable(false);
        placeMark(pane, true);
    }

    private void observe(String mark, String participant) {
        var tableList = new ArrayList<>(table.getChildren());
        Map<String, Integer> awarded = processorExecutor.execute(tableList).collect();
        awarded.forEach((key, value) -> points.merge(key, value, Integer::sum));
        pointsField.setText("Player X has " + points.get("X") + " points\nPlayer O has " + points.get("O") + " points");
        int awardedPoints = awarded.getOrDefault(mark, 0);
        if (awardedPoints > 0) {
            activityFeed.addPoints(participant, awardedPoints, points.get("X"), points.get("O"));
        }
    }
}
