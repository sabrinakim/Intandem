package com.example.intandem.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;

import java.util.Date;

@ParseClassName("Review")
public class Review extends ParseObject {

    public static final String KEY_NAME = "name";
    public static final String KEY_DATE = "date";
    public static final String KEY_TEXT = "text";
    public static final String KEY_RATING = "rating";

    public Review() {}

    public String getName() {
        return getString(KEY_NAME);
    }
    public void putName(String name) {
        put(KEY_NAME, name);
    }
    public Date getDate() {
        return getDate(KEY_DATE);
    }
    public void putDate(Date date) {
        put(KEY_DATE, date);
    }
    public String getText() {
        return getString(KEY_TEXT);
    }
    public void putText(String text) {
        put(KEY_TEXT, text);
    }
    public double getRating() {
        return getDouble(KEY_RATING);
    }
    public void putRating(double rating) {
        put(KEY_RATING, rating);
    }
}
