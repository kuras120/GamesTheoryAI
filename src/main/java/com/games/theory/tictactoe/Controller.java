package com.games.theory.tictactoe;

import atlantafx.base.controls.ToggleSwitch;
import com.games.theory.tictactoe.integration.IntegrationService;
import com.games.theory.tictactoe.integration.PythonExecutor;
import com.games.theory.tictactoe.integration.PythonRuntimeManager;
import com.games.theory.tictactoe.model.Node;
import com.games.theory.tictactoe.processor.ColumnProcessor;
import com.games.theory.tictactoe.processor.DiagonalProcessor;
import com.games.theory.tictactoe.processor.ProcessorExecutor;
import com.games.theory.tictactoe.processor.RowProcessor;
import com.games.theory.ui.ThemeManager;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
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
    @FXML private ToggleSwitch aiCheckbox;
    @FXML private ToggleSwitch darkModeToggle;
    @FXML private Label aiStatusLabel;
    @FXML private ListView<String> activityFeed;
    @FXML private TextArea pointsField;

    private IntegrationService integrationService;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        darkModeToggle.selectedProperty().addListener(
            (observable, previous, selected) -> ThemeManager.apply(selected)
        );
        activityFeed.getItems().addListener((ListChangeListener<String>) change -> {
            if (!activityFeed.getItems().isEmpty()) {
                activityFeed.scrollTo(activityFeed.getItems().size() - 1);
            }
        });
        integrationService = new IntegrationService(
            table,
            aiCheckbox,
            aiStatusLabel,
            pointsField,
            new PythonExecutor(),
            new PythonRuntimeManager(),
            new ProcessorExecutor()
                .add(new RowProcessor(3))
                .add(new ColumnProcessor(3))
                .add(new DiagonalProcessor(3)),
            new GameActivityFeed(activityFeed.getItems())
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
