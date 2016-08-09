package com.exalt.vmuseum;

import android.app.Application;

import com.exalt.vmuseum.models.Place;
import com.exalt.vmuseum.models.PlaceDetails;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Abdallah on 8/4/2016.
 */
public class VMuseum extends Application {
    public static int userID;
    public static List<PlaceDetails> placesList;

    @Override
    public void onCreate() {
        super.onCreate();
        userID = 1;
    }
}
