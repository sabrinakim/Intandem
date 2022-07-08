package com.example.intandem;

import com.example.intandem.dataModels.BusinessSearchResult;
import com.example.intandem.dataModels.ReviewSearchResult;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface YelpService {

    @GET("businesses/search")
    Call<BusinessSearchResult> searchBusinesses(
            @Header("Authorization") String authHeader,
            @Query("term") String term,
            @Query("location") String location,
            @Query("latitude") double latitude,
            @Query("longitude") double longitude,
            @Query("limit") int limit
    );

    @GET("businesses/{id}/reviews")
    Call<ReviewSearchResult> searchReviews(
            @Header("Authorization") String authHeader,
            @Path("id") String id
    );

}
