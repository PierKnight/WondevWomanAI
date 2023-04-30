package com.afgc.wondevwoman.graphic;

import javafx.scene.image.Image;

public class ImageManager {

    public static final Image[] tiles = new Image[]{
            loadImage("tile1.png"),
            loadImage("tile2.png"),
            loadImage("tile3.png"),
            loadImage("tile4.png"),
            loadImage("tile5.png")
    };

    public static final Image[] players = new Image[] {
            loadImage("player0.png"),
            loadImage("player1.png")
    };

    public static final Image border = loadImage("border.png");


    public static Image loadImage(String fileName) {
        return new Image(ImageManager.class.getResource(fileName).toExternalForm(), true);
    }
}
