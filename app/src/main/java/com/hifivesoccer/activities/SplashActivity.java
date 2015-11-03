package com.hifivesoccer.activities;

import android.app.Activity;
import android.content.Context;
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

    private static final String TAG = LoginActivity.class.getSimpleName();
    private final Context context = this;

    private final ServerHandler server = ServerHandler.getInstance(context);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

    }

}
