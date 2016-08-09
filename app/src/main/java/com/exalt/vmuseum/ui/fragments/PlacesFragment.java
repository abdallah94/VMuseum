package com.exalt.vmuseum.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.exalt.vmuseum.Constants;
import com.exalt.vmuseum.R;
import com.exalt.vmuseum.models.PlaceDetails;
import com.squareup.picasso.Picasso;

/**
 * Created by Abdallah on 7/24/2016.
 */
public class PlacesFragment extends Fragment {

    public static PlacesFragment newInstance(PlaceDetails place) {
        PlacesFragment placesFragment = new PlacesFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constants.PLACE_KEY, place);
        placesFragment.setArguments(bundle);
        return placesFragment;

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.page_layout, container, false);
        PlaceDetails place = (PlaceDetails) getArguments().getSerializable(Constants.PLACE_KEY);
        ImageView imageView = (ImageView) view.findViewById(R.id.page_image_view);
        TextView textView = (TextView) view.findViewById(R.id.page_place_name_text_view);
        textView.setText(place.getName());
        Picasso.with(getContext()).load(place.getImage()).resize(300, 300).into(imageView);
        //set image view and text view
        return view;
    }
}
