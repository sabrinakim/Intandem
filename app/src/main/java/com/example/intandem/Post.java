package com.example.intandem;

import com.google.android.libraries.places.api.model.Place;
import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("Post")
public class Post extends ParseObject {
    public static final String KEY_USER = "user";
    public static final String KEY_EVENT = "event";
    public static final String KEY_PLACEID = "placeId";
    public static final String KEY_PICTURE = "picture";
    public static final String KEY_CAPTION = "caption";
    public static final String KEY_DURATION = "duration";
    public static final String KEY_TIMEUNIT = "timeUnit";
    public static final String KEY_PLACENAME = "placeName";
    public static final String KEY_LOCATIONPOINT = "locationPoint";

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

    public String getPlaceId() {
        return getString(KEY_PLACEID);
    }

    public void setPlaceId(String placeId) {
        put(KEY_PLACEID, placeId);
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

    public String getDuration() {
        return getString(KEY_DURATION);
    }

    public void setDuration(String duration) {
        put(KEY_DURATION, duration);
    }

    public String getTimeUnit() {
        return getString(KEY_TIMEUNIT);
    }

    public void setTimeUnit(String timeUnit) {
        put(KEY_TIMEUNIT, timeUnit);
    }

    public String getPlaceName() {
        return getString(KEY_PLACENAME);
    }

    public void setPlaceName(String placeName) {
        put(KEY_PLACENAME, placeName);
    }

    public ParseGeoPoint getLocationPoint() {
        return getParseGeoPoint(KEY_LOCATIONPOINT);
    }

    public void setLocationPoint(ParseGeoPoint locationPoint) {
        put(KEY_LOCATIONPOINT, locationPoint);
    }
}

