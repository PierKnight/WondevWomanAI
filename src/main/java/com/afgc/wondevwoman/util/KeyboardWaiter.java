package com.afgc.wondevwoman.util;

import com.afgc.wondevwoman.Main;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class KeyboardWaiter {


    private static final KeyboardWaiter INSTANCE = new KeyboardWaiter();

    private final Lock lock;
    private final Condition condition;
    private int waitingThreads = 0;
    private KeyEvent receivedInput = null;

    private KeyboardWaiter() {
        this.lock = new ReentrantLock();
        this.condition = lock.newCondition();

        // modificato
        Main.GAME_HANDLER.getMyGamePanel().addEventHandler(KeyEvent.KEY_PRESSED, event -> {
            lock.lock();
            receivedInput = event;
            condition.signal();
            lock.unlock();
        });
    }

    public static KeyboardWaiter getInstance() {
        return INSTANCE;
    }


    public KeyCode waitKeyboard()
    {

        try {

            this.lock.lock();

            System.out.println("ASPETTO");
            waitingThreads += 1;

            //aspetto finchè non ricevo un input
            while (receivedInput == null)
                this.condition.await();

            waitingThreads -= 1;
            KeyCode code = receivedInput.getCode();
            receivedInput = null;



            return code;
        }
        catch (InterruptedException ignored)
        {
        }
        finally {
            this.lock.unlock();
        }
        return null;
    }

}
