package com.afgc.wondevwoman.graphic;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Tile extends ImageView {

    public int getLevel() {
        return level;
    }

    private int level = 0;

    public Tile(double tileSize, int x, int y)
    {
        this.setFitWidth(tileSize);
        this.setFitHeight(tileSize);
        this.setOnMouseClicked(event -> this.setVisible(false));
        this.setImage(ImageManager.tiles[0]);
    }


    public boolean levelUp()
    {
        if(level >= 4)
            return false;
        this.level += 1;
        this.setImage(ImageManager.tiles[this.level]);
        return true;
    }
}
