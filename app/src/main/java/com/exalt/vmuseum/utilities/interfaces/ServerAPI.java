package com.exalt.vmuseum.utilities.interfaces;

import com.exalt.vmuseum.models.Place;
import com.exalt.vmuseum.models.PlaceDetails;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by Abdallah on 7/25/2016.
 */
public interface ServerAPI {
    @GET("/getItems/{userID}")
    Call<List<PlaceDetails>> getPlaces(@Path("userID") int userID);

    @GET("/{userID}/getItemDetails/{itemID}")
    Call<PlaceDetails> getPlaceDetails(@Path("userID") int userID, @Path("itemID") int itemID);
}
