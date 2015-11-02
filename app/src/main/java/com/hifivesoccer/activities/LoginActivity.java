package com.hifivesoccer.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.hifivesoccer.AppActivity;
import com.hifivesoccer.R;
import com.hifivesoccer.SubscribeActivity;
import com.hifivesoccer.utils.ServerHandler;

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

//        server.getAllUsers(new ServerHandler.ResponseHandler() {
//            @Override
//            public void onSuccess(Object datas) {
//                Log.d(TAG, datas.toString());
//            }
//
//            @Override
//            public void onError(String error) {
//                Log.d(TAG, error);
//            }
//        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
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
    }
}
