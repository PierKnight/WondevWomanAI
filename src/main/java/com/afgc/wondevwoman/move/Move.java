package com.afgc.wondevwoman.move;


import it.unical.mat.embasp.languages.Id;
import it.unical.mat.embasp.languages.Param;

@Id("move")
public class Move {

    @Param(0)
    int pawn;

    @Param(1)
    int moveX;

    @Param(2)
    int moveY;

    @Param(3)
    int placeX;

    @Param(4)
    int placeY;

    public Move(){}

    public Move(int pawn, int dirX, int moveY, int placeX, int placeY) {
        this.pawn = pawn;
        this.moveX = dirX;
        this.moveY = moveY;
        this.placeX = placeX;
        this.placeY = placeY;
    }

    public int getPawn() {
        return pawn;
    }

    public void setPawn(int pawn) {
        this.pawn = pawn;
    }

    public int getMoveX() {
        return moveX;
    }

    public void setMoveX(int moveX) {
        this.moveX = moveX;
    }

    public int getMoveY() {
        return moveY;
    }

    public void setMoveY(int moveY) {
        this.moveY = moveY;
    }

    public int getPlaceX() {
        return placeX;
    }

    public void setPlaceX(int placeX) {
        this.placeX = placeX;
    }

    public int getPlaceY() {
        return placeY;
    }

    public void setPlaceY(int placeY) {
        this.placeY = placeY;
    }


    @Override
    public String toString() {
        return "Move{" +
                "pawn=" + pawn +
                ", dirX=" + moveX +
                ", dirY=" + moveY +
                ", placeX=" + placeX +
                ", placeY=" + placeY +
                '}';
    }
}
