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
}
