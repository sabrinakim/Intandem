package com.example.intandem;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("Reply")
public class Reply extends ParseObject {
    public static final String KEY_USER = "user";
    public static final String KEY_PICTURE = "picture";

    public Reply() {}

    public ParseUser getUser() {
        return getParseUser(KEY_USER);
    }

    public void setUser(ParseUser user) {
        put(KEY_USER, user);
    }

    public ParseFile getPicture() {
        return getParseFile(KEY_PICTURE);
    }

    public void setPicture(ParseFile picture) {
        put(KEY_PICTURE, picture);
    }
}
