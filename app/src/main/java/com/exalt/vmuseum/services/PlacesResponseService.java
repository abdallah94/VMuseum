package com.exalt.vmuseum.services;


import com.exalt.vmuseum.Constants;
import com.exalt.vmuseum.VMuseum;
import com.exalt.vmuseum.models.PlaceDetails;
import com.exalt.vmuseum.utilities.interfaces.PlaceDetailsCallback;
import com.exalt.vmuseum.utilities.interfaces.PlacesResponseServiceCallback;
import com.exalt.vmuseum.utilities.interfaces.ServerAPI;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Abdallah on 7/25/2016.
 */
public class PlacesResponseService {
    public static void getPlaces(final PlacesResponseServiceCallback callback) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.ROOT_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ServerAPI api = retrofit.create(ServerAPI.class);

        Call<List<PlaceDetails>> call = api.getPlaces(VMuseum.userID);
        call.enqueue(new Callback<List<PlaceDetails>>() {
            @Override
            public void onResponse(Call<List<PlaceDetails>> call, Response<List<PlaceDetails>> response) {
                if (response.body() == null) {
                    callback.onFailure(null);
                } else {
                    callback.onSuccess(response.body());
                }

            }

            @Override
            public void onFailure(Call<List<PlaceDetails>> call, Throwable t) {
                callback.onFailure(t);

            }

        });

    }

    public static void getPlaceDetails(int id, final PlaceDetailsCallback callback) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.ROOT_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ServerAPI api = retrofit.create(ServerAPI.class);

        Call<PlaceDetails> call = api.getPlaceDetails(VMuseum.userID, id);
        call.enqueue(new Callback<PlaceDetails>() {
            @Override
            public void onResponse(Call<PlaceDetails> call, Response<PlaceDetails> response) {
                callback.onSuccess(response.body());
            }

            @Override
            public void onFailure(Call<PlaceDetails> call, Throwable t) {
                callback.onFailure(t);

            }
        });
    }
}
