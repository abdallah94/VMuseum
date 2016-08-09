package com.exalt.vmuseum.utilities.interfaces;

import com.exalt.vmuseum.models.PlaceDetails;

/**
 * Created by Abdallah on 8/4/2016.
 */
public interface PlaceDetailsCallback {
    public void onSuccess(PlaceDetails placeDetails);
    public void onFailure(Object error);
}
