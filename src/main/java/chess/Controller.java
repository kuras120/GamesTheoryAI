package chess;

import chess.model.Chessman;
import chess.model.Node;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    @FXML
    private GridPane table;

    private Map<String, String> state;

    private Map<String, String> names;

    private Pane draggedNode;

    private double mouseX, mouseY;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        state = new HashMap<>();
        names = new HashMap<>();
        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader("src/main/java/chess/model/DefaultState.data"));
            String line = reader.readLine();
            while (line != null) {
                var splittedLine = line.split(":");
                state.put(splittedLine[0], splittedLine[1]);
                line = reader.readLine();
            }
        }
        catch (Exception ex) {
            System.out.println(ex.getMessage());
            return;
        }

        try {
            reader = new BufferedReader(new FileReader("src/main/java/chess/model/Names.data"));
            String line = reader.readLine();
            while (line != null) {
                var splittedLine = line.split(":");
                names.put(splittedLine[0], splittedLine[1]);
                line = reader.readLine();
            }
        }
        catch (Exception ex) {
            System.out.println(ex.getMessage());
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

        pane.setOnMousePressed(e -> {
            javafx.scene.Node source = (javafx.scene.Node)e.getSource();
            Pane background = (Pane) table.getChildren().filtered(p -> p.getStyleClass().contains("square")).get(colIndex * 8 + rowIndex);
            System.out.println(background);
            background.setStyle(background.getStyle() + "-fx-border-color: chocolate;");
            System.out.printf("Mouse clicked cell [%d, %d]%n", colIndex, rowIndex);
            Chessman chessman = ((Node)source.getUserData()).getChessman();
            if (chessman != null) {
                System.out.println(chessman.getName() + " " + chessman.getColor());
            }
            else {
                System.out.println("No chessman on this place");
            }

            mouseX = e.getX();
            mouseY = e.getY();
        });
        pane.setOnMouseReleased(e -> {
            javafx.scene.Node source = (javafx.scene.Node)e.getSource();
            Pane background = (Pane) table.getChildren().filtered(p -> p.getStyleClass().contains("square")).get(colIndex * 8 + rowIndex);
            background.setStyle(background.getStyle().replace("-fx-border-color: chocolate;", ""));
            draggedNode = null;
        });
        pane.setOnDragDetected(e -> {
            draggedNode = (Pane) e.getSource();
        });
        pane.setOnMouseDragged(e -> {
            if (draggedNode != null) {
                draggedNode.toFront();
                draggedNode.setTranslateX(e.getX() + draggedNode.getTranslateX() - mouseX);
                draggedNode.setTranslateY(e.getY() + draggedNode.getTranslateY() - mouseY);
            }
        });

        Pane square = new StackPane();

        square.getStyleClass().add("square");
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
        if (chessmanCode != null) {
            var code = chessmanCode.split(",");
            String name = names.get(code[0]);
            chessman = new Chessman(name, code[1], chessmanCode);

            text.setText(name);
            text.setFont(Font.font("Arial", FontWeight.BOLD, FontPosture.REGULAR, 20));
            if (code[1].equals("W")) {
                text.setFill(Color.WHITE);
                text.setStyle(text.getStyle() + "-fx-stroke: black;");
            }
            else if (code[1].equals("B")) {
                text.setFill(Color.BLACK);
                text.setStyle(text.getStyle() + "-fx-stroke: white;");
            }
        }
        Pane movable = new StackPane();
        movable.setUserData(new Node(chessman, false));
        movable.getChildren().add(text);
        StackPane.setAlignment(text, Pos.CENTER);

        return movable;
    }
}
