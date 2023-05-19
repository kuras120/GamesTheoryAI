package com.games.theory.tictactoe.integration;

import com.games.theory.tictactoe.exception.GameException;
import com.games.theory.tictactoe.model.Node;
import com.games.theory.tictactoe.processor.ProcessorExecutor;
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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

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
    private boolean repeat;
    private Random random;
    private boolean turn;
    private int size;

    public void initialize() {
        random = new Random();
        pythonExecutor.installDependencies();
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
        points = new HashMap<>();
        points.put("X", 0);
        points.put("O", 0);
        pointsField.setText("Player X has " + points.get("X") + " points\nPlayer O has " + points.get("O") + " points");
        aiMap = new String[size][size];
        table.setDisable(false);
        turn = true;
        repeat = false;
    }

    private void addPane(int rowIndex, int colIndex) {
        Pane pane = new StackPane();
        pane.setUserData(new Node(colIndex, rowIndex));
        pane.setOnMouseClicked(e -> {
            Node node = (Node) pane.getUserData();
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
                observe();
                changeTurn();
                repeat = false;
            }
            else {
                repeat = true;
            }
        });
        table.add(pane, colIndex, rowIndex);
    }

    private void changeTurn() {
        turn = !turn;
        if (!turn && aiCheckbox.isSelected()) {
            do {
                int randInt = random.nextInt(16) + 1;
                log.info("Random move: {}", randInt);
                pythonExecutor.processState(points.get("X").toString(), points.get("O").toString(), aiMap);
                table.getChildren().get(randInt).fireEvent(new MouseEvent(MouseEvent.MOUSE_CLICKED, 0, 0, 0,
                    0, MouseButton.PRIMARY, 1, true, true, true,
                    true, true, true, true, true,
                    true, true, null));
            } while (repeat);
        }
    }

    private void observe() {
        var tableList = new ArrayList<>(table.getChildren());
        processorExecutor.execute(tableList).collect().forEach((k, v) -> points.merge(k, v, Integer::sum));
        pointsField.setText("Player X has " + points.get("X") + " points\nPlayer O has " + points.get("O") + " points");
    }
}
