package com.games.theory.tictactoe;

import atlantafx.base.controls.ToggleSwitch;
import com.games.theory.tictactoe.application.GameCoordinator;
import com.games.theory.tictactoe.integration.ExternalOpponent;
import com.games.theory.tictactoe.integration.PythonExecutor;
import com.games.theory.tictactoe.integration.PythonRuntimeManager;
import com.games.theory.tictactoe.model.GameSession;
import com.games.theory.tictactoe.presentation.JavaFxGameView;
import com.games.theory.tictactoe.presentation.WinningLineProjector;
import com.games.theory.tictactoe.processor.ColumnProcessor;
import com.games.theory.tictactoe.processor.DiagonalProcessor;
import com.games.theory.tictactoe.processor.ProcessorExecutor;
import com.games.theory.tictactoe.processor.RowProcessor;
import com.games.theory.ui.ThemeManager;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.Executor;

public class Controller implements Initializable {
  @FXML private GridPane table;
  @FXML private Pane winningLineOverlay;
  @FXML private ToggleSwitch aiCheckbox;
  @FXML private ToggleSwitch darkModeToggle;
  @FXML private Label aiStatusLabel;
  @FXML private ListView<String> activityFeed;
  @FXML private TextArea pointsField;

  private GameCoordinator coordinator;

  @Override
  public void initialize(URL url, ResourceBundle resourceBundle) {
    darkModeToggle.selectedProperty().addListener(
        (observable, previous, selected) -> ThemeManager.apply(selected)
    );
    JavaFxGameView view = new JavaFxGameView(
        table,
        winningLineOverlay,
        aiCheckbox,
        aiStatusLabel,
        pointsField,
        activityFeed,
        new WinningLineProjector()
    );
    GameSession session = new GameSession(
        table.getColumnCount(),
        new ProcessorExecutor()
            .add(new RowProcessor(3))
            .add(new ColumnProcessor(3))
            .add(new DiagonalProcessor(3))
    );
    Executor backgroundExecutor = command -> Thread.startVirtualThread(command);
    Executor presentationExecutor = Platform::runLater;
    coordinator = new GameCoordinator(
        session,
        view,
        new ExternalOpponent(new PythonExecutor(), new PythonRuntimeManager()),
        backgroundExecutor,
        presentationExecutor
    );
    coordinator.initialize();
  }

  @FXML
  private void resetGame() {
    coordinator.reset();
  }
}
