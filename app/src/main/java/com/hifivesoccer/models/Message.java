package com.hifivesoccer.models;

/**
 * Created by hugohil on 31/10/15.
 */
public class Message extends AppBaseModel {
    private Chat chat;

    private String message;

    private User author;

    public Chat getChat() {
        return chat;
    }

    public void setChat(Chat chat) {
        this.chat = chat;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }
}
