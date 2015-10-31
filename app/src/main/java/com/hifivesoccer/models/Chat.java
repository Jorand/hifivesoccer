package com.hifivesoccer.models;

/**
 * Created by hugohil on 31/10/15.
 */
public class Chat extends AppBaseModel {
    private Team[] teams;

    private User[] peoples;

    private Message[] messages;

    private Game game;

    public Team[] getTeams() {
        return teams;
    }

    public void setTeams(Team[] teams) {
        this.teams = teams;
    }

    public User[] getPeoples() {
        return peoples;
    }

    public void setPeoples(User[] peoples) {
        this.peoples = peoples;
    }

    public Message[] getMessages() {
        return messages;
    }

    public void setMessages(Message[] messages) {
        this.messages = messages;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }
}
