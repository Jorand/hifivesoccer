package com.hifivesoccer.models;

/**
 * Created by hugohil on 31/10/15.
 */
public class Team extends AppBaseModel {
    private Infos infos;
    private User[] peoples;
    private Chat chat;

    public Infos getInfos() {
        return infos;
    }

    public void setInfos(Infos infos) {
        this.infos = infos;
    }

    public User[] getPeoples() {
        return peoples;
    }

    public void setPeoples(User[] peoples) {
        this.peoples = peoples;
    }

    public Chat getChat() {
        return chat;
    }

    public void setChat(Chat chat) {
        this.chat = chat;
    }

    class Infos {
        private String name;
        private String description;
        private String area;

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

        public String getArea() {
            return area;
        }

        public void setArea(String area) {
            this.area = area;
        }
    }
}
