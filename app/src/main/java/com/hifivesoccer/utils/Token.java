package com.hifivesoccer.utils;

import android.app.Activity;
import android.content.Context;

import com.hifivesoccer.activities.AppActivity;

/**
 * Created by hugohil on 02/11/15.
 */
public class Token {
    private Token(){}
    private static String TOKEN;

    public static String getToken(){
        return TOKEN;
    }

    public static synchronized String getToken(Activity activity, String token){
        if(TOKEN == null){
            TOKEN = token;
            SharedPref.setToken(activity, TOKEN);
        }
        return TOKEN;
    }

    public static synchronized void deleteToken(Activity activity){
        TOKEN = null;
        SharedPref.deleteToken(activity);
    }
}
