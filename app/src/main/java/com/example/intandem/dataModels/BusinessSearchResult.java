package com.example.intandem.dataModels;

import java.util.ArrayList;
import java.util.List;

public class BusinessSearchResult {

    List<Business> businesses;

    public BusinessSearchResult() {
        businesses = new ArrayList<>();
    }

    public List<Business> getBusinesses() {
        return businesses;
    }
}
