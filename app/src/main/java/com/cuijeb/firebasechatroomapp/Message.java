package com.cuijeb.firebasechatroomapp;

public class Message {
    public String message;
    public int timeStamp;
    public String userId;
    public String userName;

    public Message() {}

    public Message(String message, int timeStamp, String userId, String userName) {
        this.message = message;
        this.timeStamp = timeStamp;
        this.userId = userId;
        this.userName = userName;
    }
}
