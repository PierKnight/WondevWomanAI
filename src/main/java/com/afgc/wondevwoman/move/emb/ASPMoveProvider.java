package com.afgc.wondevwoman.move.emb;

import com.afgc.wondevwoman.Main;
import com.afgc.wondevwoman.asp.EmbASPHandler;
import com.afgc.wondevwoman.graphic.Pawn;
import com.afgc.wondevwoman.graphic.SceneHandler;
import com.afgc.wondevwoman.graphic.Tile;
import com.afgc.wondevwoman.move.Move;
import com.afgc.wondevwoman.move.MoveProvider;
import com.afgc.wondevwoman.move.Player;
import it.unical.mat.embasp.base.Handler;
import it.unical.mat.embasp.base.InputProgram;
import it.unical.mat.embasp.base.Output;
import it.unical.mat.embasp.languages.asp.AnswerSet;
import it.unical.mat.embasp.languages.asp.AnswerSets;

import java.util.List;
import java.util.Random;
import java.util.function.Supplier;

public class ASPMoveProvider implements MoveProvider {


    private final Supplier<Handler> handlerSupplier;
    private final String name;

    public ASPMoveProvider(String name,Supplier<Handler> handlerSupplier)
    {
        this.handlerSupplier = handlerSupplier;
        this.name = name;
    }

    @Override
    public final Move getMove(Player currentPlayer) throws InterruptedException {
        try {
            //inserire fatti
            EmbASPHandler.getInstance().variableProgram.clearAll();
            this.addFacts(EmbASPHandler.getInstance().variableProgram,currentPlayer);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //Thread.sleep(500);
        Output output = this.handlerSupplier.get().startSync();
        AnswerSets answerSets = (AnswerSets) output;
        return getMoveFromAnswerSets(answerSets);
    }

    @Override
    public String getName() {
        return name;
    }

    /**
     * this method is called to get a move given an answer set
     * @param answerSets the answer sets given by DLV
     * @return the move the player does
     */
    public Move getMoveFromAnswerSets(AnswerSets answerSets)
    {
        try {
            List<AnswerSet> answerSetList = answerSets.getOptimalAnswerSets();

            int randomIndex = new Random().nextInt(answerSetList.size());

            for (Object atom : answerSetList.get(randomIndex).getAtoms()) {
                if(atom instanceof Move move)
                    return move;
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
        for (Player player : SceneHandler.GAME_HANDLER.getPlayers()) {
            for (Pawn pawn : player.getPawns()) {
                String pawnFact = pawn.getFact(player != currentPlayer);
                variableInput.addProgram(pawnFact);
            }
        }
        for (Tile[] rowTiles : SceneHandler.GAME_HANDLER.getMyGamePanel().tiles)
            for (Tile tile : rowTiles)
                variableInput.addObjectInput(tile);
    }
}
