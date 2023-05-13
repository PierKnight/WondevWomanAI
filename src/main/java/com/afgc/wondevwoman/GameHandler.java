package com.afgc.wondevwoman;

import com.afgc.wondevwoman.graphic.GamePanel;
import com.afgc.wondevwoman.graphic.Pawn;
import com.afgc.wondevwoman.graphic.Tile;
import com.afgc.wondevwoman.move.Move;
import com.afgc.wondevwoman.move.Player;
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

            if(moveResult == null) {
                System.out.println("MOVE NOT FOUND SKIPPING TURN");
                this.nextTurn();
                return;
            }
            Pawn selectedPawn = getCurrentPlayer().pawns()[moveResult.getPawn()];

            if(selectedPawn.move(moveResult.getMoveX(),moveResult.getMoveY())) {
                if(this.updateTile(selectedPawn,moveResult.getPlaceX(), moveResult.getPlaceY()))
                    nextTurn();
            }
            else if(!Settings.SKIP_TURN_WITH_ILLEGAL_MOVE){
                System.out.println("YOU CANNOT DO THIS MOVE!" + getCurrentPlayer().name());
                this.moveService.restart();
            }
            else {
                System.out.println("ILLEGAL MOVE " + moveResult + " BY " + getCurrentPlayer().name() + ", SKIPPING TURN");
                nextTurn();
            }
        });
        timerLabel = new Label();
        timerLabel.setViewOrder(-1);
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

    public boolean updateTile(Pawn pawn,int x, int y) {
        if(!this.isSafePosition(pawn,x,y))
            return false;
        this.gamePanel.getTile(x, y).levelUp();
        return true;
    }

    private void nextTurn()
    {
        //do random moves if human player didn't complete the turn in time
        //if(this.humanMovePhase != HumanMovePhase.PAWN)

        turn += 1;
        gameTimer.playFromStart();
        if(!this.getCurrentPlayer().isHumanPlayer())
            this.moveService.restart();
        turnSeconds = Settings.SECONDS_PER_TURN;
        timerLabel.setText(String.valueOf(turnSeconds));
    }

    public Player getCurrentPlayer()
    {
        return this.turn < 0 ? null : this.players[this.turn % 2];
    }

    public Player[] getPlayers() {
        return players;
    }

    public boolean isSafePosition(Pawn currentPawn,int cellX, int cellY)
    {
        if(this.isGameStarted()) {
            for (Player player : players)
                if (player != null)
                    for (Pawn pawn : player.pawns())
                        if (pawn.getX() == cellX && pawn.getY() == cellY)
                            return false;
            if(Math.pow(currentPawn.getX() - cellX,2) > 1 || Math.pow(currentPawn.getY() - cellY,2) > 1)
                return false;
        }
        if(cellX < 0 || cellX >= 10 ||  cellY < 0 || cellY >= 10)
            return false;


        return this.gamePanel.getTile(cellX, cellY).getLevel() < 4;
    }

    private void initGameBoard()
    {
        this.humanMovePhase = HumanMovePhase.PAWN;
        this.turn = -1;
        this.getMyGamePanel().clearGamePanel();
        this.getMyGamePanel().getChildren().add(timerLabel);
        this.players[0] = new Player("Player1",createPawns(0), Settings.FIRST_PLAYER);
        this.players[1] = new Player("Player2",createPawns(1),Settings.SECOND_PLAYER);
        this.nextTurn();
    }

    private void checkForVictory(Pawn pawn)
    {
        if(this.getMyGamePanel().tiles[pawn.getX()][pawn.getY()].getLevel() == 3)
        {
            Stage stage = new Stage();
            stage.setTitle("FINE PARTITA");
            gameTimer.stop();
            this.turn = -1;
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

    public boolean isGameStarted() {
        return this.turn >= 0;
    }

    /**
     * HUMAN MOVE SECTION
     */

    private HumanMovePhase humanMovePhase = HumanMovePhase.PAWN;
    private Pawn selectedPawn;

    public void onPawnClicked(Pawn pawn){

        if(this.isGameStarted() && this.getCurrentPlayer().isHumanPlayer() && this.humanMovePhase == HumanMovePhase.PAWN)
        {
            //with this you can only select the current player pawn
            if(this.players[pawn.getPlayer()] == getCurrentPlayer()) {
                this.selectedPawn = pawn;
                this.selectedPawn.toggleBorder();
                humanMovePhase = humanMovePhase.nextPhase();
            }
        }
    }

    public void onTileClicked(Tile tile){
        if(this.isGameStarted() && this.getCurrentPlayer().isHumanPlayer())
        {

            if(humanMovePhase == HumanMovePhase.MOVE) {
                if(this.selectedPawn.move(tile.getPosX(), tile.getPosY())) {
                    checkForVictory(selectedPawn);
                    humanMovePhase = humanMovePhase.nextPhase();
                }
            }
            else if(humanMovePhase == HumanMovePhase.LEVEL)
            {
                if(this.updateTile(selectedPawn,tile.getPosX(),tile.getPosY())) {
                    humanMovePhase = humanMovePhase.nextPhase();
                    this.selectedPawn.toggleBorder();
                    this.nextTurn();
                }
            }
        }
    }

    enum HumanMovePhase
    {
        PAWN,
        MOVE,
        LEVEL;

        public HumanMovePhase nextPhase()
        {
            switch (this)
            {
                case PAWN -> {return MOVE;}
                case MOVE -> {return LEVEL;}
                case LEVEL -> {return PAWN;}
            }
            return null;
        }
    }

}
