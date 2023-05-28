package com.afgc.wondevwoman.move.emb.francescapier;

import com.afgc.wondevwoman.Settings;
import com.afgc.wondevwoman.controller.GameHandler;
import com.afgc.wondevwoman.graphic.Pawn;
import com.afgc.wondevwoman.move.Player;
import com.afgc.wondevwoman.move.emb.ASPMoveProvider;
import it.unical.mat.embasp.base.Handler;
import it.unical.mat.embasp.base.InputProgram;

import java.util.List;
import java.util.function.Supplier;

public class ASMShortPathProvider extends ASPMoveProvider
{
    public ASMShortPathProvider(String name,Supplier<Handler> handlerSupplier)
    {
        super(name,handlerSupplier);
    }

    @Override
    protected void addFacts(InputProgram variableInput, Player currentPlayer) throws Exception
    {
        super.addFacts(variableInput, currentPlayer);

        for (int i = 0; i < currentPlayer.getPawns().length; i++)
        {

            Pawn teamPawn = currentPlayer.getPawns()[i];

            Player enemy = GameHandler.getInstance().getPlayers()[0] != currentPlayer ? GameHandler.getInstance().getPlayers()[0] : GameHandler.getInstance().getPlayers()[1];
            for (int j = 0; j < enemy.getPawns().length; j++)
            {
                Pawn enemyPawn = enemy.getPawns()[j];


                Node initialNode = new Node(teamPawn.getX(), teamPawn.getY(), 0);
                Node finalNode = new Node(enemyPawn.getX(), enemyPawn.getY(), 0);
                AStar aStar = new AStar(Settings.TILES, Settings.TILES, initialNode, finalNode);

                List<Node> path = aStar.findPath();
                if(path.size() >= 2) {
                    String shortPath = "nearMoveToPawn(" + i + "," + j + "," + path.get(1).getRow() + "," + path.get(1).getCol() + "," + (path.size() - 1) + ").";
                    variableInput.addProgram(shortPath);
                }
            }
        }
    }
}
