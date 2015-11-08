package com.hifivesoccer.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.internal.widget.AdapterViewCompat;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import java.util.Collections;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class GameDetailActivity extends AppActivity {

    private static final String TAG = GameDetailActivity.class.getSimpleName();
    private final Context context = this;
    private final ServerHandler server = ServerHandler.getInstance(context);

    private LayoutInflater inflater;

    private Toolbar toolbar;
    private Button gameButton;

    private Boolean isOrganizer = false;
    private User me;
    private Game game;
    private String myId;

    private String gameId;

    private String status = "";

    private List<User> teamAList = new ArrayList<>();
    private List<User> teamBList = new ArrayList<>();

    private List<String> teamAListIds = new ArrayList<>();
    private List<String> teamBListIds = new ArrayList<>();

    private TeamListAdapter adapterTeamA;
    private TeamListAdapter adapterTeamB;
    private String[] myGamesIds;

    private LinearLayout teamA;
    private LinearLayout teamB;

    private ArrayList<String> pendingListIds = new ArrayList<>();
    private ArrayList<String> pendingListNames = new ArrayList<>();

    private ArrayList<User> pendingList = new ArrayList<>();

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

        gameId = getIntent().getStringExtra("GAME_ID");

        gameButton = (Button) findViewById(R.id.act_game_detail_button);

        teamA = (LinearLayout) findViewById(R.id.act_game_detail_team_a_list);
        teamB = (LinearLayout) findViewById(R.id.act_game_detail_team_b_list);

        if(MySelf.getSelf() != null){
            me = MySelf.getSelf();
            myId = MySelf.getSelf().get_id();
            myGamesIds = MySelf.getSelf().getGamesIDs();

        }

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

    private void updateTeamList() {

        teamA.removeAllViews();
        teamB.removeAllViews();

        if (inflater == null)
            inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        for (int i = 0; i < teamAList.size(); i++) {
            User user = teamAList.get(i);
            View convertView = inflater.inflate(R.layout.team_list_reverse, null);

            CircleImageView userPicture = (CircleImageView) convertView.findViewById(R.id.teamlist_picture);
            TextView userName = (TextView) convertView.findViewById(R.id.teamlist_username);

            userName.setText(teamAList.get(i).getUsername());

            if (teamAList.get(i).getPicture() != null) {

                byte[] decodedString = Base64.decode(teamAList.get(i).getPicture(), Base64.DEFAULT);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

                userPicture.setImageBitmap(decodedByte);
            }

            teamA.addView(convertView);
        }

        for (int i = 0; i < teamBList.size(); i++) {
            User user = teamBList.get(i);
            View convertView = inflater.inflate(R.layout.team_list, null);

            CircleImageView userPicture = (CircleImageView) convertView.findViewById(R.id.teamlist_picture);
            TextView userName = (TextView) convertView.findViewById(R.id.teamlist_username);

            userName.setText(teamBList.get(i).getUsername());

            if (teamBList.get(i).getPicture() != null) {

                byte[] decodedString = Base64.decode(teamBList.get(i).getPicture(), Base64.DEFAULT);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

                userPicture.setImageBitmap(decodedByte);
            }

            teamB.addView(convertView);
        }
    }

    private void joinTeam(final String team) {

        JSONObject json = new JSONObject();

        if (team.equals("teamA")) {
            if (!teamAListIds.contains(myId)){
                teamAListIds.add(myId);
                teamBListIds.remove(myId);
            } else {
                return;
            }
        }

        if (team.equals("teamB")) {
            if (!teamBListIds.contains(myId)){
                teamBListIds.add(myId);
                teamAListIds.remove(myId);
            } else {
                return;
            }
        }

        final JSONArray players = new JSONArray();

        for (int i = 0; i < teamAListIds.size(); i++) {
            try {
                JSONObject player = new JSONObject();
                player.put("id", teamAListIds.get(i));
                player.put("team", "A");
                players.put(player);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        for (int i = 0; i < teamBListIds.size(); i++) {
            try {
                JSONObject player = new JSONObject();
                player.put("id", teamBListIds.get(i));
                player.put("team", "B");
                players.put(player);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        for (int i = 0; i < game.getPendings().size(); i++) {
            try {
                JSONObject player = new JSONObject();
                player.put("id", game.getPendings().get(i).get_id());
                player.put("team", "pending");
                players.put(player);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        Log.d(TAG, players.toString());

        try {

            json.put("id", gameId);
            json.put("players", players);

            Log.d(TAG, "JSON: " + json.toString());

            server.updateGameTeams(json, new ServerHandler.ResponseHandler() {
                @Override
                public void onSuccess(Object response) {
                    Log.d(TAG, response.toString());

                    Toast.makeText(context, "You join Team " + team, Toast.LENGTH_SHORT).show();

                    JSONObject serializedGame = (JSONObject) response;
                    ObjectMapper mapper = new ObjectMapper();
                    try {

                        teamAListIds.clear();
                        teamAList.clear();
                        teamBListIds.clear();
                        teamBList.clear();

                        game = mapper.readValue(serializedGame.toString(), Game.class);
                        game.initPeoples(context, new Game.initHandler() {
                            @Override
                            public void handle() {

                                for (int i = 0; i < game.getTeamA().size(); i++) {

                                    teamAListIds.add(game.getTeamA().get(i).get_id());
                                    teamAList.add(game.getTeamA().get(i));

                                    if (game.getTeamA().get(i).get_id().equals(me.get_id())) {
                                        status = "teamA";
                                        gameButton.setText(R.string.game_menu_team_a);
                                    }
                                }

                                for (int i = 0; i < game.getTeamB().size(); i++) {

                                    teamBListIds.add(game.getTeamB().get(i).get_id());
                                    teamBList.add(game.getTeamB().get(i));

                                    if (game.getTeamB().get(i).get_id().equals(me.get_id())) {
                                        status = "teamB";
                                        gameButton.setText(R.string.game_menu_team_b);
                                    }
                                }

                                updateTeamList();
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

        } catch (JSONException e) {
            e.printStackTrace();
            Toast toast = Toast.makeText(context, R.string.hifive_generic_error, Toast.LENGTH_SHORT);
            toast.show();
        }

    }

    private void exitGame() {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setMessage(R.string.dialog_exit_game_msq)
                .setTitle(R.string.dialog_exit_game_title);

        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        JSONObject json = new JSONObject();

                        final List<String> teamA_List = teamAListIds;
                        final List<String> teamB_list = teamBListIds;

                        teamA_List.remove(myId);
                        teamB_list.remove(myId);

                        try {

                            json.put("_id", gameId);
                            json.put("teamA", new JSONArray(teamA_List));
                            json.put("teamB", new JSONArray(teamB_list));

                            server.putDatas("game", json, new ServerHandler.ResponseHandler() {
                                @Override
                                public void onSuccess(Object response) {
                                    Log.d(TAG, response.toString());

                                    Toast.makeText(context, R.string.act_game_detail_left_msg, Toast.LENGTH_SHORT).show();

                                    gameButton.setText(R.string.game_menu_default);

                                    JSONObject serializedGame = (JSONObject) response;
                                    ObjectMapper mapper = new ObjectMapper();
                                    try {

                                        teamAListIds.clear();
                                        teamAList.clear();
                                        teamBListIds.clear();
                                        teamBList.clear();

                                        game = mapper.readValue(serializedGame.toString(), Game.class);
                                        game.initPeoples(context, new Game.initHandler() {
                                            @Override
                                            public void handle() {

                                                for (int i = 0; i < game.getTeamA().size(); i++) {

                                                    teamAListIds.add(game.getTeamA().get(i).get_id());
                                                    teamAList.add(game.getTeamA().get(i));

                                                    if (game.getTeamA().get(i).get_id().equals(me.get_id())) {
                                                        status = "teamA";
                                                        gameButton.setText(R.string.game_menu_team_a);
                                                    }
                                                }

                                                for (int i = 0; i < game.getTeamB().size(); i++) {

                                                    teamBListIds.add(game.getTeamB().get(i).get_id());
                                                    teamBList.add(game.getTeamB().get(i));

                                                    if (game.getTeamB().get(i).get_id().equals(me.get_id())) {
                                                        status = "teamB";
                                                        gameButton.setText(R.string.game_menu_team_b);
                                                    }
                                                }

                                                updateTeamList();
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

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast toast = Toast.makeText(context, R.string.hifive_generic_error, Toast.LENGTH_SHORT);
                            toast.show();
                        }
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
    public boolean onPrepareOptionsMenu(final Menu menu) {
        super.onPrepareOptionsMenu(menu);

        final TextView OrganizerName = (TextView) findViewById(R.id.act_game_detail_organizer_username);
        final TextView GameDate = (TextView) findViewById(R.id.act_game_detail_date);
        final TextView GameTime = (TextView) findViewById(R.id.act_game_detail_time);
        final TextView GamePlace = (TextView) findViewById(R.id.act_game_detail_place);
        final TextView GamePrice = (TextView) findViewById(R.id.act_game_detail_price);

        final LinearLayout OrganizerInfos = (LinearLayout) findViewById(R.id.act_game_detail_organizer_infos);

        server.getGame(gameId, new ServerHandler.ResponseHandler() {
            @Override
            public void onSuccess(Object response) {
                Log.d(TAG, response.toString());

                JSONObject serializedGame = (JSONObject) response;
                ObjectMapper mapper = new ObjectMapper();
                try {
                    game = mapper.readValue(serializedGame.toString(), Game.class);
                    game.initPeoples(context, new Game.initHandler() {
                        @Override
                        public void handle() {

//                            Collections.addAll(pendingListIds, game.getPlayersIDs("pending"));
                            pendingList = game.getPendings();

                            if (game.getOrganizerID().equals(myId)) {
                                isOrganizer = true;
                                menu.findItem(R.id.action_delete).setVisible(isOrganizer);
                            }

                            OrganizerName.setText(game.getOrganizer().getUsername());
                            GameDate.setText(game.getDate());
                            GameTime.setText(game.getTime());
                            GamePlace.setText(game.getPlace());
                            String price = String.format("%s", game.getPrice());
                            GamePrice.setText(price + "€ / participant");

                            for (int i = 0; i < game.getTeamA().size(); i++) {

                                teamAListIds.add(game.getTeamA().get(i).get_id());
                                teamAList.add(game.getTeamA().get(i));

                                if (game.getTeamA().get(i).get_id().equals(me.get_id())) {
                                    status = "teamA";
                                    gameButton.setText(R.string.game_menu_team_a);
                                }
                            }

                            for (int i = 0; i < game.getTeamB().size(); i++) {

                                teamBListIds.add(game.getTeamB().get(i).get_id());
                                teamBList.add(game.getTeamB().get(i));

                                if (game.getTeamB().get(i).get_id().equals(me.get_id())) {
                                    status = "teamB";
                                    gameButton.setText(R.string.game_menu_team_b);
                                }
                            }

                            updateTeamList();
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
        int id = item.getItemId();

        if (id == R.id.action_game_add_player) {

            String usersIdList = "";
            String usersNameList = "";

            for (int i = 0; i < pendingList.size(); i++) {
                if (i != 0) {
                    usersIdList += ",";
                    usersNameList += ",";
                }
                usersIdList += pendingList.get(i).get_id();
                usersNameList += pendingList.get(i).getUsername();
            }

            Intent intent = new Intent(context, FriendsListActivity.class);
            intent.putExtra("USERS_LIST_ID", usersIdList);
            intent.putExtra("USERS_LIST_NAME", usersNameList);
            intent.putExtra("GAME_ID", gameId);
            startActivityForResult(intent, 0);
            return true;
        }

        if (id == R.id.action_delete) {

            server.deleteGame(gameId, new ServerHandler.ResponseHandler() {
                @Override
                public void onSuccess(Object response) {
                    Toast.makeText(context, "Votre Game a bien été supprimé", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(context, MainActivity.class);
                    startActivity(intent);
                    finish();
                }

                @Override
                public void onError(String error) {
                    Toast toast = Toast.makeText(context, R.string.hifive_generic_error, Toast.LENGTH_SHORT);
                }
            });

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.e(TAG, String.valueOf(requestCode));
        Log.e(TAG, String.valueOf(resultCode));

        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {

                pendingListIds = new ArrayList<>();
                pendingListNames = new ArrayList<>();

                String pending_ids = data.getStringExtra("USERS_LIST_ID");
                String pending_names = data.getStringExtra("USERS_LIST_NAME");

                String[] friendsIdList = pending_ids.split(",");
                String[] friendsNameList = pending_names.split(",");

                Collections.addAll(pendingListIds, friendsIdList);
                Collections.addAll(pendingListNames, friendsNameList);

                // TODO Update pending list game

            }
        }
    }

}
