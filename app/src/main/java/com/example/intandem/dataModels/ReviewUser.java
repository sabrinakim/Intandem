package com.example.intandem.dataModels;


import com.google.gson.annotations.SerializedName;

public class ReviewUser {
    String id;
    String name;

    @SerializedName("image_url")
    String imageUrl;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}
