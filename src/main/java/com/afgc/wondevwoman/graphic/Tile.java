package com.afgc.wondevwoman.graphic;

import com.afgc.wondevwoman.controller.GameHandler;
import it.unical.mat.embasp.languages.Id;
import it.unical.mat.embasp.languages.Param;
import javafx.animation.Interpolator;
import javafx.animation.ScaleTransition;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

@Id("tile")
public class Tile extends ImageView {

    @Param(0)
    private int posX;
    @Param(1)
    private int posY;
    @Param(2)
    private int level = 0;


    private ScaleTransition levelUpTransition;

    public Tile(int x, int y)
    {
        this.setOnMouseClicked(event -> this.setVisible(false));
        this.setImage(ImageManager.tiles[0]);
        this.levelUpTransition = new ScaleTransition(Duration.millis(100),this);
        this.levelUpTransition.setFromX(1);
        this.levelUpTransition.setFromY(1);
        this.levelUpTransition.setToX(1.5);
        this.levelUpTransition.setToY(1.5);
        this.levelUpTransition.setCycleCount(2);
        this.levelUpTransition.setAutoReverse(true);
        this.levelUpTransition.setOnFinished((event -> this.setViewOrder(0)));
        this.levelUpTransition.setInterpolator(Interpolator.EASE_OUT);
        this.posX = x;
        this.posY = y;
        this.setOnMouseClicked(event -> GameHandler.getInstance().onTileClicked(Tile.this));
    }

    public Tile(){}


    public void levelUp()
    {
        if(level >= 4)
            return;
        this.level += 1;
        this.setImage(ImageManager.tiles[this.level]);
        this.levelUpTransition.playFromStart();
        this.setViewOrder(-1);
    }

    public int getPosX() {
        return posX;
    }

    public void setPosX(int posX) {
        this.posX = posX;
    }

    public int getPosY() {
        return posY;
    }

    public void setPosY(int posY) {
        this.posY = posY;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
        this.setImage(ImageManager.tiles[this.level]);
    }
}
