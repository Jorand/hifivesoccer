package com.hifivesoccer.models;

import android.content.Context;
import android.util.Log;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hifivesoccer.utils.ServerHandler;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by hugohil on 31/10/15.
 */
public class Game extends AppBaseModel {

    private static final String TAG = Game.class.getSimpleName();
    private String description;
    private String date;
    private String place;
    private float price;
    private boolean privacy;
    private int requestQueue = 0;

    @JsonIgnore
    private User organizer;
    @JsonProperty("organizer")
    private String organizerID;

    @JsonIgnore
    private ArrayList<User> teamA = new ArrayList<>();
    @JsonProperty("teamA")
    private String[] teamAIDs;

    @JsonIgnore
    private ArrayList<User> teamB = new ArrayList<>();
    @JsonProperty("teamB")
    private String[] teamBIDs;

    @JsonIgnore
    private ArrayList<User> pending;
    @JsonProperty("pending")
    private String[] pendingIDs;

    public void initPeoples(Context context, final initHandler callback){
        requestQueue++;
        getUserAndAdToList(getOrganizerID(), context, new addToList() {
            @Override
            public void handle(Object response) {
                setOrganizer((User) response);
                requestQueue--;
                checkIfAsyncDone(callback);
            }
        });
        if(pendingIDs != null){
            for (String id : pendingIDs){
                requestQueue++;
                getUserAndAdToList(id, context, new addToList() {
                    @Override
                    public void handle(Object response) {
                        pending.add((User) response);
                        requestQueue--;
                        checkIfAsyncDone(callback);
                    }
                });
            }
        }
        if(teamAIDs != null){
            for (String id : teamAIDs){
                requestQueue++;
                getUserAndAdToList(id, context, new addToList() {
                    @Override
                    public void handle(Object response) {
                        teamA.add((User) response);
                        requestQueue--;
                        checkIfAsyncDone(callback);
                    }
                });
            }
        }
        if(teamBIDs != null){
            for (String id : teamBIDs){
                requestQueue++;
                getUserAndAdToList(id, context, new addToList() {
                    @Override
                    public void handle(Object response){
                        teamB.add((User) response);
                        requestQueue--;
                        checkIfAsyncDone(callback);
                    }
                });
            }
        }
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
