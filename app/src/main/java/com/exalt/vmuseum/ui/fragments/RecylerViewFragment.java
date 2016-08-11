package com.exalt.vmuseum.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.exalt.vmuseum.R;
import com.exalt.vmuseum.VMuseum;
import com.exalt.vmuseum.models.PlaceDetails;
import com.exalt.vmuseum.services.PlacesResponseService;
import com.exalt.vmuseum.ui.activities.DisplayActivity;
import com.exalt.vmuseum.utilities.DividerItemDecoration;
import com.exalt.vmuseum.utilities.RecyclerTouchListener;
import com.exalt.vmuseum.utilities.adapters.RecyclerViewAdapter;
import com.exalt.vmuseum.utilities.interfaces.ClickListener;
import com.exalt.vmuseum.utilities.interfaces.DisplayActivityCallback;
import com.exalt.vmuseum.utilities.interfaces.PlaceDetailsCallback;


/**
 * Created by Abdallah on 7/24/2016.
 */
public class RecylerViewFragment extends Fragment implements PlaceDetailsCallback {
    private static DisplayActivityCallback displayActivityCallback;

    public static RecylerViewFragment newInstance(DisplayActivityCallback displayActivityCallback) {
        RecylerViewFragment tabFragment = new RecylerViewFragment();
        RecylerViewFragment.displayActivityCallback = displayActivityCallback;
        return tabFragment;

    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        /**
         *Inflate tab_layout and setup Views.
         */
        View x = inflater.inflate(R.layout.tab_layout, null);
        RecyclerView recyclerView = (RecyclerView) x.findViewById(R.id.places_recycler_view);
        setRecyclerView(recyclerView, container);
        return x;
    }

    private void setRecyclerView(RecyclerView recyclerView, ViewGroup container) {
        RecyclerViewAdapter adapter = new RecyclerViewAdapter(getContext());
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(container.getContext().getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(container.getContext(), LinearLayoutManager.VERTICAL));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
        ProgressBar progressBar = (ProgressBar) getActivity().findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.GONE);
        final PlaceDetailsCallback placeDetailsCallback = this;
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(container.getContext().getApplicationContext(), recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                //display Details
                PlacesResponseService.getPlaceDetails(VMuseum.placesList.get(position).getId(), placeDetailsCallback);

            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

    }

    //successfully retrieved the place details for the given place from the server
    @Override
    public void onSuccess(PlaceDetails placeDetails) {
        DisplayActivity.mService.startAudio(placeDetails.getAudio());
        displayActivityCallback.changeContainerFragment(DetailsFragment.newInstance(placeDetails, displayActivityCallback));
    }

    @Override
    public void onFailure(Object error) {

    }
}
