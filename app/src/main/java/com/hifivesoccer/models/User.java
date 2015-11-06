package com.hifivesoccer.models;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hifivesoccer.utils.ServerHandler;

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
    private Profile profile;
    private Statistics statistics;
    private String position;
    private boolean isPrivate;
    private Settings settings;

    @JsonIgnore
    private ArrayList<Game> games;
    @JsonProperty("games")
    private String[] gamesIDs;

    public void initGames(Context context){
        // Add a front-end cache
        ServerHandler server = ServerHandler.getInstance(context);
        if(gamesIDs == null){
            return;
        }
        for (String id : gamesIDs){
            server.getGame(id, new ServerHandler.ResponseHandler() {
                @Override
                public void onSuccess(Object response) {
                    ObjectMapper mapper = new ObjectMapper();
                    try {
                        addGame(mapper.readValue(response.toString(), Game.class));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onError(String error) {
                    Log.e(TAG, error);
                }
            });
        }
    }

    public Profile getProfile() {
        return profile;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }

    public Statistics getStatistics() {
        return statistics;
    }

    public void setStatistics(Statistics statistics) {
        this.statistics = statistics;
    }

    public boolean isPrivate() {
        return isPrivate;
    }

    public void setIsPrivate(boolean isPrivate) {
        this.isPrivate = isPrivate;
    }

    public Settings getSettings() {
        return settings;
    }

    public void setSettings(Settings settings) {
        this.settings = settings;
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

    public class Profile {
        private String email;
        private String fname;
        private String lname;
        private String username;
        private String password;
        private String picture;

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
    }

    public class Statistics {
        private int games;
        private int goals;
        private int wins;

        public int getGames() {
            return games;
        }

        public void setGames(int games) {
            this.games = games;
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
    }

    public class Settings {
        private int notificationsLevel;
        private float maxDistance;

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
    }
}
