package com.exalt.vmuseum.utilities.adapters;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.exalt.vmuseum.VMuseum;
import com.exalt.vmuseum.ui.fragments.PlacesFragment;


/**
 * Created by Abdallah on 7/24/2016.
 */
public class MyAdapter extends FragmentStatePagerAdapter {
    public MyAdapter(FragmentManager fm) {
        super(fm);
    }

    /**
     * Return fragment with respect to Position .
     */

    @Override
    public PlacesFragment getItem(int pos) {
        if (pos < VMuseum.placesList.size()) {
            return PlacesFragment.newInstance(VMuseum.placesList.get(pos));
        } else {
            return PlacesFragment.newInstance(VMuseum.placesList.get(0));
        }
    }

    @Override
    public int getCount() {
        return VMuseum.placesList.size();
    }

    /**
     * This method returns the title of the tab according to the position.
     */

    @Override
    public CharSequence getPageTitle(int position) {
        if (position < VMuseum.placesList.size()) {
            return (VMuseum.placesList.get(position).getName());
        } else {
            return null;
        }
    }
}

