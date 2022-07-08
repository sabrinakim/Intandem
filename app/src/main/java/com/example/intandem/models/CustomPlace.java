package com.example.intandem.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;

@ParseClassName("CustomPlace")
public class CustomPlace extends ParseObject {
    public static final String KEY_GPLACEID = "gPlaceId";

    public CustomPlace() {}

    public String getGPlaceId() {
        return getString(KEY_GPLACEID);
    }

    public void setGPlaceId(String gPlaceId) {
        put(KEY_GPLACEID, gPlaceId);
    }
}
