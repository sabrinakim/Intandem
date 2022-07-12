package com.example.intandem.dataModels;

import com.google.gson.annotations.SerializedName;

public class GoogleReview {

    @SerializedName("author_name")
    String authorName;
    @SerializedName("profile_photo_url")
    String profilePhotoUrl;
    double rating;
    String text;

    public String getAuthorName() {
        return authorName;
    }

    public String getProfilePhotoUrl() {
        return profilePhotoUrl;
    }

    public double getRating() {
        return rating;
    }

    public String getText() {
        return text;
    }
}
