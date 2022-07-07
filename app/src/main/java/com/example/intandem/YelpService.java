package com.example.intandem;

import com.example.intandem.dataModels.BusinessSearchResult;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

public interface YelpService {

    @GET("businesses/search")
    Call<BusinessSearchResult> searchBusinesses(
            @Header("Authorization") String authHeader,
            @Query("term") String term,
            @Query("latitude") double latitude,
            @Query("longitude") double longitude
    );

//    @GET("autocomplete")
//    Call<ResponseBody> searchAutocomplete(
//            @Header("Authorization") String authHeader,
//            @Query("text") String text
//            @Query("latitude") Double latitude,
//            @Query("longitude") Double longitude
//    );

}
