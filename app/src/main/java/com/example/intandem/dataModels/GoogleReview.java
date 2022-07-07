package com.example.intandem.dataModels;

import com.google.gson.annotations.SerializedName;

public class GoogleReview {

    @SerializedName("author_name")
    String authorName;

    int rating;
    String text;

    public String getAuthorName() {
        return authorName;
    }

    public int getRating() {
        return rating;
    }

    public String getText() {
        return text;
    }
}
