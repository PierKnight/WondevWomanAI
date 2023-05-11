package com.afgc.wondevwoman.graphic;

import com.afgc.wondevwoman.GameHandler;
import com.afgc.wondevwoman.Main;
import com.afgc.wondevwoman.Settings;
import it.unical.mat.embasp.languages.Id;
import it.unical.mat.embasp.languages.Param;
import javafx.animation.Transition;
import javafx.animation.TranslateTransition;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

public class Pawn extends StackPane {

    private int x;
    private int y;
    private final int pawnNumber;


    private final GameHandler gameHandler;
    private final ImageView border;

    public double renderX;
    public double renderY;

    private final RenderTransition transition = new RenderTransition();

    public Pawn(GameHandler gameHandler, int team,int pawnNumber)
    {
        this.gameHandler = gameHandler;
        this.pawnNumber = pawnNumber;
        ImageView imageView = new ImageView(ImageManager.players[team]);

        imageView.fitWidthProperty().bind(this.widthProperty());
        imageView.fitHeightProperty().bind(this.heightProperty());

        border = new ImageView(ImageManager.border);

        border.fitWidthProperty().bind(this.prefWidthProperty());
        border.fitHeightProperty().bind(this.prefHeightProperty());

        this.getChildren().addAll(imageView, border);
        this.toggleBorder(); // rendo invisibile il bordo all'inizio del gioco

        //aggiorno la posizione al variare della grandezza della board
        gameHandler.getMyGamePanel().widthProperty().addListener((observable, oldValue, newValue) -> {
            Pawn.this.setTranslateX(renderX * newValue.doubleValue() / Settings.TILES);
            Pawn.this.setTranslateY(renderY * newValue.doubleValue() / Settings.TILES);
        });

    }

    public void toggleBorder() {
        border.setVisible(!border.isVisible());
    }

    public boolean move(int dirX,int dirY)
    {
        if(!gameHandler.isSafePosition(x + dirX,y + dirY))
            return false;


        int currentLevel = gameHandler.getMyGamePanel().tiles[x][y].getLevel();
        int nextLevel = gameHandler.getMyGamePanel().tiles[x + dirX][y + dirY].getLevel();
        if(nextLevel - currentLevel > 1)
            return false;

        transition.fromX = x;
        transition.fromY = y;

        transition.toX = (x + dirX);
        transition.toY =  (y + dirY);
        transition.playFromStart();

        x += dirX;
        y += dirY;

        return true;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }


    public String getFact(boolean isEnemy)
    {
        String teamFact = isEnemy ? "you" : "enemy";
        return "pawn(" + this.x + "," + this.y + "," + this.pawnNumber + "," + teamFact + ").";
    }

    private class RenderTransition extends Transition
    {

        private double fromX;
        private double fromY;
        private double toX;
        private double toY;

        private RenderTransition()
        {
            this.setCycleDuration(Duration.millis(150));
        }

        @Override
        protected void interpolate(double frac) {
            Pawn.this.renderX = fromX * (1F - frac) + toX * frac;
            Pawn.this.renderY = fromY * (1F - frac) + toY * frac;
            Pawn.this.setTranslateX(renderX * Pawn.this.gameHandler.getMyGamePanel().getWidth() / Settings.TILES);
            Pawn.this.setTranslateY(renderY * Pawn.this.gameHandler.getMyGamePanel().getWidth() / Settings.TILES);
        }
    }
}
