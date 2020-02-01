package tictactoe;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
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
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.Stack;

public class Controller implements Initializable {

    @FXML
    private GridPane table;

    private boolean turn;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        turn = true;
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

    private boolean observe(String pattern, Pair<Integer, Integer> iterator) {
        IFifoQueue nodeQue = new FifoQueue(3);
        var tableList = new ArrayList<>(table.getChildren());

        Integer column = null;
        Integer row = null;
        Integer prevLoop = null;

        for (int i = 0; i < iterator.getKey(); i++) {
            for (int j = i + 1; j < tableList.size(); j += iterator.getValue()) {
                if (tableList.get(j) instanceof StackPane) {
                    Node node = ((Node)tableList.get(j).getUserData());
                    switch (pattern) {
                        case "row":
                            if (!((Integer) node.getRowIndex()).equals(row)) nodeQue.clear();
                            nodeQue.addFirst(tableList.get(j));
                            System.out.println(nodeQue);
                            break;
                        case "column":
                            if (!((Integer) node.getColIndex()).equals(column)) nodeQue.clear();
                            nodeQue.addFirst(tableList.get(j));
                            System.out.println(nodeQue);
                            break;
                        case "diagonal-to-right":
                            if (!((Integer) i).equals(prevLoop)) {
                                nodeQue.clear();
                                prevLoop = i;
                            }
                            if ((node.getColIndex() == 4 && node.getRowIndex() == 1) ||
                                (node.getColIndex() == 3 && node.getRowIndex() == 1) ||
                                (node.getColIndex() == 4 && node.getRowIndex() == 2)) {
                                nodeQue.clear();
                            }
                            nodeQue.addFirst(tableList.get(j));
                            break;
                        case "diagonal-to-left":
                            if (!((Integer) i).equals(prevLoop)) {
                                nodeQue.clear();
                                prevLoop = i;
                            }
                            if ((node.getColIndex() == 4 && node.getRowIndex() == 3) ||
                                (node.getColIndex() == 3 && node.getRowIndex() == 4) ||
                                (node.getColIndex() == 4 && node.getRowIndex() == 4)) {
                                nodeQue.clear();
                            }
                            nodeQue.addFirst(tableList.get(j));
                            break;
                        default:
                            break;
                    }
                    if (nodeQue.isFull() && nodeQue.isAllEqual(pattern)) return true;
                    column = node.getColIndex();
                    row = node.getRowIndex();
                    System.out.println(node.getColIndex() + " " + node.getRowIndex() + " " + node.getMarkName());
                }
            }
        }
        System.out.println("\n");
        return false;
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
                text.setText(mark);
                node.setMarkName(mark);
                pane.getChildren().add(text);
                turn = !turn;
                boolean won = observe("column", new Pair<>(1, 1)) ||
                              observe("row", new Pair<>(4, 4)) ||
                              observe("diagonal-to-right", new Pair<>(3, 5)) ||
                              observe("diagonal-to-left", new Pair<>(3, 3));
                if (won) {
                    table.setDisable(true);
                    System.out.println("WON");

                }
            }
        });
        table.add(pane, colIndex, rowIndex);
    }

    @FXML
    private void resetGame() {
        for (var element:table.getChildren()) {
            if (element instanceof StackPane) {
                ((Pane) element).getChildren().clear();
                ((Node) element.getUserData()).setMarkName("");
            }
        }
        table.setDisable(false);
        turn = true;
    }
}
