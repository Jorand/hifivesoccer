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
import android.widget.Toast;

import com.hifivesoccer.R;
import com.hifivesoccer.models.Game;
import com.hifivesoccer.utils.ServerHandler;

public class FriendsListActivity extends AppActivity {

    private static final String TAG = FriendsListActivity.class.getSimpleName();
    private final Context context = this;
    private final ServerHandler server = ServerHandler.getInstance(context);

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_list);

        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        String friendsId = getIntent().getStringExtra("USERS_LIST_ID");
        String friendsName = getIntent().getStringExtra("USERS_LIST_NAME");

        String[] friendsIdList = friendsId.split(",");
        String[] friendsNameList = friendsName.split(",");

        String friendsString = "";


        if (friendsIdList.length > 0) {

            for (int i = 0; i < friendsNameList.length; i++) {
                if (i != 0)
                    friendsString += ", ";
                friendsString += friendsNameList[i];
            }

            TextView friendsListText = (TextView) findViewById(R.id.friends_list);
            friendsListText.setText("Personnes déjà invité : " + friendsString);

            friendsListText.setVisibility(View.VISIBLE);
        }

        server.getAllUsers(new ServerHandler.ResponseHandler() {
            @Override
            public void onSuccess(Object response) {
                Log.d(TAG, response.toString());

            }

            @Override
            public void onError(String error) {
                Log.e(TAG, error);
                Toast toast = Toast.makeText(context, R.string.hifive_generic_error, Toast.LENGTH_SHORT);
                toast.show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_friends_list, menu);
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
                finish();
                return true;
            default:
                finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void finish() {

        Intent intent = new Intent();
        String usersIdList = "1,2,3";
        String usersNameList = "Jorand,Hugo,David";
        intent.putExtra("USERS_LIST_ID", usersIdList);
        intent.putExtra("USERS_LIST_NAME", usersNameList);
        setResult(RESULT_OK, intent);

        super.finish();
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();

        finish();
    }

    public void sendList(View view) {

        finish();
    }
}
