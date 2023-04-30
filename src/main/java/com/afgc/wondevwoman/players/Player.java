package com.afgc.wondevwoman.players;

import com.afgc.wondevwoman.GameHandler;
import com.afgc.wondevwoman.graphic.ImageManager;
import com.afgc.wondevwoman.graphic.Size;
import javafx.animation.TranslateTransition;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

public class Player extends StackPane {

    public int x;
    public int y;
    public TranslateTransition transition =  new TranslateTransition(Duration.millis(150),this);

    public MoveProvider player;

    private final GameHandler gameHandler;

    private final ImageView border;

    public Player(GameHandler gameHandler, MoveProvider player, int number)
    {
        this.gameHandler = gameHandler;
        ImageView imageView = new ImageView(ImageManager.players[number]);
        imageView.setFitHeight(Size.HEIGHT);
        imageView.setFitWidth(Size.WIDTH);

        border = new ImageView(ImageManager.border);
        border.setFitHeight(Size.HEIGHT);
        border.setFitWidth(Size.WIDTH);

        this.getChildren().addAll(imageView, border);
        this.setPrefSize(Size.WIDTH, Size.HEIGHT);
        this.setMaxSize(Size.WIDTH, Size.HEIGHT);
        this.player = player;
        this.toggleBorder(); // rendo invisibile il bordo all'inizio del gioco
    }

    public void toggleBorder() {
        border.setVisible(!border.isVisible());
    }

    public boolean move(int dirX,int dirY)
    {
        if(!gameHandler.isSafePosition(x + dirX,y + dirY))
            return false;

        transition.setFromX(  x * Size.WIDTH);
        transition.setFromY(  y * Size.HEIGHT);

        transition.setToX( (x + dirX) * Size.WIDTH);
        transition.setToY( (y + dirY) * Size.HEIGHT);
        transition.playFromStart();
        x += dirX;
        y += dirY;

        return true;
    }
}
