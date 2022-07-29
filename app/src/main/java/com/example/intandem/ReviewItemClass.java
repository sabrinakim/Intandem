package com.example.intandem;

public class ReviewItemClass {
    public static final int Header = 0;
    public static final int Layout1 = 1;

    private int viewType;

    private String placeName, placeImageUrl, address;
    private double placeRatingOverall;

    public ReviewItemClass(int viewType, String placeName, String address, String placeImageUrl, double placeRatingOverall) {
        this.placeName = placeName;
        this.placeImageUrl = placeImageUrl;
        this.address = address;
        this.placeRatingOverall = placeRatingOverall;
        this.viewType = viewType;
    }

    public String getPlaceName() {
        return placeName;
    }

    public String getPlaceImageUrl() {
        return placeImageUrl;
    }

    public double getPlaceRatingOverall() {
        return placeRatingOverall;
    }

    public String getAddress() { return address; }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    public void setPlaceImageUrl(String placeImageUrl) {
        this.placeImageUrl = placeImageUrl;
    }

    public void setAddress(String address) { this.address = address; }

    public void setPlaceRatingOverall(double placeRatingOverall) {
        this.placeRatingOverall = placeRatingOverall;
    }

    private String profilePicUrl, name, text, source;
    private double placeRating;

    public ReviewItemClass(int viewType, String profilePicUrl, String name, String text, String source, double placeRating) {
        this.profilePicUrl = profilePicUrl;
        this.name = name;
        this.text = text;
        this.source = source;
        this.placeRating = placeRating;
        this.viewType = viewType;
    }

    public String getProfilePicUrl() {
        return profilePicUrl;
    }

    public String getName() {
        return name;
    }

    public String getText() {
        return text;
    }

    public String getSource() { return  source; }

    public double getPlaceRating() {
        return placeRating;
    }

    public void setProfilePicUrl(String profilePicUrl) {
        this.profilePicUrl = profilePicUrl;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setSource(String source) { this.source = source; }

    public void setPlaceRating(double placeRating) {
        this.placeRating = placeRating;
    }

    public int getViewType() {
        return viewType; }

    public void setViewType(int viewType)
    {
        this.viewType = viewType;
    }
}
