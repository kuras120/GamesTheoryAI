package com.games.theory.tictactoe;

import com.games.theory.tictactoe.exception.AiException;
import com.games.theory.tictactoe.exception.GameException;
import com.games.theory.tictactoe.model.Node;
import com.games.theory.tictactoe.processor.ColumnProcessor;
import com.games.theory.tictactoe.processor.DiagonalProcessor;
import com.games.theory.tictactoe.processor.ProcessorExecutor;
import com.games.theory.tictactoe.processor.RowProcessor;
import com.games.theory.utils.DataReaderUtils;
import com.games.theory.utils.LoggerUtils;
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
import lombok.extern.slf4j.Slf4j;

import java.net.URL;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Stream;

@Slf4j
public class Controller implements Initializable {
    @FXML private GridPane table;
    @FXML private CheckBox aiCheckbox;
    @FXML private TextArea winnerField;

    private ProcessorExecutor processorExecutor;
    private Map<String, Integer> points;
    private String[][] aiMap;
    private boolean turn;
    private boolean repeat;
    private Process process;
    private Random random;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        random = new Random();
        try {
            // TODO skip for tests
            process = new ProcessBuilder(List.of(
                DataReaderUtils.getScript("venv/Scripts/pip.exe").getPath(),
                "install",
                "-r",
                DataReaderUtils.getScript("game_theory/requirements.txt").getPath()
            )).start();
            if (process.waitFor(1, TimeUnit.MINUTES)) {
                LoggerUtils.processLog(process);
                log.info("AI env installation completed");
            } else {
                process.destroy();
                throw new TimeoutException("Time exceeded for AI env installation process");
            }
        } catch (Exception ex) {
            log.error("AI error {}", ex.getMessage());
            throw new AiException("AI error", ex);
        }
        turn = true;
        repeat = false;
        setInitialState();

        if (table.getColumnCount() != table.getRowCount())
            throw new GameException("Number of columns and rows must be equal");

        int size = table.getColumnCount();
        processorExecutor = new ProcessorExecutor()
            .add(new RowProcessor(3))
            .add(new ColumnProcessor(size, 3))
            .add(new DiagonalProcessor(3));

        for (int i = 0 ; i < size ; i++) {
            for (int j = 0; j < size; j++) {
                addPane(i, j);
            }
        }
        log.info("Initialization completed");
    }

    private void changeTurn() {
        turn = !turn;
        if (!turn && aiCheckbox.isSelected()) {
            do {
                int randInt = random.nextInt(16) + 1;
                log.info("Random move: {}", randInt);
                try {
                    List<String> command = Stream.concat(
                        Stream.of(
                            DataReaderUtils.getScript("venv/Scripts/python.exe").getPath(),
                            DataReaderUtils.getScript("game_theory/process.py").getPath(),
                            points.get("X").toString(),
                            points.get("O").toString()
                        ),
                        Arrays.stream(aiMap).flatMap(Arrays::stream).map(mark -> mark == null ? "N" : mark)
                    ).toList();
                    log.debug(command.toString());
                    process = new ProcessBuilder(command).start();
                    LoggerUtils.processLog(process);
                } catch (Exception ex) {
                    log.error("AI error {}", ex.getMessage());
                }
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
        winnerField.setText(
            "Player X has " + points.get("X") +
            " points\nPlayer O has " + points.get("O") + " points"
        );
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

    @FXML
    private void resetGame() {
        for (var element:table.getChildren()) {
            if (element instanceof StackPane) {
                ((Pane) element).getChildren().clear();
                ((Node) element.getUserData()).setMarkName("");
                ((Node) element.getUserData()).setChecked(false);
            }
        }
        setInitialState();
        winnerField.setText("");
        table.setDisable(false);
        turn = true;
    }

    private void setInitialState() {
        points = new HashMap<>();
        points.put("X", 0);
        points.put("O", 0);
        aiMap = new String[4][4];
    }
}
