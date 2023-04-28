package com.afgc.wondevwoman;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.util.Duration;

import java.util.Random;
import java.util.concurrent.Executors;

public class GamePanel extends StackPane {



    private final Player[] players = new Player[2];

    public final Tile[][] tiles = new Tile[10][10];

    private int turn = -1;
    private int turnSeconds;
    private final Timeline timer;
    private final Label timerLabel;


    private final Service<Integer> moveService = new Service<Integer>() {
        @Override
        protected Task<Integer> createTask() {
            //this is called only on the javafx thread, so this is thread safe
            IPlayer player = getCurrentPlayer().player;
            return new Task<>() {
                @Override
                protected Integer call() throws Exception {
                    //here this service will wait for a move given a generic player
                    return player.getMove();
                }
            };
        }
    };


    private int currentMove = 0;
    public GamePanel()
    {
        this.moveService.setExecutor(Executors.newFixedThreadPool(1));
        this.moveService.setOnSucceeded(event -> {
            //if the player moved go to the next turn otherwise reset the service
            if(getCurrentPlayer().move(moveService.getValue(),0))
                nextTurn();
            else {

                System.out.println("YOU CANNOT DO THIS MOVE!" + this.turn % 2);
                this.moveService.restart();
            }
        });

        this.setAlignment(Pos.TOP_LEFT);


        this.setWidth(500);
        this.setHeight(500);
        this.setMaxWidth(500);


        double tileSize = 500 / 10;


        for(int i = 0;i < 10;i++)
        {
            for (int j = 0; j < 10; j++) {
                Tile tile = new Tile(this.tileSize());
                tile.setTranslateX(i * tileSize);
                tile.setTranslateY(j * tileSize);
                this.getChildren().add(tile);

                this.tiles[i][j] = tile;
            }
        }


        players[0] = new Player(this, new HumanPlayer());
        players[1] = new Player(this, new HumanPlayer());
        players[1].move(5,3);

        for (Player player : this.players)
            this.getChildren().add(player);


        timerLabel = new Label();
        timer = new Timeline();
        timer.setCycleCount(Timeline.INDEFINITE);

        // KeyFrame event handler
        timer.getKeyFrames().add(
                new KeyFrame(Duration.seconds(1), event -> {
                    turnSeconds--;
                    // update timerLabel
                    timerLabel.setText(String.valueOf(turnSeconds));
                    if (turnSeconds <= 0) {

                        timer.stop();
                        nextTurn();
                    }
                }));
        nextTurn();
        this.getChildren().add(timerLabel);
    }


    void makeMove()
    {
        if(this.currentMove == 0)
            this.getCurrentPlayer().move(1,0);
        else if(this.currentMove == 1) {
            this.tiles[0][0].levelUp();
            nextTurn();
        }
    }

    private void nextTurn()
    {

        if(turn >= 0)
        getCurrentPlayer().setScaleY(1);
        turn += 1;
        timer.playFromStart();
        this.moveService.restart();
        turnSeconds = 10;
        timerLabel.setText(String.valueOf(turnSeconds));
        this.currentMove = 0;


        getCurrentPlayer().setScaleY(2);


    }

    public double tileSize()
    {
       return 500 / 10;
    }

    public Player getCurrentPlayer()
    {
        return this.players[this.turn % 2];
    }


    public boolean isSafePosition(int cellX, int cellY)
    {
        for (Player player : this.players)
            if(player.x == cellX && player.y == cellY)
                return false;

        return cellX >= 0 && cellX < 10 && cellY >= 0 && cellY < 10;
    }

}
