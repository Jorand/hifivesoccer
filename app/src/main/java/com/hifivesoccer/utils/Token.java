package com.hifivesoccer.utils;

/**
 * Created by hugohil on 02/11/15.
 */
public class Token {
    private Token(){}
    private static String TOKEN;

    public static String getToken(){
        return TOKEN;
    }

    public static synchronized String getToken(String token){
        if(TOKEN == null){
            TOKEN = token;
        }
        return TOKEN;
    }

    public static synchronized void deleteToken(){
        TOKEN = null;
    }
}
