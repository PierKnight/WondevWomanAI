package com.afgc.wondevwoman.graphic;

import com.afgc.wondevwoman.Settings;
import javafx.beans.binding.DoubleBinding;
import javafx.geometry.Pos;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

// todo: singleton (?)
public class GamePanel extends StackPane {

    public final Tile[][] tiles = new Tile[Settings.TILES][Settings.TILES];

    private final ImageView background;

    public GamePanel() {
        this.setAlignment(Pos.TOP_LEFT);
        this.background = new ImageView();
        this.background.setImage(ImageManager.background);
        this.setWidth(500);
        this.setHeight(500);
        this.clearGamePanel();
    }

    public void clearGamePanel()
    {
        this.getChildren().clear();
        this.getChildren().add(background);
        DoubleBinding binding = this.widthProperty().divide(Settings.TILES);

        for (int i = 0; i < Settings.TILES; i++) {
            for (int j = 0; j < Settings.TILES; j++) {
                Tile tile = new Tile(i,j);
                tile.translateXProperty().bind(binding.multiply(i));
                tile.translateYProperty().bind(binding.multiply(j));
                tile.fitWidthProperty().bind(binding);
                tile.fitHeightProperty().bind(binding);
                this.getChildren().add(tile);
                this.tiles[i][j] = tile;
            }
        }
    }
    public Tile getTile(int x, int y) {
        return tiles[x][y];
    }
}
