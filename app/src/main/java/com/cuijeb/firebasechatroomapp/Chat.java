package com.cuijeb.firebasechatroomapp;

public class Chat implements Comparable<Chat>{
    public String chatId;
    public String title;
    public String message;
    public int timeStamp;

    public Chat() {}

    public Chat(String title, String message, int timeStamp) {
        this.title = title;
        this.message = message;
        this.timeStamp = timeStamp;
    }
    public int compareTo(Chat c){
        return timeStamp - c.timeStamp;
    }

    public boolean equals(Chat c){
        return chatId.equals(c.chatId);
    }
    public boolean equals(Object o){
        if (! (o instanceof Chat))
            return false;
        return equals((Chat)o);
    }
}
