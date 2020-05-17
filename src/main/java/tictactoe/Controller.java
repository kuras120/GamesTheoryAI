package tictactoe;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
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

import java.net.URL;
import java.util.*;

public class Controller implements Initializable {

    @FXML
    private GridPane table;

    @FXML
    private CheckBox AICheckbox;

    @FXML
    private TextField winnerField;

    private boolean won;

    private boolean turn;

    private boolean repeat;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        won = false;
        turn = true;
        repeat = false;
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

    public boolean isTurn() {
        return turn;
    }

    public void changeTurn() {
        turn = !turn;
        if (!turn && !won && AICheckbox.isSelected()) {
            do {
                Random random = new Random();
                int randInt = random.nextInt(15) + 1;
                System.out.println(randInt);
                table.getChildren().get(randInt).fireEvent(new MouseEvent(MouseEvent.MOUSE_CLICKED, 0, 0, 0,
                        0, MouseButton.PRIMARY, 1, true, true, true, true, true, true,
                        true, true, true, true, null));
                //HERE IS THE PLACE FOR AN AI
            } while (repeat);
        }
    }

    private String observe(String pattern, Pair<List<Integer>, Integer> iterator) {
        IFifoQueue nodeQue = new FifoQueue(3);
        var tableList = new ArrayList<>(table.getChildren());

        Integer column = null;
        Integer row = null;
        Integer prevLoop = null;
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
                    if (nodeQue.isFull() && nodeQue.isAllEqual(pattern) != null) return nodeQue.isAllEqual(pattern);
                    column = node.getColIndex();
                    row = node.getRowIndex();
//                    System.out.println(node.getColIndex() + " " + node.getRowIndex() + " " + node.getMarkName());
                }
            }
        }
//        System.out.println("\n");
        return null;
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
                if (isTurn()) mark = "X";
                else mark = "O";
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
        List<Pair<String, Pair<List<Integer>, Integer>>> observers = new LinkedList<>();
        observers.add(new Pair<>("column", new Pair<>(Arrays.asList(1), 1)));
        observers.add(new Pair<>("row", new Pair<>(Arrays.asList(1, 2, 3, 4), 4)));
        observers.add(new Pair<>("diagonal-to-right", new Pair<>(Arrays.asList(1, 2, 5), 5)));
        observers.add(new Pair<>("diagonal-to-left", new Pair<>(Arrays.asList(3, 4, 8), 3)));

        String won;
        for (var pair:observers) {
            won = observe(pair.getKey(), pair.getValue());
            if (won != null) {
                this.won = true;
                table.setDisable(true);
                winnerField.setText("Player " + won + " won");
                System.out.println("Player " + won + " won");
                break;
            }
        }
    }

    @FXML
    private void resetGame() {
        for (var element:table.getChildren()) {
            if (element instanceof StackPane) {
                ((Pane) element).getChildren().clear();
                ((Node) element.getUserData()).setMarkName("");
            }
        }
        this.won = false;
        winnerField.setText("");
        table.setDisable(false);
        turn = true;
    }
}
