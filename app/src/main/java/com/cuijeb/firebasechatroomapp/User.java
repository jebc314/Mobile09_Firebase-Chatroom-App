package com.cuijeb.firebasechatroomapp;

import java.util.HashMap;

public class User {
    public String userId;
    public String userName;
    public HashMap<String, Boolean> chats;

    public User() {}

    public User(String userId, String userName, HashMap<String, Boolean> chats) {
        this.userId = userId;
        this.userName = userName;
        this.chats = chats;
    }
}
