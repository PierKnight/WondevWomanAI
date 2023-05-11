package com.afgc.wondevwoman.move;

import com.afgc.wondevwoman.graphic.Pawn;

public record Player(String name,Pawn[] pawns, MoveProvider moveProvider) {


    public boolean isHumanPlayer()
    {
        return moveProvider == null;
    }
}
