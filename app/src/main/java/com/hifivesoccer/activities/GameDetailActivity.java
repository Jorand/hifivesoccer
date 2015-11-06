package com.hifivesoccer.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.internal.widget.AdapterViewCompat;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hifivesoccer.R;
import com.hifivesoccer.adapters.GameListAdapter;
import com.hifivesoccer.adapters.TeamListAdapter;
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

public class GameDetailActivity extends AppActivity {

    private static final String TAG = GameDetailActivity.class.getSimpleName();
    private final Context context = this;
    private final ServerHandler server = ServerHandler.getInstance(context);

    private Toolbar toolbar;
    private Button gameButton;

    private Boolean isOrganizer = false;
    private User me;
    private String myId;

    private String status = "";

    private List<User> teamAList;
    private List<User> teamBList;

    private TeamListAdapter adapterTeamA;
    private TeamListAdapter adapterTeamB;
    private String[] myGamesIds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_detail);

        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        final String gameId = getIntent().getStringExtra("GAME_ID");

        final TextView OrganizerName = (TextView) findViewById(R.id.act_game_detail_organizer_username);
        final TextView GameDate = (TextView) findViewById(R.id.act_game_detail_date);
        final TextView GameTime = (TextView) findViewById(R.id.act_game_detail_time);
        final TextView GamePlace = (TextView) findViewById(R.id.act_game_detail_place);
        final TextView GamePrice = (TextView) findViewById(R.id.act_game_detail_price);

        gameButton = (Button) findViewById(R.id.act_game_detail_button);

        teamAList = new ArrayList<>();
        teamBList = new ArrayList<>();

        adapterTeamA = new TeamListAdapter(this, teamAList);
        final ListView teamA = (ListView) findViewById(R.id.act_game_detail_team_a_list);
        teamA.setAdapter(adapterTeamA);

        adapterTeamB = new TeamListAdapter(this, teamBList);
        final ListView teamB = (ListView) findViewById(R.id.act_game_detail_team_b_list);
        teamB.setAdapter(adapterTeamB);

        if(MySelf.getSelf() != null){
            me = MySelf.getSelf();
            myId = MySelf.getSelf().get_id();
            myGamesIds = MySelf.getSelf().getGamesIDs();

        }

        server.getGame(gameId, new ServerHandler.ResponseHandler() {
            @Override
            public void onSuccess(Object response) {
                Log.d(TAG, response.toString());

                JSONObject serializedGame = (JSONObject) response;
                ObjectMapper mapper = new ObjectMapper();
                try {
                    final Game game = mapper.readValue(serializedGame.toString(), Game.class);
                    game.initPeoples(context, new Game.initHandler() {
                        @Override
                        public void handle() {

                            if (game.getOrganizerID().equals(myId)) {
                                isOrganizer = true;
                            }

                            OrganizerName.setText(game.getOrganizer().getProfile().getUsername());
                            GameDate.setText(game.getDate());
                            GameTime.setText(game.getDate());
                            GamePlace.setText(game.getPlace());
                            GamePrice.setText(String.valueOf(game.getPrice()));

                            teamAList = (List<User>) game.getTeamA();
                            teamBList = (List<User>) game.getTeamB();

                            if (teamAList.contains(me)) {
                                status = "teamA";
                                gameButton.setText(R.string.game_menu_team_a);
                            }

                            if (teamBList.contains(me)) {
                                status = "teamB";
                                gameButton.setText(R.string.game_menu_team_b);
                            }

                            adapterTeamA.notifyDataSetChanged();
                            adapterTeamB.notifyDataSetChanged();
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast toast = Toast.makeText(context, R.string.hifive_generic_error, Toast.LENGTH_SHORT);
                    toast.show();
                }

            }

            @Override
            public void onError(String error) {
                Log.e(TAG, error);
                Toast toast = Toast.makeText(context, R.string.hifive_generic_error, Toast.LENGTH_SHORT);
                toast.show();
            }
        });

        gameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                PopupMenu popup = new PopupMenu(context, gameButton);
                popup.getMenuInflater().inflate(R.menu.game_action_menu, popup.getMenu());

                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        //Toast.makeText(context, "You Clicked : " + item.getTitle(), Toast.LENGTH_SHORT).show();

                        int id = item.getItemId();

                        if (id == R.id.game_menu_team_a) {
                            joinTeam("teamA");
                        }

                        if (id == R.id.game_menu_team_b) {
                            joinTeam("teamB");
                        }

                        if (id == R.id.game_menu_exit) {
                            exitGame();
                        }

                        return true;
                    }
                });

                popup.show();
            }
        });

    }

    private void joinTeam(String team) {
        Toast.makeText(context, "You join Team " + team, Toast.LENGTH_SHORT).show();

        if (team.equals("teamA"))
            gameButton.setText(R.string.game_menu_team_a);

        if (team.equals("teamB"))
            gameButton.setText(R.string.game_menu_team_b);
    }

    private void exitGame() {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setMessage(R.string.dialog_exit_game_msq)
                .setTitle(R.string.dialog_exit_game_title);

        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        Toast.makeText(context, "You join left the game !", Toast.LENGTH_SHORT).show();
                        gameButton.setText(R.string.game_menu_default);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.findItem(R.id.action_edit).setVisible(isOrganizer);
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_game_detail, menu);
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

}
