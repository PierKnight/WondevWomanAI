package com.afgc.wondevwoman;

import com.afgc.wondevwoman.graphic.GamePanel;
import com.afgc.wondevwoman.graphic.Pawn;
import com.afgc.wondevwoman.move.*;
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

    private final GamePanel gamePanel = new GamePanel();

    public GamePanel getMyGamePanel() {
        return this.gamePanel;
    }

    private final Player[] players = new Player[2];

    private int turn = -1;
    private int turnSeconds;
    private final Timeline timer;
    private final Label timerLabel;


    private final Service<Move> moveService = new Service<>() {
        @Override
        protected Task<Move> createTask() {
            //this is called only on the javafx thread, so this is thread safe
            Player player = getCurrentPlayer();
            return new Task<>() {
                @Override
                protected Move call() throws Exception {
                    //here this service will wait for a move given a generic player
                    return player.moveProvider().getMove();
                }
            };
        }
    };

    private GameHandler() {
        this.moveService.setExecutor(Executors.newFixedThreadPool(1));
        this.moveService.setOnSucceeded(event -> {
            //if the player moved go to the next turn otherwise reset the service
            Move moveResult = moveService.getValue();

            boolean isLevelUpLegal = isSafePosition(moveResult.getPlaceX(),moveResult.getPlaceY());

            if(isLevelUpLegal && getCurrentPlayer().pawns()[moveResult.getPawn()].move(moveResult.getDirX(),moveResult.getDirY())) {
                if(this.updateTile(moveResult.getPlaceX(), moveResult.getPlaceY()))
                    nextTurn();
            }
            else if(!Settings.SKIP_TURN_WITH_ILLEGAL_MOVE){
                System.out.println("YOU CANNOT DO THIS MOVE!" + this.turn % 2);
                this.moveService.restart();
            }
            else
                nextTurn();
        });


        this.players[0] = new Player(createPawns(0), Settings.FIRST_PLAYER);
        this.players[1] = new Player(createPawns(1),Settings.SECOND_PLAYER);

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

    private Pawn[] createPawns(int player)
    {
        Pawn[] pawns = new Pawn[2];
        for (int i = 0; i < pawns.length; i++) {
            pawns[i] = new Pawn(this, player);
            pawns[i].move(i,player);
            this.gamePanel.getChildren().add(pawns[i]);
        }
        return pawns;
    }

    public boolean updateTile(int x, int y) {
        if(!this.isSafePosition(x,y))
            return false;

        this.gamePanel.getTile(x, y).levelUp();
        return true;
    }

    private void nextTurn()
    {
        //if(turn >= 0)
        //    getCurrentPlayer().toggleBorder();

        turn += 1;
        timer.playFromStart();
        this.moveService.restart();
        turnSeconds = 10;
        timerLabel.setText(String.valueOf(turnSeconds));

        //getCurrentPlayer().toggleBorder();
    }

    public Player getCurrentPlayer()
    {
        return this.players[this.turn % 2];
    }


    public boolean isSafePosition(int cellX, int cellY)
    {
        for(Player player : players)
            if(player != null)
            for (Pawn pawn : player.pawns())
                if(pawn.x == cellX && pawn.y == cellY)
                    return false;

        if(cellX < 0 || cellX >= 10 ||  cellY < 0 || cellY >= 10)
            return false;

        return this.gamePanel.getTile(cellX, cellY).getLevel() < 4;
    }

}
