package com.cuijeb.firebasechatroomapp;

import java.util.HashMap;

public class User {
    public String userId;
    public String userName;
    public HashMap<String, Boolean> chats;

    public User() {}

    public User(String userName, HashMap<String, Boolean> chats) {
        this.userName = userName;
        this.chats = chats;
    }
}
