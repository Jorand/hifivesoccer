package com.hifivesoccer.models;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Created by hugohil on 31/10/15.
 */
public class Game extends AppBaseModel {

    private Infos infos = new Infos();
    private SimpleDateFormat date;
    private Place place;
    private float price;
    private Peoples peoples;
    private boolean privacy;

    public Infos getInfos() {
        return infos;
    }

    public void setInfos(Infos infos) {
        this.infos = infos;
    }

    public SimpleDateFormat getDate() {
        return date;
    }
    
    public void setDate(String date) {
        this.date = new SimpleDateFormat(date, Locale.FRENCH);
    }

    public Place getPlace() {
        return place;
    }

    public void setPlace(Place place) {
        this.place = place;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public Peoples getPeoples() {
        return peoples;
    }

    public void setPeoples(Peoples peoples) {
        this.peoples = peoples;
    }

    public boolean isPrivacy() {
        return privacy;
    }

    public void setPrivacy(boolean privacy) {
        this.privacy = privacy;
    }

    public class Infos {
        private String title;
        private String description;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }

    public class Peoples {
        private User organizer;
        private User[] pending;
        private Attending attending;
        private User[] declined;

        public User getOrganizer() {
            return organizer;
        }

        public void setOrganizer(User organizer) {
            this.organizer = organizer;
        }

        public User[] getPending() {
            return pending;
        }

        public void setPending(User[] pending) {
            this.pending = pending;
        }

        public Attending getAttending() {
            return attending;
        }

        public void setAttending(Attending attending) {
            this.attending = attending;
        }

        public User[] getDeclined() {
            return declined;
        }

        public void setDeclined(User[] declined) {
            this.declined = declined;
        }

        class Attending {
            private Team teamA;
            private Team teamB;

            public Team getTeamA() {
                return teamA;
            }

            public void setTeamA(Team teamA) {
                this.teamA = teamA;
            }

            public Team getTeamB() {
                return teamB;
            }

            public void setTeamB(Team teamB) {
                this.teamB = teamB;
            }
        }
    }
}
