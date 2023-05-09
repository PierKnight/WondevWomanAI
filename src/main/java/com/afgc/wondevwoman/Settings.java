package com.afgc.wondevwoman;

import com.afgc.wondevwoman.asp.EmbASPHandler;
import com.afgc.wondevwoman.move.MoveProvider;
import com.afgc.wondevwoman.move.RandomMoveProvider;
import com.afgc.wondevwoman.move.emb.ASPMoveProvider;
import it.unical.mat.embasp.base.Handler;

public class Settings {
    public static final int WIDTH = 50;
    public static final int HEIGHT = 50;

    /**
     * se true significa che quando un {@link MoveProvider} prova a fare una mossa illegale il turno passa all'avversario
     */
    public static final boolean SKIP_TURN_WITH_ILLEGAL_MOVE = true;

    /**
     * qui si può decidere che comportamento affidare al primo plauer per scegliere la mossa
     */

    //TODO se il move provider è null, allora la mossa è data da un giocatore umano
    public static final MoveProvider FIRST_PLAYER = new ASPMoveProvider(() -> EmbASPHandler.getInstance().EMJACOPO);

    /**
     * qui si può decidere che comportamento affidare al secondo plauer per scegliere la mossa
     */
    public static final MoveProvider SECOND_PLAYER = new RandomMoveProvider();

}