package com.afgc.wondevwoman.graphic;

import com.afgc.wondevwoman.Settings;
import javafx.beans.binding.DoubleBinding;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

// todo: singleton (?)
public class GamePanel extends VBox {

    public final Tile[][] tiles = new Tile[Settings.TILES][Settings.TILES];

    private final ImageView background;
    private final StackPane board;


    private final Label timerLabel;
    private final Label turnLabel;
    private final Label player1Points;
    private final Label player2Points;

    public GamePanel() {
        this.setAlignment(Pos.TOP_CENTER);
        this.background = new ImageView();
        this.background.setImage(ImageManager.background);
        this.board = new StackPane();
        this.board.setAlignment(Pos.TOP_LEFT);

        HBox scoreboard = new HBox();
        scoreboard.setSpacing(20);
        scoreboard.setAlignment(Pos.TOP_CENTER);
        this.setPadding(new Insets(10));

        timerLabel = new Label();

        turnLabel = new Label();

        player1Points = new Label();
        player1Points.setTextFill(Color.ORANGE);

        player2Points = new Label();
        player2Points.setTextFill(Color.RED);

        scoreboard.getChildren().add(this.player1Points);
        scoreboard.getChildren().add(this.player2Points);
        scoreboard.getChildren().add(this.timerLabel);
        scoreboard.getChildren().add(this.turnLabel);

        this.getChildren().add(scoreboard);
        this.getChildren().add(this.board);


    }

    public void clearGamePanel(int[][] tiles)
    {
        this.updatePlayer1(0);
        this.updatePlayer2(0);
        this.board.getChildren().clear();
        DoubleBinding binding = this.board.widthProperty().divide(Settings.TILES);

        for (int i = 0; i < Settings.TILES; i++) {
            for (int j = 0; j < Settings.TILES; j++) {
                Tile tile = new Tile(i,j);
                tile.setLevel(tiles[i][j]);
                tile.translateXProperty().bind(binding.multiply(i));
                tile.translateYProperty().bind(binding.multiply(j));
                tile.fitWidthProperty().bind(binding);
                tile.fitHeightProperty().bind(binding);
                this.board.getChildren().add(tile);
                this.tiles[i][j] = tile;
            }
        }
    }
    public Tile getTile(int x, int y) {
        return tiles[x][y];
    }

    public void updateTime(int time)
    {
        this.timerLabel.setText("Tempo: " + time);
    }

    public void updateTurn(int turn)
    {
        this.turnLabel.setText("Turno: " + turn);
    }

    public void updatePlayer1(int points)
    {
        this.player1Points.setText("Giocatore 1: " + points);
    }

    public void updatePlayer2(int points)
    {
        this.player2Points.setText("Giocatore 2: " + points);
    }

    public StackPane getBoard() {
        return board;
    }
}
