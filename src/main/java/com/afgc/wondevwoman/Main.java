package com.afgc.wondevwoman;

import com.afgc.wondevwoman.graphic.SceneHandler;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {


    @Override
    public void start(Stage stage) {
        SceneHandler.getInstance().init(stage);
    }

    public static void main(String[] args) {
        launch();
    }
}