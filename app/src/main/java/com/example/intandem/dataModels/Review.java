package com.example.intandem.dataModels;

import com.google.gson.annotations.SerializedName;

public class Review {
    String id;
    int rating;
    ReviewUser user;
    String text;
    @SerializedName("time_created")
    String timeCreated;
    String url;

    public String getId() {
        return id;
    }

    public int getRating() {
        return rating;
    }

    public ReviewUser getUser() {
        return user;
    }

    public String getText() {
        return text;
    }

    public String getTimeCreated() {
        return timeCreated;
    }

    public String getUrl() {
        return url;
    }
}
