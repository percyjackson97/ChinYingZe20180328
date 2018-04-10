package com.jankenchou.asus_pc.chinyingze20180328;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

/**
 * Created by Asus-PC on 3/15/2018.
 */

public class match_making extends AppCompatActivity {

    TextView match_status;
    TextView opponent_detail;

    Button accept;

    Boolean oneTime;

    Boolean selfIsPlayerA;

    Boolean whenclose;

    player player;

    boolean waitingToAccept;

    private DatabaseReference mFirebaseDatabase;
    private FirebaseDatabase mFirebaseInstance;


    private String UserID;
    private String opponentID;
    String battleid;

    private SharedPreferences preferenceSettings;

    String opponent;
    String self;

    private MediaPlayer button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.match_making);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        //Here w and h can be modified to screen size percentage
        int width = (int)(dm.widthPixels*0.9);
        int height = (int)(dm.heightPixels*0.4);

        getWindow().setLayout(width,height);

        selfIsPlayerA = false;
        whenclose = false;

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

        preferenceSettings = PreferenceManager.getDefaultSharedPreferences(this);
        UserID = preferenceSettings.getString("UserID", null);

        button = MediaPlayer.create(this, R.raw.button_click);

        Gson gson_retrieve = new Gson();
        preferenceSettings = PreferenceManager.getDefaultSharedPreferences(this);
        String json_retrieve = preferenceSettings.getString("playerONE", "");
        player = gson_retrieve.fromJson(json_retrieve, player.class);

        match_status = (TextView)findViewById(R.id.status);
        opponent_detail = (TextView)findViewById(R.id.opponent_detail);
        accept = (Button)findViewById(R.id.accept_battle);
        oneTime = true;

        match_status.setText(getString(R.string.waiting));
        match_status.setTextSize(26);
        match_status.setTextColor(getResources().getColor(R.color.black));


        opponent_detail.setVisibility(View.GONE);

        accept.setVisibility(View.GONE);

        mFirebaseInstance = FirebaseDatabase.getInstance();
        //get reference to PLAYER node
        mFirebaseDatabase = mFirebaseInstance.getReference("GamingData");
        mFirebaseDatabase.keepSynced(true);
        accept.setSoundEffectsEnabled(false);

        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                button.start();
                mFirebaseDatabase.child("MatchMaking").child(battleid).child("Status").child(self).setValue(2);
                waitingForOpponentReply();
            }
        });

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
    public void onStart() {
        super.onStart();
        //Toast.makeText(this, UserID, Toast.LENGTH_LONG).show();
        Query query = mFirebaseDatabase.child("MatchMaking").orderByChild("MatchStatus").equalTo("open");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && !selfIsPlayerA) {

                    boolean noMatch=true;
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            battleid = snapshot.getKey();
                                if (dataSnapshot.child(battleid).child("MatchStatus").getValue(String.class).equals("open")) {
                                    GotMatch();
                                    noMatch = false;
                                }
                        }
                    if(noMatch){
                            selfIsPlayerA = true;
                            TakAdeMatch();
                    }

                }
                else{
                    TakAdeMatch();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public void GotMatch(){
        selfIsPlayerA = false;
        mFirebaseDatabase.child("MatchMaking").child(battleid).child("PlayerB").setValue(UserID);
        findMatch();
    }

    public void TakAdeMatch(){
        battleid = mFirebaseDatabase.child("MatchMaking").push().getKey();
        selfIsPlayerA = true;
        mFirebaseDatabase.child("MatchMaking").child(battleid).child("PlayerA").setValue(UserID);
        mFirebaseDatabase.child("MatchMaking").child(battleid).child("MatchStatus").setValue("open");
        findMatch();
    }

    public void findMatch(){
        if(selfIsPlayerA){
            opponent = "PlayerB";
            self = "PlayerA";
        }
        else{
            opponent = "PlayerA";
            self = "PlayerB";
        }
        Query query = mFirebaseDatabase.child("MatchMaking").child(battleid);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    opponentID = dataSnapshot.child(opponent).getValue(String.class);
                    if(opponentID!=null && !dataSnapshot.child("Status").child(self).exists()) {
                        mFirebaseDatabase.child("MatchMaking").child(battleid).child("MatchStatus").setValue("close");
                        opponentFound();
                    }
                }
                if(dataSnapshot.child("Status").child(opponent).exists()){
                    if(dataSnapshot.child("Status").child(opponent).getValue(Integer.class) == 0) {
                        if (oneTime && !whenclose) {
                            Intent errorMessage = new Intent(getApplicationContext(), animationBOX.class);
                            String name_eror = getResources().getString(R.string.opponent_decline);
                            errorMessage.putExtra("name_error_message", name_eror);
                            startActivity(errorMessage);
                            oneTime = false;
                            finish();
                        }
                    }
                    else if(dataSnapshot.child("Status").child(opponent).getValue(Integer.class) == 2 && dataSnapshot.child("Status").child(self).getValue(Integer.class) == 2){
                        whenclose = true;
                        Intent battle = new Intent(getApplicationContext(), in_game.class);
                        battle.putExtra("battleID", battleid);
                        battle.putExtra("opponentID", opponentID);
                        startActivity(battle);
                        if(login_page.instance != null){
                            try{
                                login_page.instance.finish();
                            }catch (Exception e){}
                        }
                        stopService(new Intent(match_making.this, MyService.class));
                        finish();
                    }
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public void opponentFound(){
        match_status.setText(getString(R.string.opponentFOUND));
        match_status.setTextSize(33);
        match_status.setTextColor(getResources().getColor(R.color.black));

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        //Here w and h can be modified to screen size percentage
        int width = (int)(dm.widthPixels*0.9);
        int height = (int)(dm.heightPixels*0.6);
        getWindow().setLayout(width,height);
        //Toast.makeText(getApplicationContext(), existingPairs.get(i).name, Toast.LENGTH_LONG).show();

        accept.setVisibility(View.VISIBLE);
        mFirebaseDatabase.child("MatchMaking").child(battleid).child("Status").child(self).setValue(1);
        waitingToAccept = true;
    }

    public void waitingForOpponentReply(){
        accept.setVisibility(View.GONE);

        match_status.setText(getString(R.string.waitingReply));
        match_status.setTextSize(26);
        match_status.setTextColor(getResources().getColor(R.color.black));
    }




    @Override
    protected void onDestroy(){
        whenclose = true;
        if(battleid != null){
            mFirebaseDatabase.child("MatchMaking").child(battleid).child("Status").child(self).setValue(0);
        }
        if (selfIsPlayerA){
            mFirebaseDatabase.child("MatchMaking").child(battleid).child("MatchStatus").setValue("close");
        }
        super.onDestroy();
    }


}
