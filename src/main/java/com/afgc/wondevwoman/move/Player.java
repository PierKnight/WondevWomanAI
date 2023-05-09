package com.afgc.wondevwoman.move;

import com.afgc.wondevwoman.graphic.Pawn;
import com.afgc.wondevwoman.move.MoveProvider;

public record Player(Pawn[] pawns, MoveProvider moveProvider) {
}
