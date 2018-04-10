package com.jankenchou.asus_pc.chinyingze20180328;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by Asus-PC on 2/21/2018.
 */

public class animationBOX extends AppCompatActivity {
    TextView error_message;
    RelativeLayout rl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.animation_box);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        final int flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

        getWindow().getDecorView().setSystemUiVisibility(flags);

        // Code below is to handle presses of Volume up or Volume down.
        // Without this, after pressing volume buttons, the navigation bar will
        // show up and won't hide
        final View decorView = getWindow().getDecorView();
        decorView
                .setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener()
                {

                    @Override
                    public void onSystemUiVisibilityChange(int visibility)
                    {
                        if((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0)
                        {
                            decorView.setSystemUiVisibility(flags);
                        }
                    }
                });

        rl = (RelativeLayout)findViewById(R.id.animator_box);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        //Here w and h can be modified to screen size percentage
        int width = (int)(dm.widthPixels*0.9);
        int height = (int)(dm.heightPixels*0.2);

        getWindow().setLayout(width,height);

        error_message = (TextView)findViewById(R.id.error_message);

        Intent i = getIntent();
        error_message.setText(i.getStringExtra("name_error_message"));
        error_message.setTextColor(getResources().getColor(R.color.white));

        final Handler handler5 = new Handler();
        if(i.getIntExtra("playerOne_cast", -1)==1) {
            rl.setBackground(getResources().getDrawable(R.drawable.border_p1));
        }
        else if(i.getIntExtra("playerOne_cast", -1) == 2){
            rl.setBackground(getResources().getDrawable(R.drawable.border_p2));
        }
        handler5.postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        }, 2000);


    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus)
    {
        super.onWindowFocusChanged(hasFocus);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

    }

    @Override
    protected void onStop(){
        getIntent().removeExtra("name_error_message");
        super.onStop();
    }
}
