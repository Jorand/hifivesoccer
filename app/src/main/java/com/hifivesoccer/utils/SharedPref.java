package com.hifivesoccer.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.hifivesoccer.R;

import org.json.JSONObject;

/**
 * Created by hugohil on 02/11/15.
 */
public class SharedPref {
    private final static String TAG = SharedPref.class.getSimpleName();

    public static void setMyself(Activity activity, String profile){
        SharedPreferences sharedPref = activity.getSharedPreferences(activity.getResources().getString(R.string.hifive_pref_key), 0);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(activity.getResources().getString(R.string.hifive_myself_key), profile);
        editor.apply();
    }

    public static String getMyself(Activity activity){
        SharedPreferences sharedPref = activity.getSharedPreferences(activity.getResources().getString(R.string.hifive_pref_key), 0);
        return sharedPref.getString(activity.getResources().getString(R.string.hifive_myself_key), "");
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
