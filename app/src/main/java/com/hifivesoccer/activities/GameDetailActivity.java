package com.hifivesoccer.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hifivesoccer.R;
import com.hifivesoccer.models.Game;
import com.hifivesoccer.models.User;
import com.hifivesoccer.utils.MySelf;
import com.hifivesoccer.utils.ServerHandler;
import com.hifivesoccer.utils.base64ToBitmap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
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

    private LinearLayout teamA;
    private LinearLayout teamB;

    private List<User> teamAList = new ArrayList<>();
    private List<User> teamBList = new ArrayList<>();

    private List<String> teamAListIds = new ArrayList<>();
    private List<String> teamBListIds = new ArrayList<>();

    private String[] myGamesIds;

    private String status;

    private List<String> pendingListIds = new ArrayList<>();
    private Menu myMenu;

    private String organizerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_detail);

        // TOOLBAR
        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        if(MySelf.getSelf() != null){
            me = MySelf.getSelf();
            myId = MySelf.getSelf().get_id();
            myGamesIds = MySelf.getSelf().getGamesIDs();

        }
        gameId = getIntent().getStringExtra("GAME_ID");
        getMatch();

        teamA = (LinearLayout) findViewById(R.id.act_game_detail_team_a_list);
        teamB = (LinearLayout) findViewById(R.id.act_game_detail_team_b_list);
        gameButton = (Button) findViewById(R.id.act_game_detail_button);

        gameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                PopupMenu popup = new PopupMenu(context, gameButton);
                popup.getMenuInflater().inflate(R.menu.game_action_menu, popup.getMenu());

                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {

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

    private void getMatch() {

        final TextView OrganizerName = (TextView) findViewById(R.id.act_game_detail_organizer_username);
        final TextView GameDate = (TextView) findViewById(R.id.act_game_detail_date);
        final TextView GameTime = (TextView) findViewById(R.id.act_game_detail_time);
        final TextView GamePlace = (TextView) findViewById(R.id.act_game_detail_place);
        final TextView GamePrice = (TextView) findViewById(R.id.act_game_detail_price);
        final TextView GameDescrition = (TextView) findViewById(R.id.act_game_detail_description);

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

                            isOrganizer = game.getOrganizerID().equals(myId);
                            organizerId = game.getOrganizerID();
                            onPrepareOptionsMenu(myMenu);

                            OrganizerName.setText(game.getOrganizer().getUsername());
                            GameDate.setText(game.getDate());
                            GameTime.setText(game.getTime());
                            GamePlace.setText(game.getPlace());
                            String price = String.format("%s", game.getPrice());
                            GamePrice.setText(price + getString(R.string.act_game_detail_price_msg));
                            GameDescrition.setText(game.getDescription());

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

                            for (int i = 0; i < game.getPendings().size(); i++) {

                                pendingListIds.add(game.getPendings().get(i).get_id());

                                if (game.getPendings().get(i).get_id().equals(me.get_id())) {
                                    status = "pending";
                                }
                            }

                            updateTeamList();
                            /*
                            if(game.getPlayersIDs().size() > 0){
                                server.notifyPlayers(game);
                            }
                            */
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
    }

    private void updateTeamList() {

        teamA.removeAllViews();
        teamB.removeAllViews();
        teamA.removeAllViewsInLayout();
        teamB.removeAllViewsInLayout();

        if (inflater == null)
            inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        for (int i = 0; i < teamAList.size(); i++) {

            View convertView = inflater.inflate(R.layout.team_list_reverse, null);

            CircleImageView userPicture = (CircleImageView) convertView.findViewById(R.id.teamlist_picture);
            TextView userName = (TextView) convertView.findViewById(R.id.teamlist_username);

            userName.setText(teamAList.get(i).getUsername());

            if (teamAList.get(i).getPicture() != null) {

                Bitmap bm = base64ToBitmap.getBitmap(teamAList.get(i).getPicture());

                userPicture.setImageBitmap(bm);
            }
            convertView.setTag(i);

            if (isOrganizer) {

                convertView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        int position = (Integer) v.getTag();
                        removePlayer(teamAList.get(position).get_id());
                        return true;
                    }
                });
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

                Bitmap bm = base64ToBitmap.getBitmap(teamBList.get(i).getPicture());
                userPicture.setImageBitmap(bm);

            }
            convertView.setTag(i);

            if (isOrganizer) {

                convertView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        int position = (Integer) v.getTag();
                        removePlayer(teamBList.get(position).get_id());
                        return true;
                    }
                });
            }

            teamB.addView(convertView);
        }

    }

    public void joinTeam(final String team) {
        joinTeam(team, null);
    }

    private void joinTeam(final String action, String id) {

        switch (action) {
            case "teamA":
                if (!teamAListIds.contains(myId)){
                    teamAListIds.add(myId);
                } else {
                    return;
                }
                teamBListIds.remove(myId);
                pendingListIds.remove(myId);
                break;

            case "teamB":
                if (!teamBListIds.contains(myId)){
                    teamBListIds.add(myId);
                } else {
                    return;
                }
                teamAListIds.remove(myId);
                pendingListIds.remove(myId);
                break;

            case "exit":
                if (teamBListIds.contains(myId)){
                    teamBListIds.remove(myId);
                }
                if (teamAListIds.contains(myId)){
                    teamAListIds.remove(myId);
                }
                break;

            case "delete":
                if (id != null) {
                    if (teamBListIds.contains(id)) {
                        teamBListIds.remove(myId);
                    }
                    if (teamAListIds.contains(id)) {
                        teamAListIds.remove(myId);
                    }
                }
                break;

            default:

                break;
        }

        updateGameTeams(action, id);

    }

    private void updateGameTeams(final String action, final String user_id) {

        JSONObject json = new JSONObject();

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

        for (int i = 0; i < pendingListIds.size(); i++) {
            try {
                JSONObject player = new JSONObject();
                player.put("id", pendingListIds.get(i));
                player.put("team", "pending");
                players.put(player);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        try {

            json.put("id", gameId);
            json.put("players", players);

            Log.d(TAG, "Update : "+json.toString());

            // Update Game
            server.updateGameTeams(json, new ServerHandler.ResponseHandler() {
                @Override
                public void onSuccess(Object response) {
                    Log.d(TAG, response.toString());

                    JSONObject serializedGame = (JSONObject) response;
                    ObjectMapper mapper = new ObjectMapper();

                    teamAListIds.clear();
                    teamAList.clear();
                    teamBListIds.clear();
                    teamBList.clear();

                    pendingListIds.clear();

                    try {
                        game = mapper.readValue(serializedGame.toString(), Game.class);
                        game.initPeoples(context, new Game.initHandler() {
                            @Override
                            public void handle() {

                                status = "";

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

                                for (int i = 0; i < game.getPendings().size(); i++) {

                                    pendingListIds.add(game.getPendings().get(i).get_id());

                                    if (game.getPendings().get(i).get_id().equals(me.get_id())) {
                                        status = "pending";
                                    }
                                }

                                if (!status.equals("")) {
                                    Log.d(TAG, "addGameToPlayer");
                                    server.addGameToPlayer(MySelf.getSelf().get_id(), gameId);
                                }

                                if (action.equals("exit")) {
                                    status = "exit";
                                    gameButton.setText(R.string.game_menu_default);
                                    server.quitGame(MySelf.getSelf().get_id(), gameId);
                                }

                                if (action.equals("delete")) {
                                    status = "delete";
                                    server.quitGame(user_id, gameId);
                                }

                                if (action.equals("newPending")) {
                                    server.addPendingGameToPlayer(user_id, gameId);
                                }
                                else {
                                    //server.notifyPlayers(game);
                                }

                                /*
                                if (getIntent().getBooleanExtra("NOTIFY", false)) {
                                    Log.d(TAG, "notifying players");
                                    server.notifyPlayers(game);
                                }
                                */
                                //Log.d(TAG, "notifying players");

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

                        joinTeam("exit");
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

    private void removePlayer(final String id_user) {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setMessage(R.string.dialog_game_remove_player);

        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        joinTeam("delete", id_user);
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

        myMenu = menu;

        menu.findItem(R.id.action_delete).setVisible(isOrganizer);
        menu.findItem(R.id.action_done).setVisible(isOrganizer);

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

            for (int i = 0; i < pendingListIds.size(); i++) {
                if (i != 0) {
                    usersIdList += ",";
                }
                usersIdList += pendingListIds.get(i);
            }

            Intent intent = new Intent(context, FriendsListActivity.class);
            intent.putExtra("USERS_LIST_ID", usersIdList);
            intent.putExtra("GAME_ID", gameId);
            startActivityForResult(intent, 0);
            return true;
        }

        if (id == R.id.action_done) {

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
            alertDialogBuilder.setMessage(R.string.game_done_msg);
            alertDialogBuilder.setTitle(R.string.action_done);

            alertDialogBuilder
                    .setCancelable(true)
                    .setPositiveButton(R.string.act_game_detail_team_b, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                            Toast.makeText(context, "équipe b", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton(R.string.act_game_detail_team_a, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Toast.makeText(context, "équipe a", Toast.LENGTH_SHORT).show();
                        }
                    });

            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();

            return true;
        }

        if (id == R.id.action_delete) {

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
            alertDialogBuilder.setMessage(R.string.dialog_game_delete);

            alertDialogBuilder
                    .setCancelable(false)
                    .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                            server.deleteGame(gameId, new ServerHandler.ResponseHandler() {
                                @Override
                                public void onSuccess(Object response) {
                                    Toast.makeText(context, R.string.game_detail_delete, Toast.LENGTH_SHORT).show();

                                    ArrayList<User> players = game.getPlayers();
                                    for (User p : players){
                                        server.quitGame(p.get_id(), gameId);
                                    }

                                    Intent intent = new Intent(context, MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                }

                                @Override
                                public void onError(String error) {

                                    Toast.makeText(context, R.string.hifive_generic_error, Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    })
                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });

            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();

            return false;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.e(TAG, String.valueOf(requestCode));
        Log.e(TAG, String.valueOf(resultCode));

        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                String resultString = data.getStringExtra("USERS_LIST_ID");
                //Log.d(TAG, "resultString "+resultString);
                if (!resultString.equals("")) {

                    String[] pending_ids = resultString.split(",");
                    Log.d(TAG, "pending ids: " + pending_ids.length);

                    if(pending_ids.length > 0){
                        // server.notifyPlayers(game, pending_ids);
                        for (String id : pending_ids){

                            pendingListIds.add(id);
                            joinTeam("newPending", id);
                        }
                    }
                }
            }
        }
    }

    public void openProfilTeamA(View view) {
        int position = (Integer) view.getTag();
        openProfil(teamAList.get(position).get_id());
    }

    public void openProfilTeamB(View view) {
        int position = (Integer) view.getTag();
        openProfil(teamBList.get(position).get_id());
    }

    public void openProfil(String id) {

        if (id != null) {
            Intent intent = new Intent(context, ProfilActivity.class);
            intent.putExtra("USER_ID", id);
            startActivity(intent);
        }
    }

    public void openOrganizerProfil(View view) {
        openProfil(organizerId);
    }
}
