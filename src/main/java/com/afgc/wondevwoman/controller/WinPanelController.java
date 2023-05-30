package com.afgc.wondevwoman.controller;

import com.afgc.wondevwoman.graphic.SceneHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;

public class WinPanelController {

    @FXML
    private Label player1Points;

    @FXML
    private Label player2Points;

    @FXML
    private Label winPlayer;

    @FXML
    void goToMenu(MouseEvent event) {
        SceneHandler.getInstance().closeWinPanel();
        SceneHandler.getInstance().loadMenu();
    }

    public void initialize() {
        System.out.println(GameHandler.getInstance().getPlayers()[0].getPoints());
        player1Points.setText(String.valueOf(GameHandler.getInstance().getPlayers()[0].getPoints()));
        player2Points.setText(String.valueOf(GameHandler.getInstance().getPlayers()[1].getPoints()));
        winPlayer.setText(GameHandler.getInstance().getWinPlayer());
    }
}
