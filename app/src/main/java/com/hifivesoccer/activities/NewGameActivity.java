package com.hifivesoccer.activities;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.hifivesoccer.R;
import com.hifivesoccer.utils.ServerHandler;

import java.util.ArrayList;

public class NewGameActivity extends AppActivity {

    private static final String TAG = NewGameActivity.class.getSimpleName();
    private final Context context = this;

    private Toolbar toolbar;

    String friendsId = "";
    String friendsName = "";

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
                Log.e(TAG, friendsId);

                String[] friendsIdList = friendsId.split(",");
                String[] friendsNameList = friendsName.split(",");

                String friendsString = "";

                for (int i = 0; i < friendsNameList.length; i++) {
                    if (i != 0)
                        friendsString += ", ";
                    friendsString += friendsNameList[i];
                }

                TextView friendsListText = (TextView) findViewById(R.id.friends_list);
                friendsListText.setText("Personnes invité : "+ friendsString);

                friendsListText.setVisibility(View.VISIBLE);

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

        // POST
    }
}
