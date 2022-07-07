package com.example.intandem;

import com.example.intandem.dataModels.DistanceSearchResult;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface DistanceMatrixService {

    @GET("maps/api/distancematrix/json")
    Call<DistanceSearchResult> getDistanceSearchResult(
            @Query("origins") String origins,
            @Query("destinations") String destinations,
            @Query("mode") String mode,
            @Query("language") String language,
            @Query("key") String key
    );
}
