package com.example.intandem.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;

@ParseClassName("CustomPlace")
public class CustomPlace extends ParseObject {
    public static final String KEY_GPLACEID = "gPlaceId";
    public static final String KEY_NAME = "name";
    public static final String KEY_RATING = "rating";
    public static final String KEY_PRICE = "price";
    public static final String KEY_PLACE_IMAGE_URL = "placeImageUrl";
    public static final String KEY_LAT = "lat";
    public static final String KEY_LONG = "long";

    public CustomPlace() {}

    public String getGPlaceId() {
        return getString(KEY_GPLACEID);
    }

    public void setGPlaceId(String gPlaceId) {
        put(KEY_GPLACEID, gPlaceId);
    }

    public String getName() {
        return getString(KEY_NAME);
    }

    public void setName(String name) {
        put(KEY_NAME, name);
    }

    public double getRating() {
        return getDouble(KEY_RATING);
    }

    public void setRating(double rating) {
        put(KEY_RATING, rating);
    }

    public String getPrice() {
        return getString(KEY_PRICE);
    }

    public void setPrice(String price) {
        put(KEY_PRICE, price);
    }

    public String getPlaceImageUrl() {
        return getString(KEY_PLACE_IMAGE_URL);
    }

    public void setPlaceImageUrl(String placeImageUrl) {
        put(KEY_PLACE_IMAGE_URL, placeImageUrl);
    }

    public double getLat() {
        return getDouble(KEY_LAT);
    }

    public void setLat(double lat) {
        put(KEY_LAT, lat);
    }

    public double getLong() {
        return getDouble(KEY_LONG);
    }

    public void setLong(double longitude) {
        put(KEY_LONG, longitude);
    }
}
