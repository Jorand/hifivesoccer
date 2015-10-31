package com.hifivesoccer.models;

import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Created by hugohil on 31/10/15.
 */
public class User extends AppBaseModel {
    private Profile profile;
    private Infos infos;
    private Statistics statistics;
    private User[] friends;
    private Team[] teams;
    private Game[] games;
    private Chat[] chats;
    private boolean isPrivate;
    private Settings settings;

    public Profile getProfile() {
        return profile;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }

    public Infos getInfos() {
        return infos;
    }

    public void setInfos(Infos infos) {
        this.infos = infos;
    }

    public Statistics getStatistics() {
        return statistics;
    }

    public void setStatistics(Statistics statistics) {
        this.statistics = statistics;
    }

    public User[] getFriends() {
        return friends;
    }

    public void setFriends(User[] friends) {
        this.friends = friends;
    }

    public Team[] getTeams() {
        return teams;
    }

    public void setTeams(Team[] teams) {
        this.teams = teams;
    }

    public Game[] getGames() {
        return games;
    }

    public void setGames(Game[] games) {
        this.games = games;
    }

    public boolean isPrivate() {
        return isPrivate;
    }

    public void setIsPrivate(boolean isPrivate) {
        this.isPrivate = isPrivate;
    }

    public Chat[] getChats() {
        return chats;
    }

    public void setChats(Chat[] chats) {
        this.chats = chats;
    }

    public Settings getSettings() {
        return settings;
    }

    public void setSettings(Settings settings) {
        this.settings = settings;
    }


    class Profile {
        private String email;
        private String fname;
        private String lname;
        private String username;
        private SimpleDateFormat birthdate;
        private String picture;

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getFname() {
            return fname;
        }

        public void setFname(String fname) {
            this.fname = fname;
        }

        public String getLname() {
            return lname;
        }

        public void setLname(String lname) {
            this.lname = lname;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public SimpleDateFormat getBirthdate() {
            return birthdate;
        }

        public void setBirthdate(SimpleDateFormat birthdate) {
            this.birthdate = birthdate;
        }

        public void setBirthdate(String birthdate) {
            this.birthdate = new SimpleDateFormat(birthdate, Locale.FRENCH);
        }

        public String getPicture() {
            return picture;
        }

        public void setPicture(String picture) {
            this.picture = picture;
        }
    }

    class Infos {
        private Favorites favorites;
        private Availability availability;

        public Favorites getFavorites() {
            return favorites;
        }

        public void setFavorites(Favorites favorites) {
            this.favorites = favorites;
        }

        public Availability getAvailability() {
            return availability;
        }

        public void setAvailability(Availability availability) {
            this.availability = availability;
        }

        class Favorites {
            private String position;
            private Place place;

            public String getPosition() {
                return position;
            }

            public void setPosition(String position) {
                this.position = position;
            }

            public Place getPlace() {
                return place;
            }

            public void setPlace(Place place) {
                this.place = place;
            }
        }

        class Availability {
            /*
                This is probably not the best way to instantiate days.
                Suggestions welcome.
             */
            private Day monday;
            private Day tuesday;
            private Day wednesday;
            private Day thursday;
            private Day friday;
            private Day saturday;
            private Day sunday;

            public Day getMonday() {
                return monday;
            }

            public void setMonday(Day monday) {
                this.monday = monday;
            }

            public Day getTuesday() {
                return tuesday;
            }

            public void setTuesday(Day tuesday) {
                this.tuesday = tuesday;
            }

            public Day getWednesday() {
                return wednesday;
            }

            public void setWednesday(Day wednesday) {
                this.wednesday = wednesday;
            }

            public Day getThursday() {
                return thursday;
            }

            public void setThursday(Day thursday) {
                this.thursday = thursday;
            }

            public Day getFriday() {
                return friday;
            }

            public void setFriday(Day friday) {
                this.friday = friday;
            }

            public Day getSaturday() {
                return saturday;
            }

            public void setSaturday(Day saturday) {
                this.saturday = saturday;
            }

            public Day getSunday() {
                return sunday;
            }

            public void setSunday(Day sunday) {
                this.sunday = sunday;
            }

            class Day {
                private boolean available;
                private SimpleDateFormat from;
                private SimpleDateFormat to;

                public boolean isAvailable() {
                    return available;
                }

                public void setAvailable(boolean available) {
                    this.available = available;
                }

                public SimpleDateFormat getFrom() {
                    return from;
                }

                public void setFrom(SimpleDateFormat from) {
                    this.from = from;
                }

                public void setFrom(String from) {
                    this.from = new SimpleDateFormat(from, Locale.FRENCH);
                }

                public SimpleDateFormat getTo() {
                    return to;
                }

                public void setTo(SimpleDateFormat to) {
                    this.to = to;
                }

                public void setTo(String to) {
                    this.to = new SimpleDateFormat(to, Locale.FRENCH);
                }
            }
        }
    }

    class Statistics {
        private int games;
        private int goals;
        private int wins;

        public int getGames() {
            return games;
        }

        public void setGames(int games) {
            this.games = games;
        }

        public int getGoals() {
            return goals;
        }

        public void setGoals(int goals) {
            this.goals = goals;
        }

        public int getWins() {
            return wins;
        }

        public void setWins(int wins) {
            this.wins = wins;
        }
    }

    class Settings {
        private int notificationsLevel;
        private float maxDistance;

        public int getNotificationsLevel() {
            return notificationsLevel;
        }

        public void setNotificationsLevel(int notificationsLevel) {
            this.notificationsLevel = notificationsLevel;
        }

        public float getMaxDistance() {
            return maxDistance;
        }

        public void setMaxDistance(float maxDistance) {
            this.maxDistance = maxDistance;
        }
    }
}
