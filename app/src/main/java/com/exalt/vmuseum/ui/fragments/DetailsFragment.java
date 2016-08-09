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
import com.exalt.vmuseum.utilities.interfaces.DisplayActivityCallback;
import com.squareup.picasso.Picasso;

/**
 * Created by Abdallah on 8/2/2016.
 */
public class DetailsFragment extends Fragment {
    private ImageView mImageView;
    private TextView mNameTextView;
    private TextView mSummayTextView;
    private static DisplayActivityCallback displayActivityCallback;

    public static DetailsFragment newInstance(PlaceDetails placeDetails, DisplayActivityCallback displayActivityCallback) {
        DetailsFragment detailsFragment = new DetailsFragment();
        DetailsFragment.displayActivityCallback = displayActivityCallback;
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constants.PLACE_DETAILS, placeDetails);
        detailsFragment.setArguments(bundle);
        return detailsFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.details_fragment_layout, container, false);
        PlaceDetails placeDetails = (PlaceDetails) getArguments().getSerializable(Constants.PLACE_DETAILS);
        initViews(view, container);
        displayActivityCallback.setToolbarTitle(placeDetails.getName());
        Picasso.with(getContext()).load(placeDetails.getImage()).resize(300, 300).into(mImageView);
        mNameTextView.setText(placeDetails.getName() + ", " + placeDetails.getOrigin());
        mSummayTextView.setText(placeDetails.getSummary());
        return view;

    }

    private void initViews(View view, ViewGroup container) {
        mImageView = (ImageView) view.findViewById(R.id.details_image_view);
        mNameTextView = (TextView) view.findViewById(R.id.details_name_text_view);
        mSummayTextView = (TextView) view.findViewById(R.id.details_summary_text_view);

    }

}
