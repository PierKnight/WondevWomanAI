package com.afgc.wondevwoman.graphic;

import com.afgc.wondevwoman.GameHandler;
import com.afgc.wondevwoman.Settings;
import javafx.animation.TranslateTransition;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

public class Pawn extends StackPane {

    public int x;
    public int y;
    public TranslateTransition transition =  new TranslateTransition(Duration.millis(150),this);

    private final GameHandler gameHandler;
    private final ImageView border;

    public Pawn(GameHandler gameHandler, int team)
    {
        this.gameHandler = gameHandler;
        ImageView imageView = new ImageView(ImageManager.players[team]);
        imageView.setFitHeight(Settings.HEIGHT);
        imageView.setFitWidth(Settings.WIDTH);

        border = new ImageView(ImageManager.border);
        border.setFitHeight(Settings.HEIGHT);
        border.setFitWidth(Settings.WIDTH);

        this.getChildren().addAll(imageView, border);
        this.setPrefSize(Settings.WIDTH, Settings.HEIGHT);
        this.setMaxSize(Settings.WIDTH, Settings.HEIGHT);
        this.toggleBorder(); // rendo invisibile il bordo all'inizio del gioco
    }

    public void toggleBorder() {
        border.setVisible(!border.isVisible());
    }

    public boolean move(int dirX,int dirY)
    {
        if(!gameHandler.isSafePosition(x + dirX,y + dirY))
            return false;

        transition.setFromX(  x * Settings.WIDTH);
        transition.setFromY(  y * Settings.HEIGHT);

        transition.setToX( (x + dirX) * Settings.WIDTH);
        transition.setToY( (y + dirY) * Settings.HEIGHT);
        transition.playFromStart();
        x += dirX;
        y += dirY;

        return true;
    }
}
