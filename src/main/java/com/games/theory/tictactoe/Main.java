package com.games.theory.tictactoe;

import com.google.common.io.Resources;
import com.games.theory.ui.ThemeManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        ThemeManager.applyLightTheme();
        Parent root = FXMLLoader.load(Resources.getResource("TicTacToe.fxml"));
        primaryStage.setTitle("Tic Tac Toe AI");
        Scene scene = new Scene(root);
        scene.getStylesheets().add(Resources.getResource("styles/application.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(1280);
        primaryStage.setMinHeight(720);
        primaryStage.setMaxWidth(1920);
        primaryStage.setMaxHeight(1080);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
