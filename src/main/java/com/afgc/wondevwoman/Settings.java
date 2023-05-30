package com.afgc.wondevwoman;

import com.afgc.wondevwoman.move.MoveProvider;

public class Settings {
    public static final int TILES = 7;
    /**
     * se true significa che quando un {@link MoveProvider} prova a fare una mossa illegale il turno passa all'avversario
     */
    public static final boolean SKIP_TURN_WITH_ILLEGAL_MOVE = true;

    public static final int SECONDS_PER_TURN = 15;

    public static int getY(int index)
    {
        return index / Settings.TILES;
    }
    public static int getX(int index)
    {
        return index % Settings.TILES;
    }


}
