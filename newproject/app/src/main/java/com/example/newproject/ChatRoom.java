package com.example.newproject;

import java.util.HashMap;

public class ChatRoom {
    private String id;
    private String name;
    private HashMap<String,String> members;
    public String isGroupChat;

    public ChatRoom() {}

    public ChatRoom(String id, String name, HashMap<String,String> members) {
        this.id = id;
        this.name = name;
        this.members = members;
        this.isGroupChat = isGroupChat;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public HashMap<String,String> getMembers() {
        return members;
    }

    public String getIsGroupChat() {
        return isGroupChat;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setMembers(HashMap<String,String> members) {
        this.members = members;
    }

    public void setIsGroupChat(String isGroupChat) {
        this.isGroupChat = isGroupChat;
    }

    public boolean containsUser(String userId) {
        return members != null && members.containsKey(userId);
    }
}