package com.example;

import java.util.List;

public class CustomMessage {
    private List<String> users;
    private String message;
    private String currentUser;
    private String sendMessageUser;

    public List<String> getUsers() {
        return users;
    }

    public void setUsers(List<String> users) {
        this.users = users;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(String currentUser) {
        this.currentUser = currentUser;
    }

    public String getSendMessageUser() {
        return sendMessageUser;
    }

    public void setSendMessageUser(String sendMessageUser) {
        this.sendMessageUser = sendMessageUser;
    }
}
