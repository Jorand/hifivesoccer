package com.hifivesoccer.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.widget.EditText;

import com.hifivesoccer.R;
import com.hifivesoccer.utils.ServerHandler;
import com.hifivesoccer.utils.SharedPref;

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

        String email;
        String pass;

        JSONObject myself;
        String serializedSelf = SharedPref.getMyself((Activity) context);
        if(serializedSelf.length() > 0){
            try{
                myself = new JSONObject(serializedSelf);
                JSONObject profile = myself.getJSONObject("profile");
                try {
                    email = profile.getString("email");
                    pass = profile.getString("password");

                    JSONObject json = new JSONObject();
                    json.put("email", email);
                    json.put("password", pass);

                    server.authenticateToLogin(json, (Activity) context);
                } catch (JSONException e){
                    Log.e(TAG, e.toString());
                    GoToLogin();
                }
            } catch (JSONException e){
                Log.e(TAG, e.toString());
                GoToLogin();
            }
        }
        else {
            GoToLogin();
        }

    }

    private void GoToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

}
