package com.hifivesoccer.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hifivesoccer.R;
import com.hifivesoccer.models.User;

import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by hugohil on 02/11/15.
 */
public class SharedPref {
    private final static String TAG = SharedPref.class.getSimpleName();

    public static void setMyself(Activity activity, User self){
        ObjectMapper mapper = new ObjectMapper();
        String jsonSelf = null;
        try {
            jsonSelf = mapper.writeValueAsString(self);
            SharedPreferences sharedPref = activity.getSharedPreferences(activity.getResources().getString(R.string.hifive_pref_key), 0);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString(activity.getResources().getString(R.string.hifive_myself_key), jsonSelf);
            editor.apply();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    public static void setMyself(Activity activity, String jsonSelf){
        SharedPreferences sharedPref = activity.getSharedPreferences(activity.getResources().getString(R.string.hifive_pref_key), 0);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(activity.getResources().getString(R.string.hifive_myself_key), jsonSelf);
        editor.apply();
    }

    public static User getMyself(Activity activity){
        SharedPreferences sharedPref = activity.getSharedPreferences(activity.getResources().getString(R.string.hifive_pref_key), 0);
        String jsonSelf = sharedPref.getString(activity.getResources().getString(R.string.hifive_myself_key), "");
        User self = null;
        if(jsonSelf != null){
            if(!jsonSelf.equals("")){
                ObjectMapper mapper = new ObjectMapper();
                try {
                    self = mapper.readValue(jsonSelf, User.class);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return self;
    }

    public static void deleteMyself(Activity activity){
        SharedPreferences sharedPref = activity.getSharedPreferences(activity.getResources().getString(R.string.hifive_pref_key), 0);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.remove(activity.getResources().getString(R.string.hifive_myself_key));
        editor.apply();
    }

    public static void setToken(Activity activity, String token){
        SharedPreferences sharedPref = activity.getSharedPreferences(activity.getResources().getString(R.string.hifive_pref_key), 0);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(activity.getResources().getString(R.string.hifive_token_key), token);
        editor.apply();
    }

    public static String getToken(Activity activity){
        SharedPreferences sharedPref = activity.getSharedPreferences(activity.getResources().getString(R.string.hifive_pref_key), 0);
        return sharedPref.getString(activity.getResources().getString(R.string.hifive_token_key), "");
    }

    public static void deleteToken(Activity activity){
        SharedPreferences sharedPref = activity.getSharedPreferences(activity.getResources().getString(R.string.hifive_pref_key), 0);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.remove(activity.getResources().getString(R.string.hifive_token_key));
        editor.apply();
    }
}
