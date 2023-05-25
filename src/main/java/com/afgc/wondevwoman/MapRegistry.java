package com.afgc.wondevwoman;

import com.afgc.wondevwoman.move.GameStatus;

import java.util.HashMap;
import java.util.Map;

public class MapRegistry {



    private static GameStatus getEmptyMapFarPlayers()
    {
        int[][] map = new int[Settings.TILES][Settings.TILES];

        for(int i = 0;i < Settings.TILES;i++)
            for(int j= 0;j < Settings.TILES;j++)
                map[i][j] = 0;
        return new GameStatus("mappa con giocatori lontani",new int[][]{
                new int[]{0,0},
                new int[]{Settings.TILES - 1,0}
        },new int[][]{
                new int[]{0,Settings.TILES - 1},
                new int[]{Settings.TILES - 1,Settings.TILES - 1}
        },map);
    }

    private static GameStatus getEmptyMapNearPlayers()
    {
        int[][] map = new int[Settings.TILES][Settings.TILES];

        for(int i = 0;i < Settings.TILES;i++)
            for(int j= 0;j < Settings.TILES;j++)
                map[i][j] = 0;
        return new GameStatus("mappa con giocatori vicini",new int[][]{
                new int[]{3,3},
                new int[]{4,3}
        },new int[][]{
                new int[]{3,4},
                new int[]{4,4}
        },map);
    }

    public static GameStatus[] getGameMaps(){

        return new GameStatus[]{
                getEmptyMapFarPlayers(),
                getEmptyMapNearPlayers()
        };
    }



}
