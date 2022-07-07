package com.example.intandem.dataModels;

import java.util.ArrayList;
import java.util.List;

public class ReviewSearchResult {

    List<YelpReview> reviews;

    public ReviewSearchResult() {
        reviews = new ArrayList<>();
    }

    public List<YelpReview> getReviews() {
        return reviews;
    }
}
