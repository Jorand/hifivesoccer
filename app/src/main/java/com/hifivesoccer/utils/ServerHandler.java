package com.hifivesoccer.utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hifivesoccer.R;
import com.hifivesoccer.activities.LoginActivity;
import com.hifivesoccer.activities.MainActivity;
import com.hifivesoccer.models.User;

import org.apache.http.client.ResponseHandler;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;

/**
 * Created by hugohil on 31/10/15.
 */
public class ServerHandler {
    private final String TAG = ServerHandler.class.getSimpleName();

    private Context context;
    private static ServerHandler instance;
    private final String API_BASE_URL = "http://192.168.56.1:8080/api/"; // See http://bbowden.tumblr.com/post/58650831283/accessing-a-localhost-server-from-the-genymotion

    private ServerHandler(Context context){
        this.context = context;
    }

    public static synchronized ServerHandler getInstance(Context context) {
        if(instance == null){
            instance = new ServerHandler(context);
        }
        return instance;
    }

    public void authenticateToError(JSONObject json, Activity activity){
        this.postDatas("authenticate", json, this.authenticateHandlerToError(activity));
    }

    public void authenticateToLogin(JSONObject json, Activity activity){
        this.postDatas("authenticate", json, this.authenticateHandlerToLogin(activity));
    }

    public void getAllGames(final ResponseHandler handler){
        this.getDatas("game", handler);
    }

    public void getGame(String id, final ResponseHandler handler){
        this.getData("game", id, handler);
    }

    public void getAllUsers(final ResponseHandler handler){
        this.getDatas("user", handler);
    }

    public void getUser(String id, final ResponseHandler handler){
        this.getData("user", id, handler);
    }

    public void getArrayOfUsers(String ids, final ResponseHandler handler){
        this.getArrayOfDatas("user", ids, handler);
    }

    private void getData(String route, String id, final ResponseHandler handler){
        String url = API_BASE_URL + route + '/' + id;
        url += "?token=" + Token.getToken();
        this.performRequest(url, Request.Method.GET, new JSONObject(), handler);
    }

    private void getArrayOfDatas(String route, String ids, final ResponseHandler handler){
        String url = API_BASE_URL + route + '/' + ids;
        url += "?token=" + Token.getToken();
        this.performArrayRequest(url, Request.Method.GET, new JSONObject(), handler);
    }

    private void getDatas(String route, final ResponseHandler handler){
        String url = API_BASE_URL + route;
        url += "?token=" + Token.getToken();
        this.performArrayRequest(url, Request.Method.GET, new JSONObject(), handler);
    }

    public void postDatas(String route, JSONObject json, final ResponseHandler handler){
        String url = API_BASE_URL + route;
        url += "?token=" + Token.getToken();
        this.performRequest(url, Request.Method.POST, json, handler);
    }

    public void putDatas(String route, JSONObject json, final ResponseHandler handler){
        String url = API_BASE_URL + route;
        url += "?token=" + Token.getToken();
        this.performRequest(url, Request.Method.PUT, json, handler);
    }

