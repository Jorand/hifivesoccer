package com.hifivesoccer.activities;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.hifivesoccer.R;
import com.hifivesoccer.models.User;
import com.hifivesoccer.utils.MySelf;
import com.hifivesoccer.utils.ServerHandler;
import com.hifivesoccer.utils.SharedPref;
import com.hifivesoccer.utils.Token;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class SplashActivity extends AppCompatActivity {

    private static final String TAG = SplashActivity.class.getSimpleName();
    private final Context context = this;

    private final ServerHandler server = ServerHandler.getInstance(context);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        final TextView msg = (TextView) findViewById(R.id.act_splash_network);

        BroadcastReceiver networkStateReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo netInfo = cm.getActiveNetworkInfo();
                //should check null because in air plan mode it will be null
                boolean isConnected =  netInfo != null && netInfo.isConnected();
                if (isConnected){
                    init();
                    msg.setVisibility(View.GONE);
                }
                else {
                    msg.setVisibility(View.VISIBLE);
                }
            }
        };

        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkStateReceiver, filter);

    }

    private void init() {

        String token = SharedPref.getToken(this);
        User self = SharedPref.getMyself((Activity) context);
        if(self == null){
            GoToLogin();
            Log.d(TAG, "MySelf is null");
        } else {
            MySelf.setSelf((Activity) context, self);
            if(token != ""){
                Token.getToken(this, token);
                Intent intent = new Intent(context, MainActivity.class);
                startActivity(intent);
                finish();
            } else {
                GoToLogin();
                Log.d(TAG, "No token.");
            }
        }
    }

    public boolean isConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    private void GoToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

}
