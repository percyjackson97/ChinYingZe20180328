package com.jankenchou.asus_pc.chinyingze20180328;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Created by Belal on 12/30/2016.
 */

public class MyService extends Service {
    //creating a mediaplayer object
    private MediaPlayer player;
    private boolean initial = true;
    public static final String ACTION_STOP = "stop_music_service";
    public static volatile boolean dontStop = true;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


            player = MediaPlayer.create(this,
                    R.raw.menu_theme);

                //setting loop play to true
                player.setLooping(true);

                //staring the player

                player.start();




        //we have some options for service
        //start sticky means service will be explicity started and stopped
        return START_NOT_STICKY;
    }




    @Override
    public void onDestroy() {
        player.stop();
        player.release();
        //Toast.makeText(this, "STOP", Toast.LENGTH_LONG).show();
        super.onDestroy();
        //stopping the player when service is destroyed
    }


}
