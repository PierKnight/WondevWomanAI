package com.afgc.wondevwoman.move;

import com.afgc.wondevwoman.graphic.Pawn;

public class Player {

    private String name;
    private Pawn[] pawns;
    private MoveProvider moveProvider;
    private Integer points;

    public Player(String name,Pawn[] pawns, MoveProvider moveProvider, Integer points) {
        this.name = name;
        this.pawns = pawns;
        this.moveProvider = moveProvider;
        this.points = points;
    }

    public boolean isHumanPlayer()
    {
        return moveProvider == null;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Pawn[] getPawns() {
        return pawns;
    }

    public void setPawns(Pawn[] pawns) {
        this.pawns = pawns;
    }

    public MoveProvider getMoveProvider() {
        return moveProvider;
    }

    public void setMoveProvider(MoveProvider moveProvider) {
        this.moveProvider = moveProvider;
    }

    public Integer getPoints() {
        return points;
    }

    public void setPoints(Integer points) {
        this.points = points;
    }
}
