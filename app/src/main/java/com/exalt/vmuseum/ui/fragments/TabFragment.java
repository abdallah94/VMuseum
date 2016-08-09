package com.exalt.vmuseum.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.exalt.vmuseum.R;
import com.exalt.vmuseum.VMuseum;
import com.exalt.vmuseum.models.PlaceDetails;
import com.exalt.vmuseum.services.PlacesResponseService;
import com.exalt.vmuseum.utilities.adapters.MyAdapter;
import com.exalt.vmuseum.utilities.interfaces.DisplayActivityCallback;
import com.exalt.vmuseum.utilities.interfaces.PlaceDetailsCallback;


/**
 * Created by Abdallah on 7/24/2016.
 */
public class TabFragment extends Fragment implements PlaceDetailsCallback {
    private TabLayout tabLayout;
    public ViewPager viewPager;
    private static DisplayActivityCallback displayActivityCallback;

    public static TabFragment newInstance(DisplayActivityCallback displayActivityCallback) {
        TabFragment tabFragment = new TabFragment();
        TabFragment.displayActivityCallback = displayActivityCallback;
        return tabFragment;

    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        /**
         *Inflate tab_layout and setup Views.
         */
        View x = inflater.inflate(R.layout.tab_layout, null);
        tabLayout = (TabLayout) x.findViewById(R.id.tabs);
        viewPager = (ViewPager) x.findViewById(R.id.viewpager);
        viewPager.setAdapter(new MyAdapter(getChildFragmentManager()));
        tabLayout.post(new Runnable() {
            @Override
            public void run() {
                tabLayout.setupWithViewPager(viewPager);
            }
        });
        setViewPagerListener(viewPager);

        return x;
    }

    private void setViewPagerListener(final ViewPager viewPager) {
        final PlaceDetailsCallback callback = this;

        viewPager.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int currentTab = viewPager.getCurrentItem();
                int id = VMuseum.placesList.get(currentTab).getId();
                PlacesResponseService.getPlaceDetails(id, callback);
            }
        });

    }

    @Override
    public void onSuccess(PlaceDetails placeDetails) {
        displayActivityCallback.changeContainerFragment(DetailsFragment.newInstance(placeDetails, displayActivityCallback));
    }

    @Override
    public void onFailure(Object error) {

    }
}
