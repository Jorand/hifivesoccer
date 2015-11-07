package com.hifivesoccer.models;

import android.content.Context;
import android.util.Log;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
import java.util.Locale;

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
    private boolean privacy;
    private int requestQueue = 0;

    @JsonIgnore
    private User organizer;
    @JsonProperty("organizer")
    private String organizerID;

    @JsonIgnore
    private ArrayList<User> teamA = new ArrayList<User>();
    @JsonProperty("teamA")
    private String[] teamAIDs;

    @JsonIgnore
    private ArrayList<User> teamB = new ArrayList<User>();
    @JsonProperty("teamB")
    private String[] teamBIDs;

    @JsonIgnore
    private ArrayList<User> pending = new ArrayList<User>();
    @JsonProperty("pending")
    private String[] pendingIDs;

    public void initPeoples(Context context, final initHandler callback){

        requestQueue = 0;

        if(organizerID != null){
            requestQueue++;
            getUserAndAdToList(getOrganizerID(), context, new addToList() {
                @Override
                public void handle(Object response) {
                    setOrganizer((User) response);
                    requestQueue--;
                    checkIfAsyncDone(callback);
                }
            });
        }
        if(pendingIDs.length > 0){
            String ids = arrayToJavaScriptArray(pendingIDs);
            requestQueue+= pendingIDs.length;
            getArrayOfUsersAndAdToList(ids, context, new addToList() {
                @Override
                public void handle(Object response) {
                    requestQueue--;
                    pending.add((User) response);
                    checkIfAsyncDone(callback);
                }
            });
        }
        if(teamAIDs.length > 0){
            String ids = arrayToJavaScriptArray(teamAIDs);
            requestQueue+= teamAIDs.length;
            getArrayOfUsersAndAdToList(ids, context, new addToList() {
                @Override
                public void handle(Object response) {
                    requestQueue--;
                    teamA.add((User) response);
                    checkIfAsyncDone(callback);
                }
            });
        }
        if(teamBIDs.length > 0){
            String ids = arrayToJavaScriptArray(teamBIDs);
            requestQueue+= teamBIDs.length;
            getArrayOfUsersAndAdToList(ids, context, new addToList() {
                @Override
                public void handle(Object response) {
                    requestQueue--;
                    teamB.add((User) response);
                    checkIfAsyncDone(callback);
                }
            });
        }
    }

    private String arrayToJavaScriptArray (String[] array){
        String ids = "[";
        for (String id : array){
            ids += id + ",";
        }
        ids += "]";
        return ids;
    }

    void checkIfAsyncDone (initHandler callback){
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
                    Log.d(TAG, user.toString());
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

    public String[] getTeamAIDs() {
        return teamAIDs;
    }

    public void setTeamAIDs(String[] teamAIDs) {
        this.teamAIDs = teamAIDs;
    }

    public String[] getTeamBIDs() {
        return teamBIDs;
    }

    public void setTeamBIDs(String[] teamBIDs) {
        this.teamBIDs = teamBIDs;
    }

    public String[] getPendingIDs() {
        return pendingIDs;
    }

    public void setPendingIDs(String[] pendingIDs) {
        this.pendingIDs = pendingIDs;
    }

    public User getOrganizer() {
        return organizer;
    }

    public void setOrganizer(User organizer) {
        this.organizer = organizer;
    }

    public ArrayList<User> getTeamA() {
        return teamA;
    }

    public void setTeamA(ArrayList<User> teamA) {
        this.teamA = teamA;
    }

    public void addTeamA(User user){
        this.teamA.add(user);
    }

    public ArrayList<User> getTeamB() {
        return teamB;
    }

    public void setTeamB(ArrayList<User> teamB) {
        this.teamB = teamB;
    }

    public void addTeamB(User user){
        this.teamB.add(user);
    }

    public ArrayList<User> getPending() {
        return pending;
    }

    public void setPending(ArrayList<User> pending) {
        this.pending = pending;
    }

    public void addPending(User user){
        this.pending.add(user);
    }
}
