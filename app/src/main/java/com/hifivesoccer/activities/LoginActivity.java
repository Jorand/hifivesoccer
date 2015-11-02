package com.hifivesoccer.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.hifivesoccer.R;
import com.hifivesoccer.utils.ServerHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class LoginActivity extends AppActivity {
    private static final String TAG = LoginActivity.class.getSimpleName();

    private final Context context = this;
    private final ServerHandler server = ServerHandler.getInstance(context);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Auto-complete suggestions user account emails
        ArrayList<String> accountsEmails = getUserAccountEmail();
        setAutoCompleteText(R.id.act_login_email, accountsEmails);

        EditText passwordField = (EditText) findViewById(R.id.act_login_password);
        passwordField.setTransformationMethod(new PasswordTransformationMethod());
    }

    public void toggleVisibility(View view) {
        EditText editText = (EditText) findViewById(R.id.act_login_password);
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

    public void onClickLogin(View view) {

        final String email = getEditTextValue(R.id.act_login_email);
        final String password = getEditTextValue(R.id.act_login_password);

        if (!email.isEmpty() && !password.isEmpty()) {

            // login

        } else {
            Toast toast = Toast.makeText(this, R.string.from_empty, Toast.LENGTH_SHORT);
            toast.show();
        }

    }

    public void onClickRegister(View view) {

        Intent intent = new Intent(this, SubscribeActivity.class);
        startActivity(intent);
        finish();
    }
}
