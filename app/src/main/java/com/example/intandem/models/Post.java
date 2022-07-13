package com.example.intandem.models;

import com.google.android.libraries.places.api.model.Place;
import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.Date;

@ParseClassName("Post")
public class Post extends ParseObject {
    public static final String KEY_USER = "user";
    public static final String KEY_USER_FB_ID = "userFbId";
    public static final String KEY_EVENT = "event";
    public static final String KEY_CREATEDAT = "createdAt";
    public static final String KEY_CUSTOMPLACE = "customPlace";
    public static final String KEY_PICTURE = "picture";
    public static final String KEY_CAPTION = "caption";
//    public static final String KEY_DURATION = "duration";
//    public static final String KEY_TIMEUNIT = "timeUnit";
    public static final String KEY_EXPIRATION = "expiration";

    // empty constructor needed by Parceler library
    public Post() {}

    public ParseUser getUser() {
        return getParseUser(KEY_USER);
    }

    public void setUser(ParseUser user) {
        put(KEY_USER, user);
    }

    public String getUserFbId() {
        return getString(KEY_USER_FB_ID);
    }

    public void setUserFbId(String userFbId) {
        put(KEY_USER_FB_ID, userFbId);
    }

    public String getEvent() {
        return getString(KEY_EVENT);
    }

    public void setEvent(String event) {
        put(KEY_EVENT, event);
    }

    public CustomPlace getCustomPlace() {
        return (CustomPlace) getParseObject(KEY_CUSTOMPLACE);
    }

    public void setCustomPlace(CustomPlace customPlace) {
        put(KEY_CUSTOMPLACE, customPlace);
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

    public Date getExpiration() {
        return getDate(KEY_EXPIRATION);
    }

    public void setExpiration(Date expiration) {
        put(KEY_EXPIRATION, expiration);
    }

//    public String getDuration() {
//        return getString(KEY_DURATION);
//    }
//
//    public void setDuration(String duration) {
//        put(KEY_DURATION, duration);
//    }
//
//    public String getTimeUnit() {
//        return getString(KEY_TIMEUNIT);
//    }
//
//    public void setTimeUnit(String timeUnit) {
//        put(KEY_TIMEUNIT, timeUnit);
//    }

}

