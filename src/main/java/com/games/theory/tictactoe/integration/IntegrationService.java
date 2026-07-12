package com.games.theory.tictactoe.integration;

import com.games.theory.tictactoe.exception.GameException;
import com.games.theory.tictactoe.exception.AiException;
import com.games.theory.tictactoe.model.Node;
import com.games.theory.tictactoe.processor.ProcessorExecutor;
import com.games.theory.utils.DataReaderUtils;
import com.games.theory.utils.FileType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextArea;
import javafx.application.Platform;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

@Slf4j
@RequiredArgsConstructor
public class IntegrationService {
    private final GridPane table;
    private final CheckBox aiCheckbox;
    private final TextArea pointsField;
    private final PythonExecutor pythonExecutor;
    private final ProcessorExecutor processorExecutor;

    private Map<String, Integer> points;
    private String[][] aiMap;
    private boolean turn;
    private int size;
    private long stateGeneration;

    public void initialize() {
        pythonExecutor.initialize(
                DataReaderUtils.getScript(FileType.PIP).getPath(),
                "install",
                DataReaderUtils.getScript("games_theory-1.0.0-py3-none-any.whl").getPath()
        );
        pythonExecutor.initialize(DataReaderUtils.getScript(FileType.GAMES_THEORY_INIT).getPath());
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
        log.info("Initialization completed");
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
    }

    private void addPane(int rowIndex, int colIndex) {
        Pane pane = new StackPane();
        pane.setUserData(new Node(colIndex, rowIndex));
        pane.setOnMouseClicked(e -> {
            Node node = (Node) pane.getUserData();
            if (node.getMarkName().isEmpty()) {
                placeMark(pane);
            }
        });
        table.add(pane, colIndex, rowIndex);
    }

    private void placeMark(Pane pane) {
        Node node = (Node) pane.getUserData();
        String mark = turn ? "X" : "O";
        Text text = new Text(mark);
        text.setFont(Font.font("Arial", FontWeight.BOLD, FontPosture.REGULAR, 40));
        aiMap[node.getRowIndex()][node.getColIndex()] = mark;
        node.setMarkName(mark);
        pane.getChildren().add(text);
        observe();
        changeTurn();
    }

    private void changeTurn() {
        turn = !turn;
        if (!turn && aiCheckbox.isSelected()) {
            table.setDisable(true);
            long requestedGeneration = stateGeneration;
            String[] board = Arrays
                .stream(aiMap)
                .flatMap(Arrays::stream)
                .toArray(String[]::new);
            Thread.startVirtualThread(() -> {
                try {
                    AiMove move = pythonExecutor.processState(
                    points.get("X").toString(),
                    points.get("O").toString(),
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
                            table.setDisable(false);
                        }
                    });
                } catch (AiException exception) {
                    log.error("Unable to play AI move", exception);
                    Platform.runLater(() -> table.setDisable(false));
                }
            });
        }
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
        placeMark(pane);
    }

    private void observe() {
        var tableList = new ArrayList<>(table.getChildren());
        processorExecutor.execute(tableList).collect().forEach((k, v) -> points.merge(k, v, Integer::sum));
        pointsField.setText("Player X has " + points.get("X") + " points\nPlayer O has " + points.get("O") + " points");
    }
}
