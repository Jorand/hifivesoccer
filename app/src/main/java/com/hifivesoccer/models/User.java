package com.hifivesoccer.models;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hifivesoccer.utils.ServerHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by hugohil on 31/10/15.
 */
public class User extends AppBaseModel {
    private static final String TAG = User.class.getSimpleName();

    private String email;
    private String fname;
    private String lname;
    private String username;
    private String password;
    private String picture;
    private String position;
    private int played;
    private int goals;
    private int wins;
    private int notificationsLevel;
    private float maxDistance;
    private boolean isPrivate;
    private int requestQueue = 0;

    @JsonIgnore
    private ArrayList<Game> games = new ArrayList<Game>();
    @JsonProperty("games")
    private String[] gamesIDs;

    @JsonIgnore
    private ArrayList<Game> pendings = new ArrayList<Game>();
    @JsonProperty("pending")
    private String[] pendingsIDs;

    public void initGames(Context context,  final initHandler callback) {
        if (gamesIDs != null) {
            if (gamesIDs.length > 0) {
                String ids = getGamesIDsFormatted();
                requestQueue += gamesIDs.length;
                getArrayOfGamesAndAdToList(ids, context, new addToList() {
                    @Override
                    public void handle(Object response) {
                        games.add((Game) response);
                        requestQueue--;
                        checkIfAsyncDone(callback);
                    }
                });
            }
        }
    }

    private String getGamesIDsFormatted(){
        String result = "[";
        for (String id : gamesIDs){
            result += id + ",";
        }
        result += "]";
        return result;
    }

    void checkIfAsyncDone (initHandler callback){
        if(requestQueue < 1){
            callback.handle();
        }
    }

    private void getArrayOfGamesAndAdToList(String id, Context context, final addToList handler) {
        ServerHandler server = ServerHandler.getInstance(context);
        server.getArrayOfGames(id, new ServerHandler.ResponseHandler() {
            @Override
            public void onSuccess(Object response) {
                JSONArray serializedGames = (JSONArray) response;
                for (int i = 0; i < serializedGames.length(); i++) {
                    ObjectMapper mapper = new ObjectMapper();
                    try {
                        Game game = null;
                        try {
                            game = mapper.readValue(serializedGames.getJSONObject(i).toString(), Game.class);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        handler.handle(game);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onError(String error) {
                Log.e(TAG, error);
            }
        });
    }

    public interface addToList {
        void handle(Object response);
    }

    public interface initHandler {
        void handle();
    }

    public boolean isPrivate() {
        return isPrivate;
    }

    public void setIsPrivate(boolean isPrivate) {
        this.isPrivate = isPrivate;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String[] getGamesIDs(){
        return gamesIDs;
    }

    public void setGamesIDs(String[] gamesIDs) {
        this.gamesIDs = gamesIDs;
    }

    public ArrayList<Game> getGames(){
        return games;
    }

    public void setGames(ArrayList<Game> games){
        this.games = games;
    }

    public void addGame(Game game){
        this.games.add(game);
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public String getLname() {
        return lname;
    }

    public void setLname(String lname) {
        this.lname = lname;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getPlayed() {
        return played;
    }

    public void setPlayed(int played) {
        this.played = played;
    }

    public int getGoals() {
        return goals;
    }

    public void setGoals(int goals) {
        this.goals = goals;
    }

    public int getWins() {
        return wins;
    }

    public void setWins(int wins) {
        this.wins = wins;
    }

    public int getNotificationsLevel() {
        return notificationsLevel;
    }

    public void setNotificationsLevel(int notificationsLevel) {
        this.notificationsLevel = notificationsLevel;
    }

    public float getMaxDistance() {
        return maxDistance;
    }

    public void setMaxDistance(float maxDistance) {
        this.maxDistance = maxDistance;
    }

    public ArrayList<Game> getPendings() {
        return pendings;
    }

    public void setPendings(ArrayList<Game> pendings) {
        this.pendings = pendings;
    }

    public String[] getPendingsIDs() {
        return pendingsIDs;
    }

    public void setPendingsIDs(String[] pendingsIDs) {
        this.pendingsIDs = pendingsIDs;
    }
}
