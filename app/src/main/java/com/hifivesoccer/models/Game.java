package com.hifivesoccer.models;

import android.content.Context;
import android.util.Log;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hifivesoccer.utils.ServerHandler;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**
 * Created by hugohil on 31/10/15.
 */
public class Game extends AppBaseModel {

    private static final String TAG = Game.class.getSimpleName();
    private String description;
    private String date;
    private String time;
    private String place;
    private float price;
    private boolean done;
    private String winner;
    @JsonProperty("private")
    private boolean privacy;

    private int requestQueue = 0;

    @JsonIgnore
    private User organizer;
    @JsonProperty("organizer")
    private String organizerID;

    @JsonIgnore
    private ArrayList<User> players = new ArrayList<User>();
    @JsonProperty("players")
    private List<Player> playersIDs;

    public void initPeoples(Context context, final initHandler callback){

        //Log.d(TAG, "initPeoples");

        requestQueue = 0;

        if(organizerID != null){
            requestQueue++;
            getUserAndAdToList(getOrganizerID(), context, new addToList() {
                @Override
                public void handle(Object response) {
                    setOrganizer((User) response);
                    //Log.d(TAG, response.toString());
                    requestQueue--;
                    checkIfAsyncDone(callback);
                }
            });
        }
        if(playersIDs != null){
            if(playersIDs.size() > 0){
                String ids = getPlayersIDsFormatted();
                requestQueue+= playersIDs.size();
                getArrayOfUsersAndAdToList(ids, context, new addToList() {
                    @Override
                    public void handle(Object response) {
                        players.add((User) response);
                        //Log.d(TAG, response.toString());
                        requestQueue--;
                        checkIfAsyncDone(callback);
                    }
                });
            }
        }
    }

    private String getPlayersIDsFormatted(){
        String result = "[";
        for (Player p : playersIDs){
            result += p.getId() + ",";
        }
        result += "]";
        return result;
    }

    void checkIfAsyncDone (initHandler callback){
        //Log.d(TAG, "requestQueue: " + requestQueue);
        if(requestQueue < 1){
            callback.handle();
        }
    }

    private void getUserAndAdToList(String id, Context context, final addToList handler){
        ServerHandler server = ServerHandler.getInstance(context);
        server.getUser(id, new ServerHandler.ResponseHandler() {
            @Override
            public void onSuccess(Object response) {
                ObjectMapper mapper = new ObjectMapper();
                try {
                    User user = mapper.readValue(response.toString(), User.class);
                    handler.handle(user);
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

    private void getArrayOfUsersAndAdToList(String id, Context context, final addToList handler) {
        ServerHandler server = ServerHandler.getInstance(context);
        server.getArrayOfUsers(id, new ServerHandler.ResponseHandler() {
            @Override
            public void onSuccess(Object response) {

                JSONArray serializedUsers = (JSONArray) response;
                for (int i = 0; i < serializedUsers.length(); i++) {
                    ObjectMapper mapper = new ObjectMapper();
                    try {
                        User user = null;
                        try {
                            user = mapper.readValue(serializedUsers.getJSONObject(i).toString(), User.class);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        handler.handle(user);
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

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public List<Player> getPlayersIDs() {
        return playersIDs;
    }

    public ArrayList<String> getPlayersIDs(String filter) {
        ArrayList<String> result = new ArrayList<String>();
        if(playersIDs.size() > 0){
            for (Player p : playersIDs) {
                if (p.getTeam().equals(filter)) {
                    result.add(p.getId());
                }
            }
        }
        return result;
    }

    public void setPlayersIDs(List<Player> playersIDs) {
        this.playersIDs = playersIDs;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    public String getWinner() {
        return winner;
    }

    public void setWinner(String winner) {
        this.winner = winner;
    }

    public interface addToList {
        void handle(Object response);
    }

    public interface initHandler {
        void handle();
    }

    public String getDate() {
        return date;
    }
    
    public void setDate(String date) {
        this.date = date;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public boolean isPrivacy() {
        return privacy;
    }

    public void setPrivacy(boolean privacy) {
        this.privacy = privacy;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getOrganizerID() {
        return organizerID;
    }

    public void setOrganizerID(String organizerID) {
        this.organizerID = organizerID;
    }


    public User getOrganizer() {
        return organizer;
    }

    public void setOrganizer(User organizer) {
        this.organizer = organizer;
    }

    public ArrayList<User> getPlayers (){
        return players;
    }

    public void addPlayer(User player){
        players.add(player);
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    static class Player {
        private String id;
        private String team;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getTeam() {
            return team;
        }

        public void setTeam(String team) {
            this.team = team;
        }
    }

    public ArrayList<User> getTeamA(){
        ArrayList<User> result = new ArrayList<User>();
        for (Player p : playersIDs){
            if(p.getTeam().equals("A")){
                for (User u : players){
                    if(u.get_id().equals(p.getId())){
                        result.add(u);
                    }
                }
            }
        }
        return result;
    }
    public ArrayList<User> getTeamB(){
        ArrayList<User> result = new ArrayList<User>();
        for (Player p : playersIDs){
            if(p.getTeam().equals("B")){
                for (User u : players){
                    if(u.get_id().equals(p.getId())){
                        result.add(u);
                    }
                }
            }
        }
        return result;
    }
    public ArrayList<User> getPendings(){
        ArrayList<User> result = new ArrayList<User>();
        for (Player p : playersIDs){
            if(p.getTeam().equals("pending")){
                for (User u : players){
                    if(u.get_id().equals(p.getId())){
                        result.add(u);
                    }
                }
            }
        }
        return result;
    }
}
