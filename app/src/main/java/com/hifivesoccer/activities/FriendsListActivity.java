package com.hifivesoccer.activities;

import android.app.Activity;
import android.app.ProgressDialog;
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
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hifivesoccer.R;
import com.hifivesoccer.adapters.FiendsListAdapter;
import com.hifivesoccer.adapters.GameListAdapter;
import com.hifivesoccer.models.Game;
import com.hifivesoccer.models.User;
import com.hifivesoccer.utils.MySelf;
import com.hifivesoccer.utils.ServerHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FriendsListActivity extends AppActivity {

    private static final String TAG = FriendsListActivity.class.getSimpleName();
    private final Context context = this;
    private final ServerHandler server = ServerHandler.getInstance(context);

    private Toolbar toolbar;

    private ListView listView;
    private FiendsListAdapter adapter;
    private List<User> userList = new ArrayList<>();

    private List<User> userListAdded = new ArrayList<>();

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

        Log.d(TAG, friendsId);

        final String[] friendsIdList = friendsId.split(",");
        String[] friendsNameList = friendsName.split(",");

        /*
        String friendsString = "";
        TextView friendsListText = (TextView) findViewById(R.id.friends_list);

        if (friendsIdList.length > 0) {

            for (int i = 0; i < friendsNameList.length; i++) {
                if (i != 0)
                    friendsString += ", ";
                friendsString += friendsNameList[i];
            }


            friendsListText.setText("Personnes déjà invité : " + friendsString);

            friendsListText.setVisibility(View.VISIBLE);
        }
        else {
            friendsListText.setText("");
            friendsListText.setVisibility(View.GONE);
        }
        */

        listView = (ListView) findViewById(R.id.users_list);

        adapter = new FiendsListAdapter(this, userList, userListAdded);
        listView.setAdapter(adapter);

        final ProgressDialog progress = new ProgressDialog(context);
        progress.setMessage(context.getString(R.string.loading));
        progress.show();

        server.getAllUsers(new ServerHandler.ResponseHandler() {
            @Override
            public void onSuccess(Object response) {
                Log.d(TAG, response.toString());

                JSONArray serializedUsers = (JSONArray) response;

                for (int i = 0; i < serializedUsers.length(); i++) {
                    try {
                        JSONObject serializedUser = serializedUsers.getJSONObject(i);
                        ObjectMapper mapper = new ObjectMapper();
                        try {
                            final User user = mapper.readValue(serializedUser.toString(), User.class);

                            if(user != null){
                                Log.d(TAG, user.toString());
                                if (!user.get_id().equals(MySelf.getSelf().get_id())) {

                                    if (Arrays.asList(friendsIdList).contains(user.get_id())) {
                                        userListAdded.add(user);
                                    }

                                    userList.add(user);

                                    adapter.notifyDataSetChanged();
                                }
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                            Toast toast = Toast.makeText(context, R.string.hifive_generic_error, Toast.LENGTH_SHORT);
                            toast.show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast toast = Toast.makeText(context, R.string.hifive_generic_error, Toast.LENGTH_SHORT);
                        toast.show();
                    }
                    adapter.notifyDataSetChanged();
                }
                //adapter.notifyDataSetChanged();
                progress.dismiss();
            }

            @Override
            public void onError(String error) {
                Log.e(TAG, error);
                progress.dismiss();
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
        String usersIdList = "";
        String usersNameList = "";

        for (int i = 0; i < userListAdded.size(); i++) {
            if (i != 0) {
                usersIdList += ",";
                usersNameList += ",";
            }
            usersIdList += userListAdded.get(i).get_id();
            usersNameList += userListAdded.get(i).getUsername();
        }

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

    public void addToList(View view) {

        View parentRow = (View) view.getParent();
        ListView listView = (ListView) parentRow.getParent();
        final int position = listView.getPositionForView(parentRow);

        userListAdded.add(userList.get(position));
        adapter.notifyDataSetChanged();
    }

    public void removeToList(View view) {

        View parentRow = (View) view.getParent();
        ListView listView = (ListView) parentRow.getParent();
        final int position = listView.getPositionForView(parentRow);

        userListAdded.remove(userList.get(position));
        adapter.notifyDataSetChanged();
    }
}
