package com.example.intandem.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;

@ParseClassName("CustomPlaceToReview")
public class CustomPlaceToReview extends ParseObject {
    public static final String KEY_CUSTOMPLACE = "customPlace";
    public static final String KEY_REVIEW = "review";

    public CustomPlace getCustomPlace() {
        return (CustomPlace) getParseObject(KEY_CUSTOMPLACE);
    }

    public void setCustomPlace(CustomPlace customPlace) {
        put(KEY_CUSTOMPLACE, customPlace);
    }

    public Review getReview() {
        return (Review) getParseObject(KEY_REVIEW);
    }

    public void setReview(Review review) {
        put(KEY_REVIEW, review);
    }
}
