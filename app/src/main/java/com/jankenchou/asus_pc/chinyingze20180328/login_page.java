package com.jankenchou.asus_pc.chinyingze20180328;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.AdRequest;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

/**
 * Created by Asus-PC on 3/28/2018.
 */

public class login_page  extends AppCompatActivity {

    public static login_page instance = null;
    private InterstitialAd mInterstitialAd;

    Button start;
    Button quit;
    private FirebaseAuth mAuth;
    public GoogleApiClient googleApiClient;
    public static final int RequestSignInCode = 7;
    com.google.android.gms.common.SignInButton google_log_in;
    LoginButton facebook_log_in;
    private CallbackManager mCallbackManager;

    private static final String TAG = "FacebookLogin";

    private DatabaseReference mFirebaseDatabase;
    private FirebaseDatabase mFirebaseInstance;

    MediaPlayer button;
    player player;

    private SharedPreferences.Editor preferenceEditor;
    private SharedPreferences preferenceSettings;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.login_page);
        MobileAds.initialize(this, "ca-app-pub-4117031435612383~4641977196");

        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-4117031435612383/9865904788");
        AdRequest request =new AdRequest.Builder().build();
        mInterstitialAd.loadAd(request);

        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                // Load the next interstitial.
                mInterstitialAd.loadAd(new AdRequest.Builder().build());
            }

        });
        instance = this;



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

        mAuth = FirebaseAuth.getInstance();
        mFirebaseInstance = FirebaseDatabase.getInstance();
        //get reference to PLAYER node
        mFirebaseDatabase = mFirebaseInstance.getReference("GamingData");
        mFirebaseDatabase.keepSynced(true);

        // Creating and Configuring Google Sign In object.
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        // Creating and Configuring Google Api Client.
        googleApiClient = new GoogleApiClient.Builder(login_page.this)
                .enableAutoManage(login_page.this , new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

                    }
                } /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, googleSignInOptions)
                .build();

        google_log_in = (com.google.android.gms.common.SignInButton)findViewById(R.id.google);
        google_log_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserSignInMethod();
            }
        });

        mCallbackManager = CallbackManager.Factory.create();

        facebook_log_in = (LoginButton) findViewById(R.id.facebook);

        facebook_log_in.setReadPermissions("email", "public_profile");

        facebook_log_in.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                button.start();
                Log.d(TAG, "facebook:onSuccess:" + loginResult);
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "facebook:onCancel");
                updateUI(null);
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "facebook:onError", error);
                updateUI(null);
            }
        });

        start = (Button)findViewById(R.id.start_button);
        quit = (Button)findViewById(R.id.quit_button);
        button = MediaPlayer.create(this, R.raw.button_click);

        start.setSoundEffectsEnabled(false);
        quit.setSoundEffectsEnabled(false);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                button.start();
                Intent intent = new Intent(getApplicationContext(), match_making.class);
                startActivity(intent);
            }
        });

        quit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                button.start();
                finish();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        if(mAuth.getCurrentUser() ==null) {
            start.setVisibility(View.GONE);
            quit.setVisibility(View.GONE);
        }
        else{
            google_log_in.setVisibility(View.GONE);
            facebook_log_in.setVisibility(View.GONE);
            start.setVisibility(View.VISIBLE);
            quit.setVisibility(View.VISIBLE);
            mFirebaseDatabase.child("Players").child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    player = dataSnapshot.getValue(player.class);
                    if (player == null) {
                        player playerOne = new player( 5,0,0,0,0,0);
                        //mFirebaseDatabase.child(UserID).setValue(playerOne);
                        try {
                            mFirebaseDatabase.child("Players").child(mAuth.getCurrentUser().getUid()).setValue(playerOne);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        //Toast.makeText(getApplicationContext(), "waaaaa", Toast.LENGTH_LONG).show();
                    }
                }


                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.w("TAG", "Failed to retrieve Player values", databaseError.toException());
                }
            });
            preferenceSettings = PreferenceManager.getDefaultSharedPreferences(login_page.this);
            preferenceEditor = preferenceSettings.edit();
            preferenceEditor.putString("UserID", mAuth.getCurrentUser().getUid());
            preferenceEditor.apply();

        }
    }

    public void UserSignInMethod(){

        // Passing Google Api Client into Intent.
        Intent AuthIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);

        startActivityForResult(AuthIntent, RequestSignInCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Pass the activity result back to the Facebook SDK
        mCallbackManager.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RequestSignInCode){

            GoogleSignInResult googleSignInResult = Auth.GoogleSignInApi.getSignInResultFromIntent(data);

            if (googleSignInResult.isSuccess()){

                GoogleSignInAccount googleSignInAccount = googleSignInResult.getSignInAccount();

                FirebaseUserAuth(googleSignInAccount);
            }

        }
    }

    public void FirebaseUserAuth(GoogleSignInAccount googleSignInAccount) {

        AuthCredential authCredential = GoogleAuthProvider.getCredential(googleSignInAccount.getIdToken(), null);

        //Toast.makeText(battlesphere.this,""+ authCredential.getProvider(),Toast.LENGTH_LONG).show();

        mAuth.signInWithCredential(authCredential)
                .addOnCompleteListener(login_page.this, new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task AuthResultTask) {

                        if (AuthResultTask.isSuccessful()){

                            // Getting Current Login user details.
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);


                        }else {
                            Toast.makeText(login_page.this,"Something Went Wrong",Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }


    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(login_page.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }


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

    public void updateUI(FirebaseUser user){
        if(user!=null) {
            preferenceSettings = PreferenceManager.getDefaultSharedPreferences(login_page.this);
            preferenceEditor = preferenceSettings.edit();
            preferenceEditor.putString("UserID", user.getUid());
            preferenceEditor.apply();
            google_log_in.setVisibility(View.GONE);
            facebook_log_in.setVisibility(View.GONE);
            start.setVisibility(View.VISIBLE);
            quit.setVisibility(View.VISIBLE);
            mFirebaseDatabase.child("Players").child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    player = dataSnapshot.getValue(player.class);
                    if (player == null) {
                        player playerOne = new player(5, 0, 0, 0, 0, 0);
                        Gson gson = new Gson();
                        String json = gson.toJson(player);
                        preferenceSettings = PreferenceManager.getDefaultSharedPreferences(login_page.this);
                        preferenceEditor = preferenceSettings.edit();
                        preferenceEditor.putString("playerONE", json);
                        preferenceEditor.apply();
                        //mFirebaseDatabase.child(UserID).setValue(playerOne);
                        try {
                            mFirebaseDatabase.child("Players").child(mAuth.getCurrentUser().getUid()).setValue(playerOne);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        Gson gson = new Gson();
                        String json = gson.toJson(player);
                        preferenceSettings = PreferenceManager.getDefaultSharedPreferences(login_page.this);
                        preferenceEditor = preferenceSettings.edit();
                        preferenceEditor.putString("playerONE", json);
                        preferenceEditor.apply();
                    }
                }


                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.w("TAG", "Failed to retrieve Player values", databaseError.toException());
                }
            });
        }
    }

    @Override
    public void finish(){
        super.finish();
        instance = null;
    }

    @Override
    protected void onResume() {
        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        } else {
            Log.d("TAG", "The interstitial wasn't loaded yet.");
        }
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        stopService(new Intent(login_page.this, MyService.class));
        button.release();
        super.onDestroy();
    }
}
