package com.example.intandem;

import com.example.intandem.dataModels.DistanceSearchResult;
import com.example.intandem.dataModels.PlaceDetailsSearchResult;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GoogleMapsService {

    @GET("maps/api/distancematrix/json")
    Call<DistanceSearchResult> getDistanceSearchResult(
            @Query("origins") String origins,
            @Query("destinations") String destinations,
            @Query("mode") String mode,
            @Query("language") String language,
            @Query("key") String key
    );

    @GET("maps/api/place/details/json")
    Call<PlaceDetailsSearchResult> getPlaceDetailsSearchResult(
            @Query("place_id") String placeId,
            @Query("key") String key
    );
}
