package com.hifivesoccer.utils;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.apache.http.client.ResponseHandler;
import org.json.JSONArray;

/**
 * Created by hugohil on 31/10/15.
 */
public class ServerHandler {
    private final String TAG = ServerHandler.class.getSimpleName();

    private Context context;
    private static ServerHandler instance;
    private final String API_BASE_URL = "http://10.0.2.2:8080/api/"; // 10.0.2.2 is the emulator alias for your machine's localhost

    private ServerHandler(Context context){
        this.context = context;
    }

    public static synchronized ServerHandler getInstance(Context context) {
        if(instance == null){
            instance = new ServerHandler(context);
        }
        return instance;
    }

    public void getAllChats(final ResponseHandler handler){
        this.getDatas("chats", handler);
    }

    public void getAllGames(final ResponseHandler handler){
        this.getDatas("chats", handler);
    }

    public void getAllMessages(final ResponseHandler handler){
        this.getDatas("messages", handler);
    }

    public void getAllPlaces(final ResponseHandler handler){
        this.getDatas("places", handler);
    }

    public void getAllTeams(final ResponseHandler handler){
        this.getDatas("teams", handler);
    }

    public void getAllUsers(final ResponseHandler handler){
        this.getDatas("users", handler);
    }

    private void getDatas(String route, final ResponseHandler handler){
        String url = API_BASE_URL + route;
        RequestQueue requestQueue = Volley.newRequestQueue(this.context);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response){
                        handler.onSuccess(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error){
                        Log.d(TAG, error.toString());
                        handler.onError(error.toString());
                    }
                }
        );
        requestQueue.add(jsonArrayRequest);
    }

    public interface ResponseHandler {
        void onSuccess(Object datas);
        void onError(String error);
    }
}
