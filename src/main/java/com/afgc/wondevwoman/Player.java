package com.afgc.wondevwoman;

import javafx.animation.TranslateTransition;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

public class Player extends ImageView {

    public int x;
    public int y;
    public TranslateTransition transition =  new TranslateTransition(Duration.millis(150),this);

    public IPlayer player;

    private final GameHandler gameHandler;

    public Player(GameHandler gameHandler, IPlayer player)
    {
        this.gameHandler = gameHandler;
        Image image = new Image("https://cdn-icons-png.flaticon.com/512/1828/1828108.png", Size.WIDTH, Size.HEIGHT, true, true, true);

        this.setImage(image);
        this.player = player;

    }


    public boolean move(int dirX,int dirY)
    {
        if(!gameHandler.isSafePosition(x + dirX,y + dirY))
            return false;

        gameHandler.updateTile(x, y);

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
