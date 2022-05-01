package com.games.theory.tictactoe;

import com.games.theory.tictactoe.model.Node;
import com.games.theory.tictactoe.util.FifoQueue;
import com.games.theory.tictactoe.util.IFifoQueue;
import com.games.theory.tictactoe.util.LoggerUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.util.Pair;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Slf4j
public class Controller implements Initializable {

    @FXML private GridPane table;
    @FXML private CheckBox AICheckbox;
    @FXML private TextArea winnerField;

    private static final String AI_PATH = "src/main/java/com/games/theory/tictactoe/AI";

    private List<Pair<String, Pair<List<Integer>, Integer>>> observers;

    private Map<String, Integer> points;


    private String[][] aiMap;

    private boolean turn;

    private boolean repeat;

    private Process process;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            process = Runtime.getRuntime().exec(
                AI_PATH +
                "/venv/bin/pip install -r " +
                AI_PATH +
                "/requirements.txt"
            );
            if (!process.waitFor(1, TimeUnit.MINUTES)) {
                process.destroy();
                throw new InterruptedException("Time exceeded for AI env installation process");
            } else {
                LoggerUtils.processLog(process);
                log.info("AI env installation completed");
            }

        } catch (IOException | InterruptedException ex) {
            log.error("AI error {}", ex.getMessage());
        }
        turn = true;
        repeat = false;
        points = new HashMap<>() {{
            put("X", 0);
            put("O", 0);
        }};
        aiMap = new String[4][4];
        observers = Arrays.asList(
                new Pair<>("column", new Pair<>(List.of(1), 1)),
                new Pair<>("row", new Pair<>(Arrays.asList(1, 2, 3, 4), 4)),
                new Pair<>("diagonal-to-right", new Pair<>(Arrays.asList(1, 2, 5), 5)),
                new Pair<>("diagonal-to-left", new Pair<>(Arrays.asList(3, 4, 8), 3))
        );
        if (table != null) {
            int numRows = table.getRowCount();
            int numCols = table.getColumnCount();

            for (int i = 0 ; i < numCols ; i++) {
                for (int j = 0; j < numRows; j++) {
                    addPane(i, j);
                }
            }
        }
        log.info("Initialization completed");
    }

    private String buildArguments() {
        StringBuilder stringBuilder = new StringBuilder();
        for (var row:aiMap) {
            for (var mark:row) {
                stringBuilder.append(mark).append(' ');
            }
        }
        stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        return stringBuilder.toString();
    }

    private void changeTurn() {
        turn = !turn;
        if (!turn && AICheckbox.isSelected()) {
            Random random = new Random();
            do {
                int randInt = random.nextInt(16) + 1;
                log.info("Random move: {}", randInt);
                try {
                    process = Runtime.getRuntime().exec(
                        AI_PATH +
                        "/venv/bin/python " +
                        AI_PATH +
                        "/process.py " +
                        aiMap.length + " " +
                        buildArguments() + " " +
                        points.get("X") + " " +
                        points.get("O")
                    );
                    LoggerUtils.processLog(process);
                } catch (IOException ex) {
                    log.error("AI error {}", ex.getMessage());
                }
                table.getChildren().get(randInt).fireEvent(new MouseEvent(MouseEvent.MOUSE_CLICKED, 0, 0, 0,
                        0, MouseButton.PRIMARY, 1, true, true, true, true, true, true,
                        true, true, true, true, null));
            } while (repeat);
        }
    }

    private Map<String, Integer> observe(String pattern, Pair<List<Integer>, Integer> iterator) {
        IFifoQueue nodeQue = new FifoQueue(3);
        var tableList = new ArrayList<>(table.getChildren());
        Integer column = null;
        Integer row = null;
        Integer prevLoop = null;
        String won;
        Map<String, Integer> roundPoints = new HashMap<>() {{
             put("X", 0);
             put("O", 0);
         }};
        for (int i:iterator.getKey()) {
            for (int j = i; j < tableList.size(); j += iterator.getValue()) {
                if (tableList.get(j) instanceof StackPane) {
                    Node node = ((Node)tableList.get(j).getUserData());
                    switch (pattern) {
                        case "row":
                            if (!((Integer) node.getRowIndex()).equals(row)) nodeQue.clear();
                            nodeQue.addFirst(tableList.get(j));
                            break;
                        case "column":
                            if (!((Integer) node.getColIndex()).equals(column)) nodeQue.clear();
                            nodeQue.addFirst(tableList.get(j));
                            break;
                        case "diagonal-to-right":
                            if (!((Integer) i).equals(prevLoop)) {
                                nodeQue.clear();
                                prevLoop = i;
                            }
                            if ((node.getColIndex() == 3 && node.getRowIndex() == 0) ||
                                (node.getColIndex() == 2 && node.getRowIndex() == 0) ||
                                (node.getColIndex() == 3 && node.getRowIndex() == 1)) {
                                nodeQue.clear();
                            }
                            nodeQue.addFirst(tableList.get(j));
                            break;
                        case "diagonal-to-left":
                            if (!((Integer) i).equals(prevLoop)) {
                                nodeQue.clear();
                                prevLoop = i;
                            }
                            if ((node.getColIndex() == 3 && node.getRowIndex() == 2) ||
                                (node.getColIndex() == 2 && node.getRowIndex() == 3) ||
                                (node.getColIndex() == 3 && node.getRowIndex() == 3)) {
                                nodeQue.clear();
                            }
                            nodeQue.addFirst(tableList.get(j));
                            break;
                        default:
                            break;
                    }
                    if (nodeQue.isFull()) {
                        won = nodeQue.isAllEqual(pattern);
                        if (won != null) {
                            roundPoints.put(won, roundPoints.get(won) + 1);
                            points.put(won, points.get(won) + 1);
                            winnerField.setText(
                                    "Player X has " + points.get("X") +
                                    " points\nPlayer O has " + points.get("O") + " points"
                            );
                        }
                    }
                    column = node.getColIndex();
                    row = node.getRowIndex();
//                    System.out.println(node.getColIndex() + " " + node.getRowIndex() + " " + node.getMarkName());
                }
            }
        }
        return roundPoints;
    }

    private void addPane(int colIndex, int rowIndex) {
        Pane pane = new StackPane();
        pane.setUserData(new Node(colIndex, rowIndex));
        pane.setOnMouseClicked(e -> {
            Node node = (Node)pane.getUserData();
            if (node.getMarkName().isEmpty()) {
                Text text = new Text();
                text.setFont(Font.font("Arial", FontWeight.BOLD, FontPosture.REGULAR, 40));
                String mark;
                if (turn) mark = "X";
                else mark = "O";
                aiMap[rowIndex][colIndex] = mark;
                text.setText(mark);
                node.setMarkName(mark);
                pane.getChildren().add(text);
                checkWinner();
                changeTurn();
                repeat = false;
            }
            else {
                repeat = true;
            }
        });
        table.add(pane, colIndex, rowIndex);
    }

    private void checkWinner() {
        for (var pair:observers) {
            observe(pair.getKey(), pair.getValue());
        }
    }

    @FXML
    private void resetGame() {
        for (var element:table.getChildren()) {
            if (element instanceof StackPane) {
                ((Pane) element).getChildren().clear();
                ((Node) element.getUserData()).setMarkName("");
                ((Node) element.getUserData()).setChecked(false);
            }
        }
        points = new HashMap<>() {{
            put("X", 0);
            put("O", 0);
        }};
        aiMap = new String[4][4];
        winnerField.setText("");
        table.setDisable(false);
        turn = true;
    }
}
