package com.afgc.wondevwoman.move;

import java.util.Random;

public class RandomMoveProvider implements MoveProvider {

    private static final Random random = new Random();

    @Override
    public Move getMove() throws InterruptedException {

        Thread.sleep(500);




        return new Move(random.nextBoolean() ? 1 : 0,1 - random.nextInt(2),1 - random.nextInt(2),9,9);
        //return KeyboardWaiter.getInstance().waitKeyboard() == KeyCode.RIGHT ? 1 : -1;
    }
}
