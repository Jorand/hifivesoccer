package com.hifivesoccer.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Created by hugohil on 31/10/15.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class AppBaseModel {
    private String _id;

    @JsonIgnore
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

    public void setCreatedAt(String createdAt) {
        this.createdAt = new SimpleDateFormat(createdAt, Locale.FRENCH);
    }
}
