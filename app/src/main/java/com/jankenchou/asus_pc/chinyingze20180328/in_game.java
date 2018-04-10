package com.jankenchou.asus_pc.chinyingze20180328;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by Asus-PC on 3/29/2018.
 */

public class in_game extends AppCompatActivity {
    player p1;
    player p2;

    Animation anim;
    animationThread animationThread;
    Animation slideUpAnimation;
    Animation slideUpAnimation2;
    Animation slideUpAnimation3;
    Animation slideUpAnimation4;
    Animation slideLEFT;
    Animation slideRIGHT;
    Animation useCard;
    Animation finishUse;
    Animation pulse;

    boolean oneError;
    boolean doneSelection;

    public static int ACTION_CODE;
    private int SELF_ACTION;

    private DatabaseReference mFirebaseDatabase;
    private FirebaseDatabase mFirebaseInstance;

    ImageButton rock;
    ImageButton paper;
    ImageButton scissor;
    ImageButton angry;
    Button LP_up;
    Button Rock_up;
    Button Paper_up;
    Button Scissor_up;
    Button Angry_up;


    TextView playerName;
    TextView playerOne_hp;
    TextView playerOne_rock;
    TextView playerOne_paper;
    TextView playerOne_scissor;
    TextView playerOne_angry;
    TextView playerTwoName;
    TextView playerTwo_hp;
    TextView playerTwo_rock;
    TextView playerTwo_paper;
    TextView playerTwo_scissor;
    TextView playerTwo_angry;
    TextView playerOne_currentPhase;
    TextView playerTwo_currentPhase;
    TextView phase_change_box;
    TextView timerValue;

    private SharedPreferences.Editor preferenceEditor;
    private SharedPreferences preferenceSettings;

    private String UserID;
    private String battleID;
    private  String opponentID;

    int turn;

    CountDownTimer countdown;
    MediaPlayer countdownn;
    MediaPlayer slash;
    MediaPlayer card_flip;
    MediaPlayer card_flip2;
    MediaPlayer card_flip3;
    MediaPlayer card_flip4;
    MediaPlayer charge;
    MediaPlayer action_sound;

    boolean permission;
    boolean whenClose;
    boolean whenForfeit;
    boolean onetimeForfeit;
    boolean oneTimenoRepeat;
    boolean ended;
    boolean oneTimetimesUP;
    boolean oneTime;
    TextView flash;

    action currentAction;
    action opponentAction;
    String actionID;

