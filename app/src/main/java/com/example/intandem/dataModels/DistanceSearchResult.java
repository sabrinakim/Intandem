package com.example.intandem.dataModels;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class DistanceSearchResult {

    @SerializedName("destination_addresses")
    List<String> destinationAddresses;

    @SerializedName("origin_addresses")
    List<String> originAddresses;

    List<OriginToDestinations> rows;

    String status;

    public DistanceSearchResult() {
        destinationAddresses = new ArrayList<>();
        originAddresses = new ArrayList();
        rows = new ArrayList<>();
    }

    public List<String> getDestinationAddresses() {
        return destinationAddresses;
    }

    public List<String> getOriginAddresses() {
        return originAddresses;
    }

    public List<OriginToDestinations> getRows() {
        return rows;
    }

    public String getStatus() {
        return status;
    }
}
