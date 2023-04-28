package com.afgc.wondevwoman;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Tile extends ImageView {
    private static final Image tileImage = new Image("https://github.com/eulerscheZahl/WondevWomanUI/blob/master/img/tile1.png?raw=true",true);


    private static final Image[] images = new Image[]{
            new Image("https://github.com/eulerscheZahl/WondevWomanUI/blob/master/img/tile1.png?raw=true",true),
            new Image("https://github.com/eulerscheZahl/WondevWomanUI/blob/master/img/tile2.png?raw=true",true),
            new Image("https://github.com/eulerscheZahl/WondevWomanUI/blob/master/img/tile3.png?raw=true",true),
            new Image("https://github.com/eulerscheZahl/WondevWomanUI/blob/master/img/tile4.png?raw=true",true),
            new Image("https://github.com/eulerscheZahl/WondevWomanUI/blob/master/img/tile5.png?raw=true",true)
    };
    private int level = 0;

    public Tile(double tileSize)
    {
        this.setFitWidth(tileSize);
        this.setFitHeight(tileSize);
        this.setOnMouseClicked(event -> this.setVisible(false));
        this.setImage(tileImage);

    }


    public boolean levelUp()
    {
        if(level >= 4)
            return false;
        this.level += 1;
        this.setImage(images[this.level]);
        return true;
    }
}
