package com.games.theory.tictactoe;

import com.games.theory.tictactoe.integration.IntegrationService;
import com.games.theory.tictactoe.integration.PythonExecutor;
import com.games.theory.tictactoe.model.Node;
import com.games.theory.tictactoe.processor.ColumnProcessor;
import com.games.theory.tictactoe.processor.DiagonalProcessor;
import com.games.theory.tictactoe.processor.ProcessorExecutor;
import com.games.theory.tictactoe.processor.RowProcessor;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import lombok.extern.slf4j.Slf4j;

import java.net.URL;
import java.util.ResourceBundle;

@Slf4j
public class Controller implements Initializable {
    @FXML private GridPane table;
    @FXML private CheckBox aiCheckbox;
    @FXML private TextArea pointsField;

    private IntegrationService integrationService;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        integrationService = new IntegrationService(
            table,
            aiCheckbox,
            pointsField,
            new PythonExecutor(),
            new ProcessorExecutor()
                .add(new RowProcessor(3))
                .add(new ColumnProcessor(3))
                .add(new DiagonalProcessor(3))
        );
        integrationService.initialize();
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
        integrationService.setInitialState();
    }
}
