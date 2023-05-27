package com.afgc.wondevwoman.controller;

import com.afgc.wondevwoman.MapRegistry;
import com.afgc.wondevwoman.Settings;
import com.afgc.wondevwoman.asp.EmbASPHandler;
import com.afgc.wondevwoman.graphic.GamePanel;
import com.afgc.wondevwoman.graphic.SceneHandler;
import com.afgc.wondevwoman.move.GameStatus;
import com.afgc.wondevwoman.move.MoveProvider;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.util.StringConverter;

public class MenuController {


    @FXML
    private ChoiceBox<MoveProvider> player1;

    @FXML
    private ChoiceBox<MoveProvider> player2;

    @FXML
    private ChoiceBox<GameStatus> map;

    @FXML
    private CheckBox pointMode;



    public void initialize() {

        for (MoveProvider provider : EmbASPHandler.getInstance().getProviders()) {
            player1.getItems().add(provider);
            player2.getItems().add(provider);
        }

        for (GameStatus gameMap : MapRegistry.getGameMaps())
            map.getItems().add(gameMap);
        map.getSelectionModel().select(0);


        player1.getItems().add(null);
        player2.getItems().add(null);
        player1.setConverter(new MoveConverter());
        player2.setConverter(new MoveConverter());

        map.setConverter(new StringConverter<GameStatus>() {
            @Override
            public String toString(GameStatus object) {
                return object.name();
            }

            @Override
            public GameStatus fromString(String string) {
                return null;
            }
        });
    }


    @FXML
    public void onPlay(ActionEvent event)
    {



        GamePanel gamePanel = GameHandler.getInstance().initGameBoard(this.pointMode.isSelected(),this.map.getValue(),player1.getValue(),player2.getValue());
        gamePanel.requestFocus();
        SceneHandler.getInstance().loadGame(gamePanel);
    }

    private class MoveConverter extends StringConverter<MoveProvider> {
        @Override
        public String toString(MoveProvider object) {

            return object == null ? "Umano" : object.getName();
        }

        @Override
        public MoveProvider fromString(String string) {
            return null;
        }
    }
}