    private void performRequest (String url, int verb, JSONObject json, final ResponseHandler handler){
        RequestQueue requestQueue = Volley.newRequestQueue(this.context);
        JsonObjectRequest jsonRequest = new JsonObjectRequest(
                verb, url, json,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response){
                        handler.onSuccess(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error){
                        Log.e(TAG, error.toString());
                        handler.onError(error.toString());
                    }
                }
        );
        requestQueue.add(jsonRequest);
    }

    private void performArrayRequest (String url, int verb, JSONObject json, final ResponseHandler handler){
        RequestQueue requestQueue = Volley.newRequestQueue(this.context);
        JsonArrayRequest jsonRequest = new JsonArrayRequest(
                verb, url, json,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response){
                        handler.onSuccess(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error){
                        Log.e(TAG, error.toString());
                        handler.onError(error.toString());
                    }
                }
        );
        requestQueue.add(jsonRequest);
    }

    public interface ResponseHandler {
        void onSuccess(Object response);
        void onError(String error);
    }

    private ResponseHandler authenticateHandlerToError (final Activity activity){

        final ProgressDialog progress = new ProgressDialog(activity);
        progress.setMessage(activity.getString(R.string.connexion));
        progress.show();

        return new ResponseHandler() {
            @Override
            public void onSuccess(Object res) {
                Log.d(TAG, res.toString());
                progress.dismiss();
                JSONObject response = (JSONObject) res;
                try {
                    Token.getToken(activity, response.getString("token"));
                    try {
                        SharedPref.setMyself(activity, response.getString("user"));

                        User myself = new User();
                        ObjectMapper mapper = new ObjectMapper();
                        String serializedSelf = SharedPref.getMyself((Activity) context);
                        Log.d(TAG, serializedSelf);
                        if(serializedSelf.length() > 0){
                            try{
                                myself = mapper.readValue(serializedSelf, User.class);
                                myself.initGames(context);
                                MySelf.setSelf(myself);
                                Log.d(TAG, myself.toString());
                            } catch (IOException e){
                                Log.e(TAG, e.toString());

                                Intent intent = new Intent(activity, LoginActivity.class);
                                activity.startActivity(intent);
                                activity.finish();
                            }
                        } else {
                            Intent intent = new Intent(activity, LoginActivity.class);
                            activity.startActivity(intent);
                            activity.finish();
                        }

                        Intent intent = new Intent(context, MainActivity.class);
                        activity.startActivity(intent);
                        activity.finish();

                    } catch (JSONException e){
                        Log.e(TAG, e.toString());
                    }
                } catch (JSONException e){
                    Log.e(TAG, e.toString());
                }
            }

            @Override
            public void onError(String error) {
                Log.d(TAG, error);
                progress.dismiss();
                JSONObject jsonError = new JSONObject();
                try {
                    jsonError = new JSONObject(error);
                } catch (JSONException e){
                    Log.e(TAG, e.toString());
                }
                Log.d(TAG, jsonError.toString());
                if(jsonError.length() > 0){
                    String reason;
                    try {
                        reason = jsonError.getString("reason");
                    } catch (JSONException e){
                        Log.e(TAG, e.toString());
                        reason = activity.getResources().getString(R.string.act_login_error);
                    }
                    Toast toast = Toast.makeText(activity, reason, Toast.LENGTH_LONG);
                    //Toast toast = Toast.makeText(activity, R.string.act_login_error, Toast.LENGTH_LONG);
                    toast.show();
                } else {
                    Toast toast = Toast.makeText(activity, R.string.act_login_error, Toast.LENGTH_LONG);
                    toast.show();
                }
            }
        };
    }

    private ResponseHandler authenticateHandlerToLogin (final Activity activity){
        return new ResponseHandler() {
            @Override
            public void onSuccess(Object res) {
                Log.d(TAG, res.toString());
                JSONObject response = (JSONObject) res;
                try {
                    Token.getToken(activity, response.getString("token"));
                    try {
                        SharedPref.setMyself(activity, response.getString("user"));

                        Intent intent = new Intent(context, MainActivity.class);
                        activity.startActivity(intent);
                        activity.finish();

                    } catch (JSONException e){
                        Log.e(TAG, e.toString());
                        Intent intent = new Intent(context, LoginActivity.class);
                        activity.startActivity(intent);
                        activity.finish();
                    }
                } catch (JSONException e){
                    Log.e(TAG, e.toString());
                    Intent intent = new Intent(context, LoginActivity.class);
                    activity.startActivity(intent);
                    activity.finish();
                }
            }

            @Override
            public void onError(String error) {
                Log.d(TAG, error);
                Intent intent = new Intent(context, LoginActivity.class);
                activity.startActivity(intent);
                activity.finish();
            }
        };
    }
}
