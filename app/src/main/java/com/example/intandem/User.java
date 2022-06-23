package com.example.intandem;

import com.parse.ParseClassName;
import com.parse.ParseObject;

@ParseClassName("User")
public class User extends ParseObject {
    public static final String KEY_FBID = "fbId";
    public static final String KEY_FIRSTNAME = "firstName";
    public static final String KEY_LASTNAME = "lastName";

    // empty constructor needed by Parceler library
    public User() {}

    public String getFbId() {
        return getString(KEY_FBID);
    }

    public void setFbId(String fbId) {
        put(KEY_FBID, fbId);
    }

    public String getFirstName() {
        return getString(KEY_FIRSTNAME);
    }

    public void setFirstName(String firstName) {
        put(KEY_FIRSTNAME, firstName);
    }

    public String getLastName() {
        return getString(KEY_LASTNAME);
    }

    public void setLastName(String lastName) {
        put(KEY_LASTNAME, lastName);
    }

}
