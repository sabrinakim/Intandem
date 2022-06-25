package com.example.intandem;

import com.google.android.libraries.places.api.model.Place;
import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("Post")
public class Post extends ParseObject {
    public static final String KEY_USER = "user";
    public static final String KEY_EVENT = "event";
    public static final String KEY_LOCATION = "location";
    public static final String KEY_PICTURE = "picture";
    public static final String KEY_CAPTION = "caption";

    // empty constructor needed by Parceler library
    public Post() {}

    public ParseUser getUser() {
        return getParseUser(KEY_USER);
    }

    public void setUser(ParseUser user) {
        put(KEY_USER, user);
    }

    public String getEvent() {
        return getString(KEY_EVENT);
    }

    public void setEvent(String event) {
        put(KEY_EVENT, event);
    }

    public Place getLocation() {
        return (Place) get(KEY_LOCATION);
    }

    public void setLocation(Place place) {
        put(KEY_LOCATION, place);
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
}

