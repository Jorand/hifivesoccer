package com.hifivesoccer.models;

import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Created by hugohil on 31/10/15.
 */
public class AppBaseModel {
    private String _id;
    private SimpleDateFormat createdAt;

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public SimpleDateFormat getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(SimpleDateFormat createdAt) {
        this.createdAt = createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = new SimpleDateFormat(createdAt, Locale.FRENCH);
    }
}
