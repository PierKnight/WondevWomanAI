package com.afgc.wondevwoman;

import com.afgc.wondevwoman.util.KeyboardWaiter;
import javafx.scene.input.KeyCode;

public class HumanPlayer implements IPlayer{


    @Override
    public int getMove() throws InterruptedException {

        return KeyboardWaiter.getInstance().waitKeyboard() == KeyCode.RIGHT ? 1 : -1;
    }
}
