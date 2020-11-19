package com.cuijeb.firebasechatroomapp;

public class User {
    public String userName;
    public String[] groups;

    public User() {}

    public User(String userName, String[] groups) {
        this.userName = userName;
        this.groups = groups;
    }
}
