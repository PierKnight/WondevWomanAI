package com.afgc.wondevwoman.move.emb;

import com.afgc.wondevwoman.Main;
import com.afgc.wondevwoman.asp.EmbASPHandler;
import com.afgc.wondevwoman.graphic.Pawn;
import com.afgc.wondevwoman.graphic.Tile;
import com.afgc.wondevwoman.move.Move;
import com.afgc.wondevwoman.move.MoveProvider;
import com.afgc.wondevwoman.move.Player;
import it.unical.mat.embasp.base.Handler;
import it.unical.mat.embasp.base.InputProgram;
import it.unical.mat.embasp.base.Output;
import it.unical.mat.embasp.languages.asp.AnswerSet;
import it.unical.mat.embasp.languages.asp.AnswerSets;

import java.util.function.Supplier;

public class ASPMoveProvider implements MoveProvider {


    private final Supplier<Handler> handlerSupplier;

    public ASPMoveProvider(Supplier<Handler> handlerSupplier)
    {
        this.handlerSupplier = handlerSupplier;
    }

    @Override
    public final Move getMove(Player currentPlayer) {
        try {
            //inserire fatti
            EmbASPHandler.getInstance().variableProgram.clearAll();
            this.addFacts(EmbASPHandler.getInstance().variableProgram,currentPlayer);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Output output = this.handlerSupplier.get().startSync();
        AnswerSets answerSets = (AnswerSets) output;
        return getMoveFromAnswerSets(answerSets);
    }
    /**
     * this method is called to get a move given an answer set
     * @param answerSets the answer sets given by DLV
     * @return the move the player does
     */
    public Move getMoveFromAnswerSets(AnswerSets answerSets)
    {
        try {
            for (AnswerSet answerSet : answerSets.getOptimalAnswerSets()) {
                for (Object atom : answerSet.getAtoms()) {
                    if(atom instanceof Move move)
                        return move;
                }
            }
        }
        catch (Exception exception)
        {
            exception.printStackTrace();
        }
        return null;
    }

    protected void addFacts(InputProgram variableInput,Player currentPlayer) throws Exception
    {
        //add pawns
        for (Player player : Main.GAME_HANDLER.getPlayers()) {
            for (Pawn pawn : player.getPawns()) {
                String pawnFact = pawn.getFact(player != currentPlayer);
                variableInput.addProgram(pawnFact);
            }
        }
        for (Tile[] rowTiles : Main.GAME_HANDLER.getMyGamePanel().tiles)
            for (Tile tile : rowTiles)
                variableInput.addObjectInput(tile);
    }
}
