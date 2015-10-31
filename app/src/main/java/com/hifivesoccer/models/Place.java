package com.hifivesoccer.models;

/**
 * Created by hugohil on 31/10/15.
 */
public class Place extends AppBaseModel {
    private String name;
    private String description;
    private String url;
    private Location location;
    private int fields;
    private User contact;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public int getFields() {
        return fields;
    }

    public void setFields(int fields) {
        this.fields = fields;
    }

    public User getContact() {
        return contact;
    }

    public void setContact(User contact) {
        this.contact = contact;
    }

    class Location {
        private String address;
        private long lat;
        private long lon;

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public long getLat() {
            return lat;
        }

        public void setLat(long lat) {
            this.lat = lat;
        }

        public long getLon() {
            return lon;
        }

        public void setLon(long lon) {
            this.lon = lon;
        }
    }
}
