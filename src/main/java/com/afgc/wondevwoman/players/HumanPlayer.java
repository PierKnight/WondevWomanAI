package com.afgc.wondevwoman.players;

import com.afgc.wondevwoman.util.KeyboardWaiter;
import javafx.scene.input.KeyCode;

public class HumanPlayer implements MoveProvider {


    @Override
    public Move getMove() throws InterruptedException {

        return new Move(0,1,2,2);
        //return KeyboardWaiter.getInstance().waitKeyboard() == KeyCode.RIGHT ? 1 : -1;
    }
}
