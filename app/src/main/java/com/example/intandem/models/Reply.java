package com.example.intandem.models;

import com.example.intandem.models.Post;
import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("Reply")
public class Reply extends ParseObject {
    public static final String KEY_USER = "user";
    public static final String KEY_PICTURE = "picture";
    public static final String KEY_CAPTION = "caption";
    public static final String KEY_POST = "post";


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

    public String getCaption() {
        return getString(KEY_CAPTION);
    }

    public void setCaption(String caption) {
        put(KEY_CAPTION, caption);
    }

    public Post getPost() {
        return (Post) getParseObject(KEY_POST);
    }

    public void setPost(Post post) {
        put(KEY_POST, post);
    }


}
