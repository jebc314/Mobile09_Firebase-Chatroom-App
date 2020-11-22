package com.cuijeb.firebasechatroomapp;

import java.util.HashMap;

public class Messages {
    public HashMap<String, Message> messages;
    public Messages(){}

    public Messages(HashMap<String, Message> messages) {
        this.messages = messages;
    }
}
