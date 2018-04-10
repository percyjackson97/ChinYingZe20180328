package com.jankenchou.asus_pc.chinyingze20180328;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Created by Asus-PC on 2/18/2018.
 */

public class MyServiceInGame extends Service {
    //creating a mediaplayer object
    private MediaPlayer playertwo;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        playertwo = MediaPlayer.create(this,
                R.raw.battle_theme);
        //setting loop play to true
        float log1=(float)(1+ Math.log(15)/Math.log(50));
        playertwo.setVolume(log1,log1);
        playertwo.setLooping(true);

        //staring the player

        playertwo.start();


        //we have some options for service
        //start sticky means service will be explicity started and stopped
        return START_STICKY;
    }




    @Override
    public void onDestroy() {
        playertwo.stop();
        playertwo.release();
        //Toast.makeText(this, "STOP", Toast.LENGTH_LONG).show();
        //Toast.makeText(this, "STOP", Toast.LENGTH_LONG).show();
        super.onDestroy();
        //stopping the player when service is destroyed
    }


}
