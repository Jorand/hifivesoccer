package com.hifivesoccer.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hifivesoccer.R;
import com.hifivesoccer.models.User;
import com.hifivesoccer.utils.MySelf;
import com.hifivesoccer.utils.ServerHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;

public class UserSettingsActivity extends AppActivity {

    private static final String TAG = FriendsListActivity.class.getSimpleName();
    private final Context context = this;
    private final ServerHandler server = ServerHandler.getInstance(context);

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_settings);

        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        EditText username = (EditText) findViewById(R.id.act_settings_username);
        EditText position = (EditText) findViewById(R.id.act_settings_position);

        if(MySelf.getSelf() != null){
            Log.d(TAG, MySelf.getSelf().get_id());
            username.setText(MySelf.getSelf().getUsername());
            position.setText(MySelf.getSelf().getPosition());
        } else {
            Log.d(TAG, "MySelf is null");
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_user_settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_done:
                updateProfile();
                return true;
            default:
                finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateProfile() {

        String usernameText = getEditTextValue(R.id.act_settings_username);
        String positionText = getEditTextValue(R.id.act_settings_position);

        if (!usernameText.isEmpty()) {

            JSONObject json = new JSONObject();
            try {
                json.put("_id", MySelf.getSelf().get_id());
                json.put("username", usernameText);
                json.put("position", positionText);

                final ProgressDialog progress = new ProgressDialog(context);
                progress.setMessage(context.getString(R.string.loading));
                progress.show();

                server.putDatas("user", json, new ServerHandler.ResponseHandler() {
                    @Override
                    public void onSuccess(Object response) {
                        Log.d(TAG, response.toString());

                        JSONObject serializedUser = (JSONObject) response;
                        ObjectMapper mapper = new ObjectMapper();
                        try {
                            final User user = mapper.readValue(serializedUser.toString(), User.class);

                            if(user != null){
                                Log.d(TAG, user.toString());
                                if (user.get_id().equals(MySelf.getSelf().get_id())) {

                                    MySelf.setSelf(user);

                                    Intent intent = new Intent(context, MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                            Toast toast = Toast.makeText(context, R.string.hifive_generic_error, Toast.LENGTH_SHORT);
                            toast.show();
                        }

                        progress.dismiss();
                    }

                    @Override
                    public void onError(String error) {
                        Log.e(TAG, error);
                        Toast toast = Toast.makeText(context, R.string.hifive_generic_error, Toast.LENGTH_SHORT);
                        toast.show();
                        progress.dismiss();
                    }
                });

            } catch (JSONException e) {
                Log.e(TAG, e.toString());
                Toast toast = Toast.makeText(context, R.string.hifive_generic_error, Toast.LENGTH_SHORT);
                toast.show();
            }

        } else {
            Toast toast = Toast.makeText(context, R.string.from_empty, Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    public void onClickPicture(View view) {
        // TODO update profile picture
        updateProfile();
    }
}
