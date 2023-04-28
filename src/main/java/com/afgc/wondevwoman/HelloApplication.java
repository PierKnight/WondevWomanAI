package com.afgc.wondevwoman;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {

    private KeyEvent lastEvent;



    public static final GamePanel gamePanel = new GamePanel();

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.setWidth(500);
        stage.setMinWidth(500);
        stage.setHeight(540);
        stage.setResizable(false);
        stage.show();


        gamePanel.requestFocus();


    }

    public static void main(String[] args) {
        launch();
    }
}