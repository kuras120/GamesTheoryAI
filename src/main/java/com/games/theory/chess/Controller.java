package com.games.theory.chess;

import com.games.theory.chess.model.Chessman;
import com.games.theory.chess.model.Node;
import com.games.theory.chess.utils.DataReaderUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import lombok.extern.slf4j.Slf4j;

import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;

@Slf4j
public class Controller implements Initializable {

    @FXML private GridPane table;

    private static final String SQUARE = "square";

    private Map<String, String> state;
    private Map<String, String> names;
    private javafx.scene.Node checkedNode;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        state = DataReaderUtils.readModel("data/DefaultState.data");
        names = DataReaderUtils.readModel("data/Names.data");

        if (state.isEmpty() || names.isEmpty()) {
            log.error("Error in model loading has occurred");
            return;
        }

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

    private void addPane(int colIndex, int rowIndex) {
        Pane pane = spawnChessman(colIndex, rowIndex);

        pane.setOnMouseClicked(e -> {
            Pane background;
            if (checkedNode == null) {
                checkedNode = (javafx.scene.Node) e.getSource();
                log.info("Mouse clicked cell [{}, {}]\n", colIndex, rowIndex);
                background = (Pane) ((Node) checkedNode.getUserData()).getBackgroundNode(table, SQUARE, colIndex, rowIndex);
                background.setStyle(background.getStyle() + "-fx-border-color: chocolate;");
                if (((Node) checkedNode.getUserData()).getChessman() != null) {
                    log.info(
                        "{} - {}",
                        ((Node) checkedNode.getUserData()).getChessman().getName(),
                        ((Node) checkedNode.getUserData()).getChessman().getColor()
                    );
                }
                else {
                    log.info("No chessman on this place");
                }
            }
            else {
                background = (Pane) ((Node) checkedNode.getUserData()).getBackgroundNode(table, SQUARE, null, null);
                background.setStyle(background.getStyle().replace("-fx-border-color: chocolate;", ""));
                if (checkedNode != e.getSource()) {
                    Chessman swap = ((Node) ((Pane) e.getSource()).getUserData()).getChessman();
                    ((Node) ((Pane) e.getSource()).getUserData()).setChessman(((Node) checkedNode.getUserData()).getChessman());
                    ((Node) checkedNode.getUserData()).setChessman(swap);

                    ((Pane) e.getSource()).getChildren().addAll(((Pane) checkedNode).getChildren());
                    ((Pane) checkedNode).getChildren().removeAll();
                }
                checkedNode = null;
            }
        });

        Pane square = new StackPane();

        square.getStyleClass().add(SQUARE);
        square.setStyle(square.getStyle() + "-fx-border-width: 3px;");
        if ((colIndex + rowIndex) % 2 == 0) square.setStyle(square.getStyle() + "-fx-background-color: sandybrown;");
        else square.setStyle(square.getStyle() + "-fx-background-color: saddlebrown;");

        table.add(square, colIndex, rowIndex);
        table.add(pane, colIndex, rowIndex);
    }

    private Pane spawnChessman(int colIndex, int rowIndex) {
        String chessmanCode = state.get(Character.toString((char) (colIndex + 65)) + (rowIndex + 1));
        Chessman chessman = null;
        Text text = new Text();
        Pane movable = new StackPane();

        if (chessmanCode != null) {
            var code = chessmanCode.split(",");
            String name = names.get(code[0]);
            chessman = new Chessman(name, code[1], chessmanCode);

            text.setText(name);
            text.setFont(Font.font("Arial", FontWeight.BOLD, FontPosture.REGULAR, 20));
            if ("W".equals(code[1])) {
                text.setFill(Color.WHITE);
                text.setStyle(text.getStyle() + "-fx-stroke: black;");
            }
            else if ("B".equals(code[1])) {
                text.setFill(Color.BLACK);
                text.setStyle(text.getStyle() + "-fx-stroke: white;");
            }

            movable.getChildren().add(text);
            StackPane.setAlignment(text, Pos.CENTER);
        }
        movable.setUserData(new Node(chessman, false, colIndex, rowIndex));

        return movable;
    }
}
