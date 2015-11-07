package com.hifivesoccer.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.hifivesoccer.R;
import com.hifivesoccer.utils.ServerHandler;
import com.hifivesoccer.utils.SharedPref;
import com.hifivesoccer.utils.Token;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class SubscribeActivity extends AppActivity {

    private static final String TAG = SubscribeActivity.class.getSimpleName();

    private final Context context = this;
    private final ServerHandler server = ServerHandler.getInstance(context);

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscribe);

        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        // Auto-complete suggestions user account emails
        ArrayList<String> accountsEmails = getUserAccountEmail();
        setAutoCompleteText(R.id.act_subscribe_email, accountsEmails);

        EditText passwordField = (EditText) findViewById(R.id.act_subscribe_password);
        passwordField.setTransformationMethod(new PasswordTransformationMethod());
    }

    public void toggleVisibility(View view) {

        EditText editText = (EditText) findViewById(R.id.act_subscribe_password);
        CheckBox checkBox = (CheckBox) findViewById(R.id.togglePasswordVisibility);

        int start = editText.getSelectionStart();
        int end = editText.getSelectionEnd();

        if (checkBox.isChecked()) {
            editText.setTransformationMethod(null);
        }
        else {
            editText.setTransformationMethod(new PasswordTransformationMethod());
        }

        editText.setSelection(start, end);
    }

    public void onClickSubscribe(View view) {

        final String username = getEditTextValue(R.id.act_subscribe_name);
        final String email = getEditTextValue(R.id.act_subscribe_email);
        final String password = getEditTextValue(R.id.act_subscribe_password);

        if (!email.isEmpty() && !password.isEmpty() && !username.isEmpty()) {

            JSONObject json = new JSONObject();
            try {
                json.put("email", email);
                json.put("username", username);
                json.put("password", password);
            } catch (JSONException e){
                Log.e(TAG, e.toString());
            }

            server.authenticateToError(json, (Activity) context);

        } else {
            Toast toast = Toast.makeText(this, R.string.from_empty, Toast.LENGTH_SHORT);
            toast.show();
        }

    }

    public void onClickLogin(View view) {

        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();

    }
}
