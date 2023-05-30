package com.afgc.wondevwoman.controller;

import com.afgc.wondevwoman.Settings;
import com.afgc.wondevwoman.graphic.GamePanel;
import com.afgc.wondevwoman.graphic.Pawn;
import com.afgc.wondevwoman.graphic.SceneHandler;
import com.afgc.wondevwoman.graphic.Tile;
import com.afgc.wondevwoman.move.GameStatus;
import com.afgc.wondevwoman.move.Move;
import com.afgc.wondevwoman.move.MoveProvider;
import com.afgc.wondevwoman.move.Player;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.binding.DoubleBinding;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
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
    private String winPlayer;
    private boolean versionePunti = false;

    private final Service<Move> moveService = new Service<>() {
        @Override
        protected Task<Move> createTask() {
            //this is called only on the javafx thread, so this is thread safe
            Player player = getCurrentPlayer();
            return new Task<>() {
                @Override
                protected Move call() throws Exception {
                    //here this service will wait for a move given a generic player
                    return player.getMoveProvider().getMove(player);
                }
            };
        }
    };

    private GameHandler() {
        winPlayer = "";
        this.moveService.setExecutor(Executors.newFixedThreadPool(1));
        this.moveService.setOnSucceeded(event -> {
            //if the player moved go to the next turn otherwise reset the service
            Move moveResult = moveService.getValue();

            if(moveResult == null) {
                System.out.println("MOVE NOT FOUND SKIPPING TURN");
                this.nextTurn();
                return;
            }
            Pawn selectedPawn = getCurrentPlayer().getPawns()[moveResult.getPawn()];

            if(selectedPawn.move(moveResult.getMoveX(),moveResult.getMoveY())) {
                if(this.updateTile(selectedPawn,moveResult.getPlaceX(), moveResult.getPlaceY()))
                    nextTurn();
            }
            else if(!Settings.SKIP_TURN_WITH_ILLEGAL_MOVE){
                System.out.println("YOU CANNOT DO THIS MOVE!" + getCurrentPlayer().getName());
                this.moveService.restart();
            }
            else {
                System.out.println("ILLEGAL MOVE " + moveResult + " BY " + getCurrentPlayer().getName() + ", SKIPPING TURN");
                nextTurn();
            }
        });

        gameTimer = new Timeline();
        gameTimer.setCycleCount(Timeline.INDEFINITE);
        gameTimer.getKeyFrames().add(
                new KeyFrame(Duration.seconds(1), event -> {
                    turnSeconds--;
                    // update timerLabel
                    this.gamePanel.updateTime(this.turnSeconds);
                    if (turnSeconds <= 0) {
                        gameTimer.stop();
                        nextTurn();
                    }
                }));
    }

    public GamePanel getMyGamePanel() {
        return this.gamePanel;
    }

    private Pawn[] createPawns(int player, int[][] pawnPositions)
    {
        Pawn[] pawns = new Pawn[2];
        for (int i = 0; i < pawns.length; i++) {
            pawns[i] = new Pawn(this, player,i);
            pawns[i].move(pawnPositions[i][0],pawnPositions[i][1]);

            DoubleBinding binding = this.getMyGamePanel().getBoard().widthProperty().divide(Settings.TILES);

            pawns[i].minWidthProperty().bind(binding);
            pawns[i].maxWidthProperty().bind(binding);
            pawns[i].minHeightProperty().bind(binding);
            pawns[i].maxHeightProperty().bind(binding);
            this.gamePanel.getBoard().getChildren().add(pawns[i]);
        }
        return pawns;
    }

    public boolean updateTile(Pawn pawn,int x, int y) {
        if(!this.isGameStarted())
            return false;

        if(!this.isSafePosition(pawn,x,y))
            return false;
        this.gamePanel.getTile(x, y).levelUp();
        return true;
    }

    private void nextTurn()
    {
        if(this.checkForVictory())
            return;

        turn += 1;
        this.gamePanel.updateTurn(this.turn);
        gameTimer.playFromStart();
        if(!this.getCurrentPlayer().isHumanPlayer())
            this.moveService.restart();
        turnSeconds = Settings.SECONDS_PER_TURN;
        this.gamePanel.updateTime(turnSeconds);


    }

    public Player getCurrentPlayer()
    {
        return this.turn < 0 ? null : this.players[this.turn % 2];
    }

    public Player[] getPlayers() {
        return players;
    }

    public String getWinPlayer() {
        return winPlayer;
    }

    public boolean isSafePosition(Pawn currentPawn, int cellX, int cellY)
    {
        if(cellX < 0 || cellX >= Settings.TILES ||  cellY < 0 || cellY >= Settings.TILES)
            return false;

        if(this.isGameStarted()) {
            for (Player player : players)
                if (player != null)
                    for (Pawn pawn : player.getPawns())
                        if (pawn.getX() == cellX && pawn.getY() == cellY)
                            return false;
            if(Math.pow(currentPawn.getX() - cellX,2) > 1 || Math.pow(currentPawn.getY() - cellY,2) > 1)
                return false;
        }

        return this.gamePanel.getTile(cellX, cellY).getLevel() < 4;
    }

    public GamePanel initGameBoard(boolean pointMode,GameStatus gameStatus, MoveProvider player1, MoveProvider player2)
    {
        this.humanMovePhase = HumanMovePhase.PAWN;
        this.turn = -1;
        this.getMyGamePanel().clearGamePanel(gameStatus.map());
        this.players[0] = new Player("Player1",createPawns(0,gameStatus.player1Pawns()),player1,0);
        this.players[1] = new Player("Player2",createPawns(1,gameStatus.player2Pawns()),player2,0);
        this.versionePunti = pointMode;
        this.nextTurn();


        return this.gamePanel;
    }

    public void checkForPoint(Pawn pawn)
    {
        if(this.getMyGamePanel().tiles[pawn.getX()][pawn.getY()].getLevel() == 3)
        {
            players[pawn.getPlayer()].setPoints(players[pawn.getPlayer()].getPoints()+1);
            if(this.turn % 2 == 0)
                this.gamePanel.updatePlayer1(players[pawn.getPlayer()].getPoints());
            else
                this.gamePanel.updatePlayer2(players[pawn.getPlayer()].getPoints());
            System.out.println(players[pawn.getPlayer()].getName() + " has earned a point!");
            System.out.println(players[pawn.getPlayer()].getName() + ": " + players[pawn.getPlayer()].getPoints());
        }

    }

    public boolean checkForVictory()
    {

        if(versionePunti) {
            int pawnBlocked = 0;

            for(int i = 0; i < 2; i++) {
                for(Pawn pawn: players[i].getPawns())
                    if(isPawnBlocked(pawn))
                        pawnBlocked++;
            }

            if(pawnBlocked < 4)
                return false;
        }
        else {
            if(!this.isGameStarted() || (this.getCurrentPlayer() != null && this.getCurrentPlayer().getPoints() == 0))
                return false;
        }


        System.out.println(players[0].getName() + ": " + players[0].getPoints());
        System.out.println(players[1].getName() + ": " + players[1].getPoints());

        if(players[0].getPoints() > players[1].getPoints()) {
            winPlayer = "Vince il giocatore 1!";
            System.out.println(players[0].getName() + " won!");
        }

        else if(players[0].getPoints() < players[1].getPoints()) {
            winPlayer = "Vince il giocatore 2!";
            System.out.println(players[1].getName() + " won!");
        }

        else {
            winPlayer = "Pareggio";
            System.out.println("Draw");
        }


        gameTimer.stop();
        moveService.cancel();
        this.turn = -1;
        SceneHandler.getInstance().loadWinPanel();

        return true;
    }

    private boolean isPawnBlocked(Pawn pawn)
    {
        Tile currentTile = this.gamePanel.getTile(pawn.getX(), pawn.getY());

        for(int i = -1; i <= 1; i++)
            for(int j = -1; j <= 1; j++)
                if((i != 0 || j != 0) && isSafePosition(pawn, pawn.getX()+i, pawn.getY()+j) &&
                        (this.gamePanel.getTile(pawn.getX()+i, pawn.getY()+j).getLevel() <= currentTile.getLevel()+1))
                    return false;
        return true;
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
