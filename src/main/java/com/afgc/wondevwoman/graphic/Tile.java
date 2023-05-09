package com.afgc.wondevwoman.graphic;

import it.unical.mat.embasp.languages.Id;
import it.unical.mat.embasp.languages.Param;
import javafx.scene.image.ImageView;

@Id("tile")
public class Tile extends ImageView {

    @Param(0)
    private int posX;
    @Param(1)
    private int posY;
    @Param(2)
    private int level = 0;

    public Tile(double tileSize, int x, int y)
    {
        this.setFitWidth(tileSize);
        this.setFitHeight(tileSize);
        this.setOnMouseClicked(event -> this.setVisible(false));
        this.setImage(ImageManager.tiles[0]);
        this.posX = x;
        this.posY = y;
    }

    public Tile(){}


    public void levelUp()
    {
        if(level >= 4)
            return;
        this.level += 1;
        this.setImage(ImageManager.tiles[this.level]);
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
    }
}
