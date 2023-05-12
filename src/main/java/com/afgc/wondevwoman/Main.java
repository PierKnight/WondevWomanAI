package com.afgc.wondevwoman;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

public class Main extends Application {

    private KeyEvent lastEvent;

    public static final GameHandler GAME_HANDLER = GameHandler.getInstance();
    @Override
    public void start(Stage stage) {
        Scene scene = new Scene(GameHandler.getInstance().getMyGamePanel(), 320, 240);

        stage.setTitle("WondevWoman");
        stage.setScene(scene);
        stage.setWidth(500);
        stage.setHeight(530);
        stage.setResizable(true);

        stage.show();

        GAME_HANDLER.getMyGamePanel().requestFocus();
    }

    public static void main(String[] args) {
        launch();
    }
}