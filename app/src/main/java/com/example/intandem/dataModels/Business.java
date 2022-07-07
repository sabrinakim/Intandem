package com.example.intandem.dataModels;

import com.google.gson.annotations.SerializedName;

public class Business {

    double rating;
    String price;
    String phone;
    String id;
    String name;
    String url;

    @SerializedName("image_url")
    String imageUrl;

    double distance;

    public double getRating() {
        return rating;
    }

    public String getPrice() {
        return price;
    }

    public String getPhone() {
        return phone;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public double getDistance() {
        return distance;
    }
}
