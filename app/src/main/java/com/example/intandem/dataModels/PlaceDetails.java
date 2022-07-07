package com.example.intandem.dataModels;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class PlaceDetails {

    @SerializedName("formatted_address")
    String formattedAddress;
    String name;
    @SerializedName("place_id")
    String placeId;
    double rating;
    List<GoogleReview> reviews;

    public PlaceDetails() {
        reviews = new ArrayList<>();
    }


    public String getFormattedAddress() {
        return formattedAddress;
    }

    public String getName() {
        return name;
    }

    public String getPlaceId() {
        return placeId;
    }

    public double getRating() {
        return rating;
    }

    public List<GoogleReview> getReviews() {
        return reviews;
    }

}
