package com.example.intandem.dataModels;

import com.google.gson.annotations.SerializedName;

public class Location {
    public String city;
    public String country;
    public String address2;
    public String address3;
    public String state;
    public String address1;
    @SerializedName("zip_code")
    public String zipCode;

    public String getCity() {
        return city;
    }

    public String getCountry() {
        return country;
    }

    public String getAddress2() {
        return address2;
    }

    public String getAddress3() {
        return address3;
    }

    public String getState() {
        return state;
    }

    public String getAddress1() {
        return address1;
    }

    public String getZipCode() {
        return zipCode;
    }
}
