package com.timem.game2048javafx;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Game2048 extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Game2048.class.getResource("game2048.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 920, 480);
        stage.setTitle("Game 2048 -- FXML!");
        stage.setScene(scene);
        // 设置窗体大小不可调整
        stage.setResizable(false);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}