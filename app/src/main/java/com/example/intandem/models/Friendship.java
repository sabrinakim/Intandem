package com.example.intandem.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("Friendships")
public class Friendships extends ParseObject {
    public static final String KEY_USER1Id = "user1Id";
    public static final String KEY_USER2Id = "user2Id";

    // empty constructor needed by Parceler library
    public Friendships() {}

    public String getUser1Id() {
        return getString(KEY_USER1Id);
    }

    public void setUser1Id(String user1Id) {
        put(KEY_USER1Id, user1Id);
    }

    public String getUser2Id() {
        return getString(KEY_USER2Id);
    }

    public void setUser2Id(String user2Id) {
        put(KEY_USER1Id, user2Id);
    }
}