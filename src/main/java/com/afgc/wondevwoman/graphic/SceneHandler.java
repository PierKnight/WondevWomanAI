package com.afgc.wondevwoman.graphic;

import com.afgc.wondevwoman.Main;
import com.afgc.wondevwoman.controller.GameHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class SceneHandler {


    private static final SceneHandler INSTANCE = new SceneHandler();

    private SceneHandler(){}

    private Parent MENU = loadScene("menu.fxml");

    private AnchorPane mainNode;

    private Stage winPanelStage;


    public static final GameHandler GAME_HANDLER = GameHandler.getInstance();

    public static SceneHandler getInstance() {
        return INSTANCE;
    }


    private Parent loadScene(String file)
    {
        try {
            return FXMLLoader.load(Objects.requireNonNull(Main.class.getResource("graphic/fxml/" + file)));
        } catch (IOException e) {
            System.out.println("FAILED TO LOAD NODE: " + file);
            e.printStackTrace();
        }
        return null;
    }

    public void loadGame(GamePanel gamePanel)
    {
        this.mainNode.getChildren().set(0,gamePanel);
        AnchorPane.setLeftAnchor(gamePanel,0.0);
        AnchorPane.setRightAnchor(gamePanel,0.0);
    }
    public void loadMenu()
    {
        this.mainNode.getChildren().set(0,MENU);
    }

    public void init(Stage stage)
    {
        mainNode = new AnchorPane();
        AnchorPane.setLeftAnchor(MENU,0.0);
        AnchorPane.setRightAnchor(MENU,0.0);
        mainNode.getChildren().add(MENU);


        Scene menuScene = new Scene(mainNode, 320, 240);
        stage.setTitle("WondevWoman");
        stage.setScene(menuScene);
        stage.setWidth(500);
        stage.setHeight(530);
        stage.setResizable(true);
        stage.show();
        GAME_HANDLER.getMyGamePanel().requestFocus();
    }


    public void loadWinPanel() {
        winPanelStage = new Stage();
        winPanelStage.setTitle("FINE PARTITA");
        winPanelStage.setScene(new Scene(loadScene("winPanel.fxml")));
        winPanelStage.show();
    }

    public void closeWinPanel() {
        winPanelStage.close();
    }
}
