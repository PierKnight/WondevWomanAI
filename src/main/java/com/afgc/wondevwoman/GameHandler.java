package com.afgc.wondevwoman;

import com.afgc.wondevwoman.graphic.GamePanel;
import com.afgc.wondevwoman.players.HumanPlayer;
import com.afgc.wondevwoman.players.Move;
import com.afgc.wondevwoman.players.MoveProvider;
import com.afgc.wondevwoman.players.Player;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.scene.control.Label;
import javafx.util.Duration;

import java.util.concurrent.Executors;

public class GameHandler {

    private static GameHandler instance = null;

    public static GameHandler getInstance() {
        if (instance == null)
            instance = new GameHandler();
        return instance;
    }

    private GamePanel gamePanel = new GamePanel();

    public GamePanel getMyGamePanel() {
        return this.gamePanel;
    }

    private final Player[] players = new Player[2];
    private int turn = -1;
    private int turnSeconds;
    private final Timeline timer;
    private final Label timerLabel;


    private final Service<Move> moveService = new Service<Move>() {
        @Override
        protected Task<Move> createTask() {
            //this is called only on the javafx thread, so this is thread safe
            MoveProvider player = getCurrentPlayer().player;
            return new Task<>() {
                @Override
                protected Move call() throws Exception {
                    //here this service will wait for a move given a generic player
                    return player.getMove();
                }
            };
        }
    };


    private int currentMove = 0;



    private GameHandler() {
        this.moveService.setExecutor(Executors.newFixedThreadPool(1));
        this.moveService.setOnSucceeded(event -> {
            //if the player moved go to the next turn otherwise reset the service
            Move moveResult = moveService.getValue();

            if(getCurrentPlayer().move(moveResult.dirX(),moveResult.dirY()) && this.updateTile(moveResult.placeX(), moveResult.placeY())) {
                nextTurn();
            }
            else {
                System.out.println("YOU CANNOT DO THIS MOVE!" + this.turn % 2);
                this.moveService.restart();
            }
        });


        players[0] = new Player(this, new HumanPlayer(),0);
        players[1] = new Player(this, new HumanPlayer(),1);
        players[1].move(5,3);

        for (Player player : this.players)
            this.gamePanel.getChildren().add(player);

        timerLabel = new Label();
        timer = new Timeline();
        timer.setCycleCount(Timeline.INDEFINITE);

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

        this.gamePanel.getChildren().add(timerLabel);
    }

    public boolean updateTile(int x, int y) {
        if(!this.isSafePosition(x,y))
            return false;

        this.gamePanel.getTile(x, y).levelUp();
        return true;
    }

    void makeMove()
    {
        if(this.currentMove == 0)
            this.getCurrentPlayer().move(1,0);
        else if(this.currentMove == 1) {
            this.gamePanel.getTile(0,0).levelUp();
            nextTurn();
        }
    }

    private void nextTurn()
    {
        if(turn >= 0)
            getCurrentPlayer().toggleBorder();

        turn += 1;
        timer.playFromStart();
        this.moveService.restart();
        turnSeconds = 10;
        timerLabel.setText(String.valueOf(turnSeconds));
        this.currentMove = 0;

        getCurrentPlayer().toggleBorder();
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

        if(cellX < 0 || cellX >= 10 ||  cellY < 0 || cellY >= 10)
            return false;

        if(this.gamePanel.getTile(cellX, cellY).getLevel() == 4)
            return false;

        return true;
    }

}
