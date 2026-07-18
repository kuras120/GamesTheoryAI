package com.games.theory.chess;

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
        Parent root = FXMLLoader.load(Resources.getResource("Chess.fxml"));
        primaryStage.setTitle("Chess AI");
        Scene scene = new Scene(root, 1600, 900);
        scene.getStylesheets().add(Resources.getResource("styles/application.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
