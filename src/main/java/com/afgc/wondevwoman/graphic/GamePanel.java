package com.afgc.wondevwoman.graphic;

import javafx.geometry.Pos;
import javafx.scene.layout.StackPane;

// todo: singleton (?)
public class GamePanel extends StackPane {

    public final Tile[][] tiles = new Tile[10][10];
    private final double tileSize = 500 / 10;

    public GamePanel() {
        this.setAlignment(Pos.TOP_LEFT);

        this.setWidth(500);
        this.setHeight(500);
        this.setMaxWidth(500);


        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                Tile tile = new Tile(this.getTileSize());
                tile.setTranslateX(i * tileSize);
                tile.setTranslateY(j * tileSize);
                this.getChildren().add(tile);

                this.tiles[i][j] = tile;
            }
        }
    }

    public double getTileSize()
    {
        return this.tileSize;
    }

    public Tile getTile(int x, int y) {
        return tiles[x][y];
    }
}
