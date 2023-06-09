package com.afgc.wondevwoman.move;

public interface MoveProvider {

    /**
     * this is called at the start of the player's turn
     * it should return a move within the turn time limit otherwise the thread that calls this is interrupted
     * and the player does nothing
     * @param player the player making the move
     * @throws InterruptedException when the thread handling this turn is interrupted by the timer
     * @return the move to make
     */
    Move getMove(Player player) throws InterruptedException;

    /**
     * @return the name of this provider
     */
    String getName();

}
