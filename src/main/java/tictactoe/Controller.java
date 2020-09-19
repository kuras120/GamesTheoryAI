package tictactoe;

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
import tictactoe.model.Node;
import tictactoe.util.FifoQueue;
import tictactoe.util.IFifoQueue;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.*;

public class Controller implements Initializable {

    @FXML
    private GridPane table;

    @FXML
    private CheckBox AICheckbox;

    @FXML
    private TextArea winnerField;

    private Map<String, Integer> points;

    List<Pair<String, Pair<List<Integer>, Integer>>> observers;

    String[][] aiMap;

    private boolean turn;

    private boolean repeat;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        turn = true;
        repeat = false;
        points = new HashMap<>() {{
            put("X", 0);
            put("O", 0);
        }};
        aiMap = new String[4][4];
        observers = Arrays.asList(
                new Pair<>("column", new Pair<>(Arrays.asList(1), 1)),
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
    }

    private String buildArguments() {
        StringBuilder stringBuilder = new StringBuilder();
        for (var row:aiMap) {
            for (var mark:row) {
                stringBuilder.append(mark).append(" ");
            }
        }
        stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        return stringBuilder.toString();
    }

    private void changeTurn() {
        turn = !turn;
        if (!turn && AICheckbox.isSelected()) {
            Process process;
            do {
                Random random = new Random();
                int randInt = random.nextInt(16) + 1;
                System.out.println(randInt);
                try {
                    process = Runtime.getRuntime().exec(
                            "python3 src/main/java/tictactoe/AI/process.py " +
                                    aiMap.length + " " +
                                    buildArguments() + " " +
                                    points.get("X") + " " +
                                    points.get("O")
                    );
                    System.out.println(process.getOutputStream().toString());
                    BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
                    BufferedReader stdError = new BufferedReader(new InputStreamReader(process.getErrorStream()));
                    // Read the output from the command
                    System.out.println("Here is the standard output of the command:\n");
                    String s = null;
                    while ((s = stdInput.readLine()) != null) {
                        System.out.println(s);
                    }
                    // Read any errors from the attempted command
                    System.out.println("Here is the standard error of the command (if any):\n");
                    while ((s = stdError.readLine()) != null) {
                        System.out.println(s);
                    }
                } catch (IOException ex) {
                    System.out.println(ex.getMessage());
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
            Node node = ((Node)pane.getUserData());
            if (node.getMarkName().equals("")) {
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
