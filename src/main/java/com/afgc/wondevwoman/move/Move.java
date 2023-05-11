package com.afgc.wondevwoman.move;


import it.unical.mat.embasp.languages.Id;
import it.unical.mat.embasp.languages.Param;

@Id("move")
public class Move {

    @Param(0)
    int pawn;

    @Param(1)
    int dirX;

    @Param(2)
    int dirY;

    @Param(3)
    int placeX;

    @Param(4)
    int placeY;

    public Move(){}

    public Move(int pawn, int dirX, int dirY, int placeX, int placeY) {
        this.pawn = pawn;
        this.dirX = dirX;
        this.dirY = dirY;
        this.placeX = placeX;
        this.placeY = placeY;
    }

    public int getPawn() {
        return pawn;
    }

    public void setPawn(int pawn) {
        this.pawn = pawn;
    }

    public int getDirX() {
        return dirX;
    }

    public void setDirX(int dirX) {
        this.dirX = dirX;
    }

    public int getDirY() {
        return dirY;
    }

    public void setDirY(int dirY) {
        this.dirY = dirY;
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
                ", dirX=" + dirX +
                ", dirY=" + dirY +
                ", placeX=" + placeX +
                ", placeY=" + placeY +
                '}';
    }
}
