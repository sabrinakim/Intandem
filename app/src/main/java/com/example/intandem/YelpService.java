package com.example.intandem;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

public interface YelpService {

    @GET("businesses/search")
    Call<ResponseBody> searchRestaurants(
            @Header("Authorization") String authHeader,
            @Query("term") String searchTerm,
            @Query("location") String location
    );

//    @GET("autocomplete")
//    Call<ResponseBody> searchAutocomplete(
//            @Header("Authorization") String authHeader,
//            @Query("text") String text
//            @Query("latitude") Double latitude,
//            @Query("longitude") Double longitude
//    );

}
