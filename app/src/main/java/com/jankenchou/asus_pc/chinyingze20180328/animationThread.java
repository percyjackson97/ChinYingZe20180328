package com.jankenchou.asus_pc.chinyingze20180328;

import android.os.Handler;
import android.os.Message;

/**
 * Created by Asus-PC on 2/19/2018.
 */

public class animationThread extends Thread {
    Handler handler = new Handler();
    Message GameHandlerMessage = handler.obtainMessage();
    boolean startAnimation = false;
    boolean running = true;

    public animationThread(Handler GameHandler){
        this.handler=GameHandler;
    }

    @Override
    public void run() {
        super.run();

        while(running){
            if (startAnimation) {
                try {
                    startAnimation = false;
                    GameHandlerMessage = handler.obtainMessage(0);
                    GameHandlerMessage.what = 0;
                    GameHandlerMessage.sendToTarget();
                    sleep(1500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
