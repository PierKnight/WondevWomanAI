package com.afgc.wondevwoman;

import com.afgc.wondevwoman.graphic.GamePanel;
import com.afgc.wondevwoman.graphic.Pawn;
import com.afgc.wondevwoman.move.*;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.binding.DoubleBinding;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
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
    private final Player[] players = new Player[2];

    private int turn = -1;
    private int turnSeconds;
    private final Timeline gameTimer;
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
                    return player.moveProvider().getMove(player);
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
            Pawn selectedPawn = getCurrentPlayer().pawns()[moveResult.getPawn()];

            if(isLevelUpLegal && selectedPawn.move(moveResult.getDirX(),moveResult.getDirY())) {
                checkForVictory(selectedPawn);
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
        timerLabel = new Label();
        timerLabel.setViewOrder(1);
        gameTimer = new Timeline();
        gameTimer.setCycleCount(Timeline.INDEFINITE);
        gameTimer.getKeyFrames().add(
                new KeyFrame(Duration.seconds(1), event -> {
                    turnSeconds--;
                    // update timerLabel
                    timerLabel.setText(String.valueOf(turnSeconds));
                    if (turnSeconds <= 0) {
                        gameTimer.stop();
                        nextTurn();
                    }
                }));
        this.gamePanel.getChildren().add(timerLabel);
        this.initGameBoard();
    }

    public GamePanel getMyGamePanel() {
        return this.gamePanel;
    }

    private Pawn[] createPawns(int player)
    {
        Pawn[] pawns = new Pawn[2];
        for (int i = 0; i < pawns.length; i++) {
            pawns[i] = new Pawn(this, player,i);
            pawns[i].move(i * 9, player * 9);

            DoubleBinding binding = this.getMyGamePanel().widthProperty().divide(Settings.TILES);

            pawns[i].minWidthProperty().bind(binding);
            pawns[i].maxWidthProperty().bind(binding);
            pawns[i].minHeightProperty().bind(binding);
            pawns[i].maxHeightProperty().bind(binding);
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
        gameTimer.playFromStart();
        this.moveService.restart();
        turnSeconds = Settings.SECONDS_PER_TURN;
        timerLabel.setText(String.valueOf(turnSeconds));


        //getCurrentPlayer().toggleBorder();
    }

    public Player getCurrentPlayer()
    {
        return this.turn < 0 ? null : this.players[this.turn % 2];
    }

    public Player[] getPlayers() {
        return players;
    }

    public boolean isSafePosition(int cellX, int cellY)
    {
        if(this.turn >= 0)
        for(Player player : players)
            if(player != null)
            for (Pawn pawn : player.pawns())
                if(pawn.getX() == cellX && pawn.getY() == cellY)
                    return false;

        if(cellX < 0 || cellX >= 10 ||  cellY < 0 || cellY >= 10)
            return false;

        return this.gamePanel.getTile(cellX, cellY).getLevel() < 4;
    }

    private void initGameBoard()
    {
        this.turn = -1;
        this.getMyGamePanel().clearGamePanel();
        this.players[0] = new Player(createPawns(0), Settings.FIRST_PLAYER);
        this.players[1] = new Player(createPawns(1),Settings.SECOND_PLAYER);
        this.nextTurn();
    }

    private void checkForVictory(Pawn pawn)
    {
        if(this.getMyGamePanel().tiles[pawn.getX()][pawn.getY()].getLevel() == 3)
        {
            Stage stage = new Stage();
            stage.setTitle("FINE PARTITA");
            gameTimer.stop();

            Button button = new Button("click me");
            button.setOnMouseClicked(event ->
            {
                GameHandler.this.initGameBoard();
                stage.close();
            });

            Scene scenes = new Scene(button);
            stage.setScene(scenes);
            stage.show();
        }
    }

}
