package com.hifivesoccer.utils;

import android.app.Activity;

import com.hifivesoccer.models.User;

/**
 * Created by hugohil on 03/11/15.
 */
public class MySelf {
    private MySelf(){}
    private static User self = null;

    public static User getSelf (){
        return self;
    }

    public static synchronized User setSelf(Activity activity, User self) {
        MySelf.self = self;
        SharedPref.setMyself(activity, self);
        return self;
    }
}
