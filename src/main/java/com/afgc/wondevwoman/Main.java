package com.afgc.wondevwoman;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {

    private KeyEvent lastEvent;

    public static final GameHandler GAME_HANDLER = GameHandler.getInstance();

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(GameHandler.getInstance().getMyGamePanel(), 320, 240);

        stage.setTitle("WondevWoman");
        stage.setScene(scene);
        stage.setWidth(500);
        stage.setMinWidth(500);
        stage.setHeight(540);
        stage.setResizable(false);
        stage.show();


        GAME_HANDLER.getMyGamePanel().requestFocus();

    }

    public static void main(String[] args) {
        launch();
    }
}