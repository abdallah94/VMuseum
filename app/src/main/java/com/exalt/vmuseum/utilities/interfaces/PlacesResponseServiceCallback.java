package com.exalt.vmuseum.utilities.interfaces;


import com.exalt.vmuseum.models.Place;
import com.exalt.vmuseum.models.PlaceDetails;

import java.util.List;

/**
 * Created by Abdallah on 7/25/2016.
 */
public interface PlacesResponseServiceCallback {
    public void onFailure(Object error);

    public void onSuccess(List<PlaceDetails> placeList);
}
