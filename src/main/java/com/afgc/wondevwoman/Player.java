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

    private final GamePanel gamePanel;

    public Player(GamePanel gamePanel, IPlayer player)
    {
        this.gamePanel = gamePanel;
        Image image = new Image("https://cdn-icons-png.flaticon.com/512/1828/1828108.png",gamePanel.tileSize(),gamePanel.tileSize(),true,true,true);

        this.setImage(image);
        this.player = player;

    }


    public boolean move(int dirX,int dirY)
    {
        if(!gamePanel.isSafePosition(x + dirX,y + dirY))
            return false;

        gamePanel.tiles[x][y].levelUp();
        transition.setFromX(  x * gamePanel.tileSize());
        transition.setFromY(  y * gamePanel.tileSize());

        transition.setToX( (x + dirX) * gamePanel.tileSize());
        transition.setToY( (y + dirY) * gamePanel.tileSize());
        transition.playFromStart();
        x += dirX;
        y += dirY;

        return true;
    }


}
