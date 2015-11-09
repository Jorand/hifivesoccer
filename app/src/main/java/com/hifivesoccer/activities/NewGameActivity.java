package com.hifivesoccer.activities;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.hifivesoccer.R;
import com.hifivesoccer.utils.MySelf;
import com.hifivesoccer.utils.ServerHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class NewGameActivity extends AppActivity {

    private static final String TAG = NewGameActivity.class.getSimpleName();
    private final Context context = this;

    private Toolbar toolbar;

    String friendsId = "";
    String friendsName = "";

    JSONArray pendingList = new JSONArray();

    private final ServerHandler server = ServerHandler.getInstance(context);

    static final int DIALOG_DATE_ID = 0;
    static final int DIALOG_TIME_ID = 1;

    private TextView button_date;
    int year_x;
    int month_x;
    int day_x;

    private TextView button_time;
    int hours_x;
    int min_x;

    private Calendar cal;

    private Date gameDate;
    private Date gameTime;

    boolean isPrivate = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_game);

        // TOOLBAR
        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        Switch isPrivateSwitch = (Switch) findViewById(R.id.private_game);

        isPrivateSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                isPrivate = isChecked;
            }
        });

        final Button createButton = (Button) findViewById(R.id.new_match);

        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String placeName = getEditTextValue(R.id.place_name);
                final String placeAddress = getEditTextValue(R.id.place_address);
                final String gamePrice = getEditTextValue(R.id.game_price);
                final String gameDescription = getEditTextValue(R.id.game_description);

                String postGameDate = new SimpleDateFormat("EEE dd MMM yyyy", Locale.getDefault()).format(gameDate);
                String postGameTime = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(gameTime);

                if (!placeName.isEmpty() && !placeAddress.isEmpty() && !gamePrice.isEmpty()) {

                    JSONObject json = new JSONObject();
                    try {
                        json.put("organizer", MySelf.getSelf().get_id());
                        json.put("description", gameDescription);
                        json.put("date", postGameDate);
                        json.put("time", postGameTime);
                        json.put("place", placeName);
                        json.put("price", Float.parseFloat(gamePrice));
                        json.put("private", isPrivate);

                        JSONArray players = new JSONArray();

                        try {
                            JSONObject player = new JSONObject();
                            player.put("id", MySelf.getSelf().get_id());
                            player.put("team", "A");
                            players.put(player);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        if (pendingList.length() > 0){
                            for (int i = 0; i < pendingList.length(); i++) {
                                JSONObject player = new JSONObject();
                                try {
                                    player.put("id", pendingList.get(i));
                                    player.put("team", "pending");
                                    players.put(player);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                        if(players.length() > 0){
                            json.put("players", players);
                        }

                        final ProgressDialog progress = new ProgressDialog(context);
                        progress.setMessage(context.getString(R.string.loading));
                        progress.show();

                        server.postDatas("game", json, new ServerHandler.ResponseHandler() {
                            @Override
                            public void onSuccess(Object response) {
                                Log.d(TAG, response.toString());

                                progress.dismiss();

                                JSONObject res = (JSONObject) response;

                                try {
                                    String id = res.getString("id");

                                    // TODO Put game in user games
                                    //putInUserGames(id);

                                    Intent intent = new Intent(context, GameDetailActivity.class);
                                    intent.putExtra("GAME_ID", id);
                                    intent.putExtra("NOTIFY", true);
                                    startActivity(intent);
                                    finish();

                                } catch (JSONException e) {
                                    Log.e(TAG, e.toString());

                                    Intent intent = new Intent(context, MainActivity.class);
                                    startActivity(intent);
                                    finish();

                                    e.printStackTrace();
                                }

                                //Toast toast = Toast.makeText(context, "Game bien ajouté", Toast.LENGTH_SHORT);
                                //toast.show();
                            }

                            @Override
                            public void onError(String error){
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
        });

        cal = Calendar.getInstance(Locale.getDefault());

        TimeZone tz = TimeZone.getDefault();
        cal.setTimeZone(tz);

        year_x = cal.get(Calendar.YEAR);
        month_x = cal.get(Calendar.MONTH);
        day_x = cal.get(Calendar.DATE);

        hours_x = cal.get(Calendar.HOUR_OF_DAY);
        min_x = cal.get(Calendar.MINUTE);

        gameDate = cal.getTime();
        gameTime = cal.getTime();

        showDialogOnButtonClick();
        showTimePickerDialog();

    }

    private void putInUserGames(String id) {

        JSONObject json = new JSONObject();
        try {
            json.put("_id", MySelf.getSelf().get_id());
            JSONArray games = new JSONArray();
            games.put(id);
            json.put("games", games);

            server.putDatas("user", json, new ServerHandler.ResponseHandler() {
                @Override
                public void onSuccess(Object response) {
                    Log.d(TAG, response.toString());


                }

                @Override
                public void onError(String error) {
                    Log.e(TAG, error);
                }
            });

        } catch (JSONException e) {
            Log.e(TAG, e.toString());
        }
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch(id) {
            case DIALOG_DATE_ID:
                return new DatePickerDialog(this, dpickerListner, year_x, month_x, day_x);
            case DIALOG_TIME_ID:
                return new TimePickerDialog(this, tpickerListner, hours_x, min_x, true);
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener dpickerListner = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            year_x = year;
            month_x = monthOfYear + 1;
            day_x = dayOfMonth;

            Calendar calendar = Calendar.getInstance();
            calendar.set(year,monthOfYear,dayOfMonth);

            gameDate = calendar.getTime();
            button_date.setText(new SimpleDateFormat("EEE dd MMM yyyy", Locale.getDefault()).format(gameDate));

        }
    };

    public void showDialogOnButtonClick() {
        button_date = (TextView) findViewById(R.id.game_date);
        button_date.setText(new SimpleDateFormat("EEE dd MMM yyyy", Locale.getDefault()).format(cal.getTime()));

        button_date.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showDialog(DIALOG_DATE_ID);
                    }
                }
        );
    }

    private TimePickerDialog.OnTimeSetListener tpickerListner = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            hours_x = hourOfDay;
            min_x = minute;

            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
            calendar.set(Calendar.MINUTE, minute);
            TimeZone tz = TimeZone.getDefault();
            calendar.setTimeZone(tz);

            gameTime = calendar.getTime();

            button_time.setText(new SimpleDateFormat("HH:mm", Locale.getDefault()).format(gameTime));

        }
    };

    public void showTimePickerDialog() {
        button_time = (TextView) findViewById(R.id.game_time);
        button_time.setText(new SimpleDateFormat("HH:mm", Locale.getDefault()).format(cal.getTime()));
        button_time.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showDialog(DIALOG_TIME_ID);
                    }
                }
        );
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
                if(friendsId.equals("")){
                    return;
                }

                pendingList = new JSONArray();

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
