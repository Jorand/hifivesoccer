package com.hifivesoccer.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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

import org.json.JSONException;
import org.json.JSONObject;

public class SubscribeActivity extends AppActivity {
    private static final String TAG = SubscribeActivity.class.getSimpleName();

    private final Context context = this;
    private final ServerHandler server = ServerHandler.getInstance(context);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscribe);
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

        if (!email.isEmpty() && !password.isEmpty()) {

            JSONObject json = new JSONObject();
            try {
                json.put("email", email);
                json.put("username", username);
                json.put("password", password);
            } catch (JSONException e){
                Log.e(TAG, e.toString());
            }

            server.authenticate(json, new ServerHandler.ResponseHandler() {
                @Override
                public void onSuccess(JSONObject response) {
                    Log.d(TAG, response.toString());
                }

                @Override
                public void onError(String error) {
                    Log.d(TAG, error);
                    JSONObject jsonError = new JSONObject();
                    try {
                        jsonError = new JSONObject(error);
                    } catch (JSONException e){
                        Log.e(TAG, e.toString());
                    }
                    if(jsonError.length() > 0){
                        String reason;
                        try {
                            reason = jsonError.getString("reason");
                        } catch (JSONException e){
                            Log.e(TAG, e.toString());
                            reason = getResources().getString(R.string.hifive_generic_error);
                        }
                        Toast toast = Toast.makeText(getBaseContext(), reason, Toast.LENGTH_SHORT);
                        toast.show();
                    } else {
                        Toast toast = Toast.makeText(getBaseContext(), R.string.hifive_generic_error, Toast.LENGTH_SHORT);
                        toast.show();
                    }

                }
            });

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
