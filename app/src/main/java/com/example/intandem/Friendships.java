package com.example.intandem;

import com.parse.ParseClassName;
import com.parse.ParseObject;

@ParseClassName("Friendships")
public class Friendships extends ParseObject {
    public static final String KEY_USER1 = "user1";
    public static final String KEY_USER2 = "user2";

    // empty constructor needed by Parceler library
    public Friendships() {}


}