    int selfBleeding;
    int oppBleeding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.battle_activity);
        //background.setImageDrawable(getResources().getDrawable(R.drawable.animation));
        //AnimationDrawable progrssAnimation = (AnimationDrawable)background.getDrawable();
        //progrssAnimation.start();

        final int flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
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
                .setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {

                    @Override
                    public void onSystemUiVisibilityChange(int visibility) {
                        if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                            decorView.setSystemUiVisibility(flags);
                        }
                    }
                });

        ACTION_CODE = 0;
        SELF_ACTION = 0;
        whenClose =false;
        doneSelection = false;
        oneTime =true;
        oneError = true;

        Intent intent = getIntent();
        battleID = intent.getStringExtra("battleID");
        opponentID = intent.getStringExtra("opponentID");
        preferenceSettings = PreferenceManager.getDefaultSharedPreferences(this);
        UserID = preferenceSettings.getString("UserID", null);

        mFirebaseInstance = FirebaseDatabase.getInstance();
        //get reference to PLAYER node
        mFirebaseDatabase = mFirebaseInstance.getReference("GamingData");

        mFirebaseDatabase.child("Players").child(UserID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                p1 = dataSnapshot.getValue(player.class);

            }


            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("TAG", "Failed to retrieve Player values", databaseError.toException());
            }
        });

        rock = (ImageButton)findViewById(R.id.skill_one);
        paper = (ImageButton)findViewById(R.id.skill_two);
        scissor = (ImageButton)findViewById(R.id.skill_three);
        angry = (ImageButton)findViewById(R.id.skill_four);

        LP_up = (Button)findViewById(R.id.LP_up);
        Rock_up = (Button)findViewById(R.id.rock_up);
        Paper_up = (Button)findViewById(R.id.paper_up);
        Scissor_up = (Button)findViewById(R.id.scissor_up);
        Angry_up = (Button)findViewById(R.id.angry_up);

        rock.setImageDrawable(getResources().getDrawable(R.drawable.skill_one));
        paper.setImageDrawable(getResources().getDrawable(R.drawable.skill_two));
        scissor.setImageDrawable(getResources().getDrawable(R.drawable.skill_three));
        angry.setImageDrawable(getResources().getDrawable(R.drawable.skill_four));

        playerName = (TextView)findViewById(R.id.player_name_in_game);
        playerOne_hp = (TextView)findViewById(R.id.own_LP);
        playerOne_rock = (TextView)findViewById(R.id.own_rock);
        playerOne_paper = (TextView)findViewById(R.id.own_paper);
        playerOne_scissor = (TextView)findViewById(R.id.own_scissor);
        playerOne_angry = (TextView)findViewById(R.id.own_angry);
        playerOne_currentPhase = (TextView)findViewById(R.id.current_phase);

        playerTwoName = (TextView)findViewById(R.id.opponent_name_in_game);
        playerTwo_hp = (TextView)findViewById(R.id.opponent_LP);
        playerTwo_rock = (TextView)findViewById(R.id.opponent_rock);
        playerTwo_paper = (TextView)findViewById(R.id.opponent_paper);
        playerTwo_scissor = (TextView)findViewById(R.id.opponent_scissor);
        playerTwo_angry = (TextView)findViewById(R.id.opponent_angry);
        playerTwo_currentPhase = (TextView)findViewById(R.id.opponent_current_phase);

        phase_change_box = (TextView)findViewById(R.id.phase_change_animator);

        playerName.setText(getString(R.string.playerOne));
        playerName.setTextSize(20);
        playerName.setTextColor(getResources().getColor(R.color.white));
        playerName.setAllCaps(true);

        playerTwoName.setText(getString(R.string.playerTwo));
        playerTwoName.setTextSize(20);
        playerTwoName.setTextColor(getResources().getColor(R.color.white));
        playerTwoName.setAllCaps(true);


        //starting game
        turn = 0;
        doneSelection = false;
        oneTimenoRepeat = true;

        anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(50);
        anim.setStartOffset(20);
        anim.setRepeatMode(Animation.REVERSE);
        anim.setRepeatCount(3);

        //testing to run thread
        animationThread = new animationThread(animationHandler);
        animationThread.start();

        countdownn = MediaPlayer.create(this, R.raw.countdown);
        countdownn.setLooping(true);
        //please work
        timerValue = (TextView)findViewById(R.id.timerValue);

        permission = true;

        LP_up.setSoundEffectsEnabled(false);
        Rock_up.setSoundEffectsEnabled(false);
        Paper_up.setSoundEffectsEnabled(false);
        Scissor_up.setSoundEffectsEnabled(false);
        Angry_up.setSoundEffectsEnabled(false);

        flash = (TextView)findViewById(R.id.flash);
        flash.setVisibility(View.GONE);

        final Handler handler5 = new Handler();
        handler5.postDelayed(new Runnable() {
            @Override
            public void run() {
                startService(new Intent(in_game.this, MyServiceInGame.class));
            }
        }, 2400);

        slideUpAnimation = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.slide_up_animation);
        slideUpAnimation2 = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.slide_up_animation);
        slideUpAnimation3 = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.slide_up_animation);
        slideUpAnimation4 = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.slide_up_animation);
        slideLEFT = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.slide_left_animation);
        slideRIGHT = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.slide_right_animation);
        pulse = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.pulse);
        useCard = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.use_card);
        useCard.setFillAfter(true);
        finishUse = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.finish_use);
        finishUse.setFillAfter(true);

        rock.setVisibility(View.GONE);
        paper.setVisibility(View.GONE);
        scissor.setVisibility(View.GONE);
        angry.setVisibility(View.GONE);

        playerOne_hp.setVisibility(View.GONE);
        playerTwo_hp.setVisibility(View.GONE);

        playerName.setVisibility(View.GONE);
        playerTwoName.setVisibility(View.GONE);

        timerValue.setVisibility(View.GONE);

        slash = MediaPlayer.create(this, R.raw.slash);
        card_flip = MediaPlayer.create(this, R.raw.card_flip_sound);
        card_flip2 = MediaPlayer.create(this, R.raw.card_flip_sound);
        card_flip3 = MediaPlayer.create(this, R.raw.card_flip_sound);
        card_flip4 = MediaPlayer.create(this, R.raw.card_flip_sound);
        charge = MediaPlayer.create(this, R.raw.charge);
        action_sound = MediaPlayer.create(this, R.raw.charge_spell);

        preAnim();

        handler5.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(turn == 0) {
                    playerOne_phase(1);
                }
            }
        }, 4200);

        LP_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(permission) {
                    if (p1.hp < 5) {
                        SELF_ACTION = 1;
                        mFirebaseDatabase.child("Action").orderByChild("action_ID").equalTo(SELF_ACTION).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    actionID = snapshot.getKey();
                                    currentAction = dataSnapshot.child(actionID).getValue(action.class);
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                        charge.start();
                        flash.setBackgroundColor(Color.parseColor("#33008000"));
                        flash.setVisibility(View.VISIBLE);
                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                flash.setVisibility(View.GONE);
                            }
                        }, 300);
                        LP_up.startAnimation(pulse);
                        permission = false;
                        playerOne_phase(2);
                        mFirebaseDatabase.child("Battles").child(battleID).child(UserID).setValue(1);
                        if(ACTION_CODE !=0 && SELF_ACTION != 0){
                            GAMEFLOW();
                        }
                    }
                    else {
                        Intent errorMessage = new Intent(getApplicationContext(), animationBOX.class);
                        String name_eror = "もう元気すぎ";
                        errorMessage.putExtra("name_error_message", name_eror);
                        startActivity(errorMessage);
                    }
                }
            }
        });

        Rock_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(permission) {
                    if (p1.rock_charge < 1) {
                        SELF_ACTION = 2;
                        mFirebaseDatabase.child("Action").orderByChild("action_ID").equalTo(SELF_ACTION).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    actionID = snapshot.getKey();
                                    currentAction = dataSnapshot.child(actionID).getValue(action.class);
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                        charge.start();
                        flash.setBackgroundColor(Color.parseColor("#33008000"));
                        flash.setVisibility(View.VISIBLE);
                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                flash.setVisibility(View.GONE);
                            }
                        }, 300);
                        Rock_up.startAnimation(pulse);
                        permission = false;
                        playerOne_phase(2);
                        mFirebaseDatabase.child("Battles").child(battleID).child(UserID).setValue(2);
                        if(ACTION_CODE !=0 && SELF_ACTION != 0){
                            GAMEFLOW();
                        }
                    } else {
                        Intent errorMessage = new Intent(getApplicationContext(), animationBOX.class);
                        String name_eror = "石は重いから一個でいい";
                        errorMessage.putExtra("name_error_message", name_eror);
                        startActivity(errorMessage);
                    }
                }
            }
        });

        Paper_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(permission) {
                    if (p1.paper_charge < 1) {
                        SELF_ACTION = 3;
                        mFirebaseDatabase.child("Action").orderByChild("action_ID").equalTo(SELF_ACTION).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    actionID = snapshot.getKey();
                                    currentAction = dataSnapshot.child(actionID).getValue(action.class);
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                        charge.start();
                        flash.setBackgroundColor(Color.parseColor("#33008000"));
                        flash.setVisibility(View.VISIBLE);
                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                flash.setVisibility(View.GONE);
                            }
                        }, 300);
                        Paper_up.startAnimation(pulse);
                        permission = false;
                        playerOne_phase(2);
                        mFirebaseDatabase.child("Battles").child(battleID).child(UserID).setValue(3);
                        if(ACTION_CODE !=0 && SELF_ACTION != 0){
                            GAMEFLOW();
                        }
                    } else {
                        Intent errorMessage = new Intent(getApplicationContext(), animationBOX.class);
                        String name_eror = "紙は自然環境に被害するからいい一個でいい";
                        errorMessage.putExtra("name_error_message", name_eror);
                        startActivity(errorMessage);
                    }
                }
            }
        });

        Scissor_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(permission) {
                    if (p1.scissor_charge < 1) {
                        SELF_ACTION = 4;
                        mFirebaseDatabase.child("Action").orderByChild("action_ID").equalTo(SELF_ACTION).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    actionID = snapshot.getKey();
                                    currentAction = dataSnapshot.child(actionID).getValue(action.class);
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                        charge.start();
                        flash.setBackgroundColor(Color.parseColor("#33008000"));
                        flash.setVisibility(View.VISIBLE);
                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                flash.setVisibility(View.GONE);
                            }
                        }, 300);
                        Scissor_up.startAnimation(pulse);
                        permission = false;
                        playerOne_phase(2);
                        mFirebaseDatabase.child("Battles").child(battleID).child(UserID).setValue(4);
                        if(ACTION_CODE !=0 && SELF_ACTION != 0){
                            GAMEFLOW();
                        }
                    } else {
                        Intent errorMessage = new Intent(getApplicationContext(), animationBOX.class);
                        String name_eror = "はさみ危ないから一個でいい";
                        errorMessage.putExtra("name_error_message", name_eror);
                        startActivity(errorMessage);
                    }
                }
            }
        });

        Angry_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (permission) {
                    if (p1.angry_charge < 1) {
                        SELF_ACTION = 5;
                        mFirebaseDatabase.child("Action").orderByChild("action_ID").equalTo(SELF_ACTION).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    actionID = snapshot.getKey();
                                    currentAction = dataSnapshot.child(actionID).getValue(action.class);
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                        charge.start();
                        flash.setBackgroundColor(Color.parseColor("#33008000"));
                        flash.setVisibility(View.VISIBLE);
                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                flash.setVisibility(View.GONE);
                            }
                        }, 300);
                        Angry_up.startAnimation(pulse);
                        playerOne_phase(2);
                        permission = false;
                        mFirebaseDatabase.child("Battles").child(battleID).child(UserID).setValue(5);
                        if(ACTION_CODE !=0 && SELF_ACTION != 0){
                            GAMEFLOW();
                        }
                    } else {
                        Intent errorMessage = new Intent(getApplicationContext(), animationBOX.class);
                        String name_eror = "もう怒ってるからいい加減にしろ";
                        errorMessage.putExtra("name_error_message", name_eror);
                        startActivity(errorMessage);
                    }
                }
            }
        });

        rock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(permission) {
                    SELF_ACTION = 6;
                    mFirebaseDatabase.child("Action").orderByChild("action_ID").equalTo(SELF_ACTION).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                actionID = snapshot.getKey();
                                currentAction = dataSnapshot.child(actionID).getValue(action.class);
                                if(currentAction!=null){
                                    if (skillcheck(currentAction, p1)) {
                                        card_flip.start();
                                        flash.setBackgroundColor(Color.parseColor("#33008000"));
                                        flash.setVisibility(View.VISIBLE);
                                        final Handler handler = new Handler();
                                        handler.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                flash.setVisibility(View.GONE);
                                            }
                                        }, 300);
                                        rock.setAnimation(useCard);
                                        permission = false;
                                        playerOne_phase(2);
                                        mFirebaseDatabase.child("Battles").child(battleID).child(UserID).setValue(SELF_ACTION);
                                        if(ACTION_CODE !=0 && SELF_ACTION != 0){
                                            GAMEFLOW();
                                        }
                                    } else {
                                        Intent errorMessage = new Intent(getApplicationContext(), error_message.class);
                                        String name_eror = currentAction.error;
                                        errorMessage.putExtra("name_error_message", name_eror);
                                        startActivity(errorMessage);
                                    }
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }
        });

        paper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(permission) {
                    SELF_ACTION = 7;
                    mFirebaseDatabase.child("Action").orderByChild("action_ID").equalTo(SELF_ACTION).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                actionID = snapshot.getKey();
                                currentAction = dataSnapshot.child(actionID).getValue(action.class);
                                if(currentAction!=null){
                                    if (skillcheck(currentAction, p1)) {
                                        card_flip.start();
                                        flash.setBackgroundColor(Color.parseColor("#33008000"));
                                        flash.setVisibility(View.VISIBLE);
                                        final Handler handler = new Handler();
                                        handler.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                flash.setVisibility(View.GONE);
                                            }
                                        }, 300);
                                        paper.setAnimation(useCard);
                                        permission = false;
                                        playerOne_phase(2);
                                        mFirebaseDatabase.child("Battles").child(battleID).child(UserID).setValue(SELF_ACTION);
                                        if(ACTION_CODE !=0 && SELF_ACTION != 0){
                                            GAMEFLOW();
                                        }
                                    } else {
                                        Intent errorMessage = new Intent(getApplicationContext(), error_message.class);
                                        String name_eror = currentAction.error;
                                        errorMessage.putExtra("name_error_message", name_eror);
                                        startActivity(errorMessage);
                                    }
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }
        });

        scissor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(permission) {
                    SELF_ACTION = 8;
                    mFirebaseDatabase.child("Action").orderByChild("action_ID").equalTo(SELF_ACTION).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                actionID = snapshot.getKey();
                                currentAction = dataSnapshot.child(actionID).getValue(action.class);
                                if(currentAction!=null){
                                    if (skillcheck(currentAction, p1)) {
                                        card_flip.start();
                                        flash.setBackgroundColor(Color.parseColor("#33008000"));
                                        flash.setVisibility(View.VISIBLE);
                                        final Handler handler = new Handler();
                                        handler.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                flash.setVisibility(View.GONE);
                                            }
                                        }, 300);
                                        scissor.setAnimation(useCard);
                                        permission = false;
                                        playerOne_phase(2);
                                        mFirebaseDatabase.child("Battles").child(battleID).child(UserID).setValue(SELF_ACTION);
                                        if(ACTION_CODE !=0 && SELF_ACTION != 0){
                                            GAMEFLOW();
                                        }
                                    } else {
                                        Intent errorMessage = new Intent(getApplicationContext(), error_message.class);
                                        String name_eror = currentAction.error;
                                        errorMessage.putExtra("name_error_message", name_eror);
                                        startActivity(errorMessage);
                                    }
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }
        });

        angry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (permission) {
                    SELF_ACTION = 9;
                    mFirebaseDatabase.child("Action").orderByChild("action_ID").equalTo(SELF_ACTION).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                actionID = snapshot.getKey();
                                currentAction = dataSnapshot.child(actionID).getValue(action.class);
                                if(currentAction!=null){
                                    if (skillcheck(currentAction, p1)) {
                                        card_flip.start();
                                        flash.setBackgroundColor(Color.parseColor("#33008000"));
                                        flash.setVisibility(View.VISIBLE);
                                        final Handler handler = new Handler();
                                        handler.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                flash.setVisibility(View.GONE);
                                            }
                                        }, 300);
                                        angry.setAnimation(useCard);
                                        permission = false;
                                        playerOne_phase(2);
                                        mFirebaseDatabase.child("Battles").child(battleID).child(UserID).setValue(SELF_ACTION);
                                        if(ACTION_CODE !=0 && SELF_ACTION != 0){
                                            GAMEFLOW();
                                        }
                                    } else {
                                        Intent errorMessage = new Intent(getApplicationContext(), error_message.class);
                                        String name_eror = currentAction.error;
                                        errorMessage.putExtra("name_error_message", name_eror);
                                        startActivity(errorMessage);
                                    }
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }
        });

        oneTimetimesUP = true;
        countdown = new CountDownTimer(24000, 1000) {

            public void onTick(long millisUntilFinished) {
                if(!doneSelection) {
                    float log1=(float)(1-Math.log(30)/Math.log(50));
                    countdownn.setVolume(log1,log1);
                    countdownn.start();
                    timerValue.setText(String.valueOf(millisUntilFinished / 1000));
                }
                //here you can have your logic to set text to edittext
            }

            public void onFinish() {
                countdownn.stop();
                if(!doneSelection) {
                    if (oneTimenoRepeat) {
                        SELF_ACTION = -1;
                        whenForfeit = true;
                        oneError = false;
                        ended = true;
                        oneTimenoRepeat = false;
                        onetimeForfeit = false;
                        if (oneTimetimesUP) {
                            mFirebaseDatabase.child("Battles").child(battleID).child(UserID).setValue(-1);
                            oneTimetimesUP = false;
                        }
                        Intent errorMessage = new Intent(getApplicationContext(), animationBOX.class);
                        String name_eror = "時間です";
                        errorMessage.putExtra("name_error_message", name_eror);
                        startActivity(errorMessage);
                        phase_change_box.setText(getString(R.string.end));
                        phase_change_box.setTextSize(30);
                        phase_change_box.setTextColor(getResources().getColor(R.color.white));
                        animationThread.startAnimation = true;
                        Toast.makeText(in_game.this, "負けた:(", Toast.LENGTH_LONG).show();
                        handler5.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mFirebaseDatabase.child("Battles").child(battleID).removeValue();
                                //opponent win
                                doneSelection = true;
                                whenForfeit = true;
                                oneError = false;
                                onetimeForfeit = false;
                                Intent end = new Intent(getApplicationContext(), login_page.class);
                                startActivity(end);
                                stopService(new Intent(in_game.this,MyServiceInGame.class));
                                startService(new Intent(in_game.this,MyService.class));
                                finish();
                            }
                        }, 1200);
                    }
                }
                else if (doneSelection && ACTION_CODE == 0){
                    if (oneError) {
                        doneSelection = true;
                        whenForfeit = true;
                        oneError = false;
                        onetimeForfeit = false;
                        Intent errorMessage = new Intent(getApplicationContext(), animationBOX.class);
                        String name_eror = "時間です";
                        errorMessage.putExtra("name_error_message", name_eror);
                        startActivity(errorMessage);
                        phase_change_box.setText(getString(R.string.end));
                        phase_change_box.setTextSize(30);
                        phase_change_box.setTextColor(getResources().getColor(R.color.white));
                        animationThread.startAnimation = true;
                        Toast.makeText(in_game.this, "勝った！！！", Toast.LENGTH_LONG).show();
                        handler5.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                //you win
                                doneSelection = true;
                                whenForfeit = true;
                                oneError = false;
                                onetimeForfeit = false;
                                Intent end = new Intent(getApplicationContext(), login_page.class);
                                startActivity(end);
                                stopService(new Intent(in_game.this,MyServiceInGame.class));
                                startService(new Intent(in_game.this,MyService.class));
                                finish();
                            }
                        }, 1200);
                    }
                }
            }


        };
    }

    @Override
    public void onStart() {
        super.onStart();
        mFirebaseDatabase.child("Battles").child(battleID).child(opponentID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!whenForfeit) {
                    if (turn != 0) {
                        ACTION_CODE = dataSnapshot.getValue(Integer.class);
                    }
                }
                if (ACTION_CODE == -1 && SELF_ACTION != -1) {
                    if (oneError) {
                        doneSelection = true;
                        whenForfeit = true;
                        oneError = false;
                        onetimeForfeit = false;
                        Intent errorMessage = new Intent(getApplicationContext(), animationBOX.class);
                        String name_eror = "時間です";
                        errorMessage.putExtra("name_error_message", name_eror);
                        startActivity(errorMessage);
                        phase_change_box.setText(getString(R.string.end));
                        phase_change_box.setTextSize(30);
                        phase_change_box.setTextColor(getResources().getColor(R.color.white));
                        animationThread.startAnimation = true;
                        final Handler handler5 = new Handler();
                        Toast.makeText(in_game.this, "勝った！！！", Toast.LENGTH_LONG).show();
                        handler5.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                //you win
                                doneSelection = true;
                                whenForfeit = true;
                                oneError = false;
                                onetimeForfeit = false;
                                Intent end = new Intent(getApplicationContext(), login_page.class);
                                startActivity(end);
                                stopService(new Intent(in_game.this,MyServiceInGame.class));
                                startService(new Intent(in_game.this,MyService.class));
                                finish();
                            }
                        }, 1200);
                    }
                }

                if (ACTION_CODE != 0) {
                    if (oneError) {
                        playerTwo_currentPhase.setText(getString(R.string.phase_two));
                        playerTwo_currentPhase.setTextSize(15);
                        playerTwo_currentPhase.setTextColor(getResources().getColor(R.color.white));

                        animationThread.startAnimation = true;
                    }
                }
                if (ACTION_CODE != 0 && SELF_ACTION != 0) {
                    if (!whenForfeit) {
                        GAMEFLOW();
                    }
                }
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("TAG", "Failed to retrieve Player values", databaseError.toException());
            }
        });

        mFirebaseDatabase.child("Players").child(opponentID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (oneTime) {
                    p2 = dataSnapshot.getValue(player.class);

                    playerTwoName.setText(getString(R.string.playerTwo));
                    playerTwoName.setTextSize(20);
                    playerTwoName.setTextColor(getResources().getColor(R.color.white));
                    playerTwoName.setAllCaps(true);

                    showPlayerStatus(p1, p2);
                    oneTime = false;
                }
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("TAG", "Failed to retrieve Player values", databaseError.toException());
            }
        });
    }

    //Phase Change
    public void playerOne_phase (int phase){
        if(phase == 1){
            timerValue.setVisibility(View.VISIBLE);
            turn += 1;
            final Handler handler5 = new Handler();
            handler5.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if(!whenClose) {
                        Intent errorMessage = new Intent(getApplicationContext(), animationBOX.class);
                        String name_eror = "TURN " + String.valueOf(turn);
                        errorMessage.putExtra("name_error_message", name_eror);
                        startActivity(errorMessage);
                    }
                }
            }, 200);
            p1.damage = 0;
            p1.heal = 0;
            playerOne_currentPhase.setText(getString(R.string.phase_one));
            playerOne_currentPhase.setTextSize(15);
            playerOne_currentPhase.setTextColor(getResources().getColor(R.color.white));
            playerTwo_currentPhase.setText(getString(R.string.phase_one));
            playerTwo_currentPhase.setTextSize(15);
            playerTwo_currentPhase.setTextColor(getResources().getColor(R.color.white));
            phase_change_box.setText(getString(R.string.changing_phase_one));
            phase_change_box.setTextSize(30);
            phase_change_box.setTextColor(getResources().getColor(R.color.white));
            animationThread.startAnimation = true;
            doneSelection = false;
            timerValue.setVisibility(View.VISIBLE);
            if(!ended) {
                countdown.start();
            }
        }
        if(phase == 2){
            countdownn.pause();
            doneSelection = true;
            timerValue.setVisibility(View.GONE);
            playerOne_currentPhase.setText(getString(R.string.phase_two));
            playerOne_currentPhase.setTextSize(15);
            playerOne_currentPhase.setTextColor(getResources().getColor(R.color.white));
            phase_change_box.setText(getString(R.string.changing_phase_one));
            phase_change_box.setTextSize(30);
            phase_change_box.setTextColor(getResources().getColor(R.color.white));
            animationThread.startAnimation = true;
        }
        if(phase == 3){
            countdown.cancel();
            playerOne_currentPhase.setText(getString(R.string.phase_two));
            playerOne_currentPhase.setTextSize(15);
            playerOne_currentPhase.setTextColor(getResources().getColor(R.color.white));
            playerTwo_currentPhase.setText(getString(R.string.phase_two));
            playerTwo_currentPhase.setTextSize(15);
            playerTwo_currentPhase.setTextColor(getResources().getColor(R.color.white));
            phase_change_box.setText(getString(R.string.changing_phase_1_5));
            phase_change_box.setTextSize(30);
            phase_change_box.setTextColor(getResources().getColor(R.color.white));
            animationThread.startAnimation = true;
        }
        if(phase == 4){
            playerOne_currentPhase.setText(getString(R.string.phase_three));
            playerOne_currentPhase.setTextSize(15);
            playerOne_currentPhase.setTextColor(getResources().getColor(R.color.white));
            playerTwo_currentPhase.setText(getString(R.string.phase_three));
            playerTwo_currentPhase.setTextSize(15);
            playerTwo_currentPhase.setTextColor(getResources().getColor(R.color.white));
            phase_change_box.setText(getString(R.string.changing_phase_two));
            phase_change_box.setTextSize(30);
            phase_change_box.setTextColor(getResources().getColor(R.color.white));
            animationThread.startAnimation = true;
        }
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

    Handler animationHandler = new Handler(){

        public void handleMessage (Message msg){
            super.handleMessage(msg);
            if(msg.what == 0){
                phase_change_box.startAnimation(anim);
            }
        }
    };

    public void showPlayerStatus (player player, player enemy){
        selfBleeding = player.bleed;
        oppBleeding = enemy.bleed;
        rock.setAnimation(finishUse);
        paper.setAnimation(finishUse);
        scissor.setAnimation(finishUse);
        angry.setAnimation(finishUse);
        //anim
        slash.start();
        flash.setBackgroundColor(Color.parseColor("#33FF0000"));
        flash.setVisibility(View.VISIBLE);
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                flash.setVisibility(View.GONE);
            }
        }, 300);
        //anim
        if(player.stopBleed){
            player.bleed = 0;
            selfBleeding = 0;
            player.stopBleed = false;
            Intent errorMessage = new Intent(getApplicationContext(), animationBOX.class);
            String name_eror = "出血は止めた。。";
            errorMessage.putExtra("name_error_message", name_eror);
            startActivity(errorMessage);
        }
        if(enemy.stopBleed){
            enemy.bleed = 0;
            oppBleeding = 0;
            enemy.stopBleed = false;
            Intent errorMessage = new Intent(getApplicationContext(), animationBOX.class);
            String name_eror = "出血は止めた。。";
            errorMessage.putExtra("name_error_message", name_eror);
            startActivity(errorMessage);
        }
        if(selfBleeding > 0){
            Intent errorMessage = new Intent(getApplicationContext(), animationBOX.class);
            String name_eror = "きゃ！僕の血が出てる\n" +"残り"+ String.valueOf(selfBleeding-1) + "ターン";
            errorMessage.putExtra("name_error_message", name_eror);
            startActivity(errorMessage);
            //Toast.makeText(this, player.name.toUpperCase() + " is bleeding!\n" + String.valueOf(player.currentBS()) + " turns left", Toast.LENGTH_LONG).show();
            player.setHpDamage(1);
            player.bleed -= 1;
        }
        if(oppBleeding > 0){
            Intent errorMessage = new Intent(getApplicationContext(), animationBOX.class);
            String name_eror = "ふあ、君の血が出てる\n" +"残り"+ String.valueOf(oppBleeding-1) + "ターン";
            errorMessage.putExtra("name_error_message", name_eror);
            startActivity(errorMessage);
            //Toast.makeText(this, player.name.toUpperCase() + " is bleeding!\n" + String.valueOf(player.currentBS()) + " turns left", Toast.LENGTH_LONG).show();
            enemy.setHpDamage(1);
            enemy.bleed -= 1;
        }
        if(player.reverseHeal){
            Intent errorMessage = new Intent(getApplicationContext(), animationBOX.class);
            String name_eror = "こわっ！ぼくの元気なくなちゃった";
            errorMessage.putExtra("name_error_message", name_eror);
            startActivity(errorMessage);
            player.setHpDamage(player.heal*2);
            player.reverseHeal = false;
        }
        if(enemy.reverseHeal){
            Intent errorMessage = new Intent(getApplicationContext(), animationBOX.class);
            String name_eror = "はっはっ！君の元気なくなちゃったね";
            errorMessage.putExtra("name_error_message", name_eror);
            startActivity(errorMessage);
            enemy.setHpDamage(enemy.heal*2);
            enemy.reverseHeal = false;
        }
        if(player.hp <= 0){
            if(enemy.hp <=0){
                if(oneError) {
                    doneSelection = true;
                    whenForfeit = true;
                    oneError = false;
                    onetimeForfeit = false;
                    Intent end = new Intent(getApplicationContext(), login_page.class);
                    startActivity(end);
                    Toast.makeText(in_game.this, "両方負け。悲し", Toast.LENGTH_LONG).show();
                    stopService(new Intent(in_game.this, MyServiceInGame.class));
                    startService(new Intent(in_game.this, MyService.class));
                    finish();
                }
            }
            else{
                if(oneError) {
                    doneSelection = true;
                    whenForfeit = true;
                    oneError = false;
                    onetimeForfeit = false;
                    Intent end = new Intent(getApplicationContext(), login_page.class);
                    startActivity(end);
                    Toast.makeText(in_game.this, "負けた！！！", Toast.LENGTH_LONG).show();
                    stopService(new Intent(in_game.this, MyServiceInGame.class));
                    startService(new Intent(in_game.this, MyService.class));
                    finish();
                }
            }
        }
        if(enemy.hp <=0){
            if(oneError) {
                doneSelection = true;
                whenForfeit = true;
                oneError = false;
                onetimeForfeit = false;
                Intent end = new Intent(getApplicationContext(), login_page.class);
                startActivity(end);
                Toast.makeText(in_game.this, "勝った！！！", Toast.LENGTH_LONG).show();
                stopService(new Intent(in_game.this, MyServiceInGame.class));
                startService(new Intent(in_game.this, MyService.class));
                finish();
            }
        }
        if(player.hp >5) {
            if(turn != 0) {
                while (player.hp > 5) {
                    player.setHpDamage(1);
                }
            }
        }
        if(enemy.hp >5) {
            if(turn !=0) {
                while (enemy.hp > 5) {
                    enemy.setHpDamage(1);
                }
            }
        }
        chargeReboot(player);
        chargeReboot(enemy);
        playerOne_hp.setText(String.valueOf(player.hp) + "/5");
        playerOne_hp.setTextSize(17);
        playerOne_hp.setTextColor(getResources().getColor(R.color.yellow));

        playerOne_rock.setText(String.valueOf(player.rock_charge) + "/1");
        playerOne_rock.setTextSize(17);
        playerOne_rock.setTextColor(getResources().getColor(R.color.yellow));

        playerOne_paper.setText(String.valueOf(player.paper_charge) + "/1");
        playerOne_paper.setTextSize(17);
        playerOne_paper.setTextColor(getResources().getColor(R.color.yellow));

        playerOne_scissor.setText(String.valueOf(player.scissor_charge) + "/1");
        playerOne_scissor.setTextSize(17);
        playerOne_scissor.setTextColor(getResources().getColor(R.color.yellow));

        playerOne_angry.setText(String.valueOf(player.angry_charge) + "/1");
        playerOne_angry.setTextSize(17);
        playerOne_angry.setTextColor(getResources().getColor(R.color.yellow));

        playerTwo_hp.setText(String.valueOf(enemy.hp) + "/5");
        playerTwo_hp.setTextSize(17);
        playerTwo_hp.setTextColor(getResources().getColor(R.color.yellow));

        playerTwo_rock.setText(String.valueOf(enemy.rock_charge) + "/1");
        playerTwo_rock.setTextSize(17);
        playerTwo_rock.setTextColor(getResources().getColor(R.color.yellow));

        playerTwo_paper.setText(String.valueOf(enemy.paper_charge) + "/1");
        playerTwo_paper.setTextSize(17);
        playerTwo_paper.setTextColor(getResources().getColor(R.color.yellow));

        playerTwo_scissor.setText(String.valueOf(enemy.scissor_charge) + "/1");
        playerTwo_scissor.setTextSize(17);
        playerTwo_scissor.setTextColor(getResources().getColor(R.color.yellow));

        playerTwo_angry.setText(String.valueOf(enemy.angry_charge) + "/1");
        playerTwo_angry.setTextSize(17);
        playerTwo_angry.setTextColor(getResources().getColor(R.color.yellow));
    }

    public void chargeReboot (player player){
        while (player.rock_charge < 0){
            player.setRock_charge(1);
        }
        while (player.paper_charge < 0){
            player.setPaper_charge(1);
        }
        while (player.scissor_charge < 0){
            player.setScissor_charge(1);
        }
        while (player.angry_charge < 0){
            player.setAngry_charge(1);
        }
        while (player.rock_charge >1){
            player.setRock_charge(-1);
        }
        while (player.paper_charge >1){
            player.setPaper_charge(-1);
        }
        while (player.scissor_charge >1){
            player.setScissor_charge(-1);
        }
        while (player.angry_charge >1){
            player.setAngry_charge(-1);
        }
    }

    public void preAnim(){
        card_flip.start();
        rock.startAnimation(slideUpAnimation);
        rock.setVisibility(View.VISIBLE);

        final Handler handler = new Handler();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                card_flip2.start();
                paper.startAnimation(slideUpAnimation4);
                paper.setVisibility(View.VISIBLE);
            }
        }, 400);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                card_flip3.start();
                scissor.startAnimation(slideUpAnimation3);
                scissor.setVisibility(View.VISIBLE);
            }
        }, 800);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                card_flip4.start();
                angry.startAnimation(slideUpAnimation2);
                angry.setVisibility(View.VISIBLE);
            }
        }, 1200);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                slash.start();
                Intent errorMessage = new Intent(getApplicationContext(), animationBOX.class);
                String name_eror = "最初はぐう\nじゃんけんぽん";
                errorMessage.putExtra("name_error_message", name_eror);
                startActivity(errorMessage);
                MediaPlayer start_match = MediaPlayer.create(in_game.this,R.raw.saishowaguu);
                start_match.start();
                playerOne_hp.startAnimation(slideRIGHT);
                playerOne_hp.setVisibility(View.VISIBLE);
                playerTwo_hp.startAnimation(slideLEFT);
                playerTwo_hp.setVisibility(View.VISIBLE);
                playerName.startAnimation(slideRIGHT);
                playerName.setVisibility(View.VISIBLE);
                playerTwoName.startAnimation(slideLEFT);
                playerTwoName.setVisibility(View.VISIBLE);
                showPlayerStatus(p1,p2);
            }
        }, 2000);
    }

    boolean skillcheck (action skill, player player){
        if(-skill.rock_own > player.rock_charge) {
            return false;
        }
        if(-skill.paper_own > player.paper_charge) {
            return false;
        }
        if(-skill.scissor_own > player.scissor_charge) {
            return false;
        }
        if(-skill.angry_own > player.angry_charge) {
            return false;
        }
        else{
            return true;
        }
    }

    public void action_function (player caster, player target){
        if (caster == p1){
            Intent errorMessage = new Intent(getApplicationContext(), animationBOX.class);
            String name_eror = "自分が"+ currentAction.name;
            errorMessage.putExtra("name_error_message", name_eror);
            int skill_caster;
            skill_caster = 1;
            errorMessage.putExtra("playerOne_cast", skill_caster);
            startActivity(errorMessage);
            caster.execute(currentAction, target);
        }
        else if (caster == p2){
            Intent errorMessage = new Intent(getApplicationContext(), animationBOX.class);
            String name_eror = "相手が"+ opponentAction.name;
            errorMessage.putExtra("name_error_message", name_eror);
            int skill_caster;
            skill_caster = 2;
            errorMessage.putExtra("playerOne_cast", skill_caster);
            startActivity(errorMessage);
            caster.execute(opponentAction, target);
        }
    }

    public void opponent_cast_skill(){
            mFirebaseDatabase.child("Action").orderByChild("action_ID").equalTo(ACTION_CODE).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        actionID = snapshot.getKey();
                        opponentAction = dataSnapshot.child(actionID).getValue(action.class);
                        if(opponentAction !=null){
                            action_sound.start();
                            action_function(p2,p1);
                            playerOne_phase(3);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void GAMEFLOW(){
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                opponent_cast_skill();
            }
        }, 3000);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                action_sound.start();
                action_function(p1,p2);
            }
        }, 7000);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                playerOne_phase(4);
            }
        }, 8000);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                showPlayerStatus(p1,p2);
            }
        }, 10000);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                playerOne_phase(1);
                permission = true;
                ACTION_CODE = 0;
                SELF_ACTION = 0;
                currentAction = null;
                opponentAction = null;
                actionID = null;
                if(!ended) {
                    mFirebaseDatabase.child("Battles").child(battleID).child(UserID).setValue(0);
                }
            }
        }, 12000);
    }

    @Override
    public void onBackPressed(){
    }

    @Override
    protected void onRestart() {
        startService(new Intent(this, MyServiceInGame.class));
        super.onRestart();
    }

    @Override
    protected void onStop() {
        /*Intent stopIntent = new Intent(getBaseContext(), MyService.class);
        stopIntent.setAction(MyService.ACTION_STOP);
        stopService(stopIntent);*/
        stopService(new Intent(this, MyServiceInGame.class));


        super.onStop();
    }

    @Override
    protected void onDestroy() {
        SELF_ACTION = -1;
        whenForfeit = true;
        oneError = false;
        ended = true;
        oneTimenoRepeat = false;
        whenClose = true;
        card_flip.release();
        card_flip2.release();
        card_flip3.release();
        card_flip4.release();
        slash.release();
        charge.release();
        super.onDestroy();
    }
}
