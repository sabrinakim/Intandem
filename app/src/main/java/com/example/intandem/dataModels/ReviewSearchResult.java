package com.example.intandem.dataModels;

import java.util.ArrayList;
import java.util.List;

public class ReviewSearchResult {

    List<Review> reviews;

    public ReviewSearchResult() {
        reviews = new ArrayList<>();
    }

    public List<Review> getReviews() {
        return reviews;
    }
}
