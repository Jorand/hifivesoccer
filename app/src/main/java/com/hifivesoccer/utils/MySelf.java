package com.hifivesoccer.utils;

import com.hifivesoccer.models.User;

/**
 * Created by hugohil on 03/11/15.
 */
public class MySelf {
    private MySelf(){}
    private static User self;

    public static User getSelf() {
        return self;
    }

    public static synchronized User getSelf(User self) {
        if(MySelf.self == null){
            MySelf.self = self;
        }
        return self;
    }
}
