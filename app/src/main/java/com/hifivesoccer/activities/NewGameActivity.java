package com.hifivesoccer.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hifivesoccer.R;
import com.hifivesoccer.models.Game;
import com.hifivesoccer.utils.MySelf;
import com.hifivesoccer.utils.ServerHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

public class NewGameActivity extends AppActivity {

    private static final String TAG = NewGameActivity.class.getSimpleName();
    private final Context context = this;

    private Toolbar toolbar;

    String friendsId = "";
    String friendsName = "";

    JSONArray pendingList = new JSONArray();

    private final ServerHandler server = ServerHandler.getInstance(context);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_game);

        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        final Button createButton = (Button) findViewById(R.id.new_match);

        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String placeName = getEditTextValue(R.id.place_name);
                final String placeAddress = getEditTextValue(R.id.place_address);
                final String gameDate = getEditTextValue(R.id.game_date);
                final String gamePrice = getEditTextValue(R.id.game_price);
                final String gameDescription = getEditTextValue(R.id.game_description);
                Switch isPrivateSwitch = (Switch) findViewById(R.id.private_game);
                final boolean isPrivate = isPrivateSwitch.isEnabled();


                if (!placeName.isEmpty() && !placeAddress.isEmpty() && !gameDate.isEmpty() && !gamePrice.isEmpty() && !gameDescription.isEmpty()) {

                    JSONObject json = new JSONObject();
                    try {
                        json.put("organizer", MySelf.getSelf().get_id());
                        json.put("description", gameDescription);
                        json.put("date", gameDate);
                        json.put("place", placeName);
                        json.put("price", Float.parseFloat(gamePrice));
                        json.put("private", isPrivate);
                        json.put("pending", pendingList);
                    } catch (JSONException e) {
                        Log.e(TAG, e.toString());
                    }

                    Log.d(TAG, json.toString());

                    server.postDatas("game", json, new ServerHandler.ResponseHandler() {
                        @Override
                        public void onSuccess(Object response) {
                            Log.d(TAG, response.toString());

                            Toast toast = Toast.makeText(context, "Game bien ajouté", Toast.LENGTH_SHORT);
                            toast.show();

                            //Intent intent = new Intent(context, GameDetailActivity.class);
                            //intent.putExtra("GAME_ID", gameList.get(position).get_id());
                            //startActivity(intent);
                        }

                        @Override
                        public void onError(String error){
                            Log.e(TAG, error);
                            Toast toast = Toast.makeText(context, R.string.hifive_generic_error, Toast.LENGTH_SHORT);
                            toast.show();
                        }
                    });

                } else {
                    Toast toast = Toast.makeText(context, R.string.from_empty, Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });

    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.e(TAG, String.valueOf(requestCode));
        Log.e(TAG, String.valueOf(resultCode));

        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {

                friendsId = data.getStringExtra("USERS_LIST_ID");
                friendsName = data.getStringExtra("USERS_LIST_NAME");
                Log.e(TAG, "List id: "+ friendsId);

                String[] friendsIdList = friendsId.split(",");
                String[] friendsNameList = friendsName.split(",");

                String friendsString = "";

                TextView friendsListText = (TextView) findViewById(R.id.friends_list);

                if (friendsNameList.length > 0) {

                    for (int i = 0; i < friendsNameList.length; i++) {
                        if (i != 0) {
                            friendsString += ", ";
                        }
                        friendsString += friendsNameList[i];

                        pendingList.put(friendsIdList[i]);
                    }

                    friendsListText.setText("Personnes invité : " + friendsString);
                    friendsListText.setVisibility(View.VISIBLE);
                }
                else {
                    friendsListText.setText("");
                    friendsListText.setVisibility(View.GONE);
                }

            }
        }
    }

    public void addFriends(View view) {

        Intent intent = new Intent(context, FriendsListActivity.class);
        intent.putExtra("USERS_LIST_ID", friendsId);
        intent.putExtra("USERS_LIST_NAME", friendsName);
        startActivityForResult(intent, 0);
    }

    public void newGame(View view) {



    }
}
