package com.exalt.vmuseum;

import android.app.Application;

import com.exalt.vmuseum.models.PlaceDetails;
import com.gimbal.android.Gimbal;

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
        Gimbal.setApiKey(this, "1b250cd9-8d49-41ed-960e-92c7a20fa9ae");
        userID = 1;
    }
}
