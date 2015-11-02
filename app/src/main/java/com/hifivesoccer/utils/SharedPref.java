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

    public static void setProfile(Activity activity, String profile){
        SharedPreferences sharedPref = activity.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(activity.getResources().getString(R.string.hifive_profile_key), profile);
        editor.apply();
    }

    public static String getProfile(Activity activity){
        SharedPreferences sharedPref = activity.getPreferences(Context.MODE_PRIVATE);
        return sharedPref.getString(activity.getResources().getString(R.string.hifive_profile_key), "{}");
    }
}
