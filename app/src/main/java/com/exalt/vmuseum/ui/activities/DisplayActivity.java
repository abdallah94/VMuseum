package com.exalt.vmuseum.ui.activities;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.exalt.vmuseum.R;
import com.exalt.vmuseum.VMuseum;
import com.exalt.vmuseum.models.PlaceDetails;
import com.exalt.vmuseum.services.PlacesResponseService;
import com.exalt.vmuseum.ui.fragments.TabFragment;
import com.exalt.vmuseum.utilities.interfaces.DisplayActivityCallback;
import com.exalt.vmuseum.utilities.interfaces.PlacesResponseServiceCallback;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class DisplayActivity extends AppCompatActivity implements PlacesResponseServiceCallback, DisplayActivityCallback {
    private static String tag = DisplayActivity.class.getSimpleName();
    private ImageView mprofileImageView;
    private TextView mnameTextView;
    private FragmentManager mFragmentManager;
    private FragmentTransaction mFragmentTransaction;
    private DrawerLayout mDrawerLayout;
    private Toolbar mToolbar;
    private int mCurrentTab = 0;
    private TabFragment mTabFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);
        initViews();
        PlacesResponseService.getPlaces(this);//get the list of places to display in viewPager

    }

    private void initViews() {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        NavigationView navigationView = (NavigationView) mDrawerLayout.findViewById(R.id.navigation_view);
        View headerLayout = navigationView.getHeaderView(0);
        mprofileImageView = (CircleImageView) headerLayout.findViewById(R.id.imageViewProfile);
        mnameTextView = (TextView) headerLayout.findViewById(R.id.nameView);
        ImageView imageView = (ImageView) headerLayout.findViewById(R.id.header_background);
        //mnameTextView.setText(Profile.getCurrentProfile().getName());    set Name
        Picasso.with(this).load(R.drawable.no_image).resize(100, 100).into(mprofileImageView);      //set picture
        Picasso.with(this).load(R.drawable.background).resize(250, 250).into(imageView);
        setupDrawerContent(navigationView);//add listener for the menu items
        mToolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.app_name,
                R.string.app_name);
        mDrawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();
    }

    @Override
    public void onFailure(Object error) {
        Log.v(tag, "Couldn't load data", (Throwable) error);
        Toast.makeText(this, "Couldn't load data ", Toast.LENGTH_LONG).show();
        Log.v(tag, "places list loading data", (Throwable) error);

    }

    @Override
    public void onSuccess(List<PlaceDetails> placeList) {
        VMuseum.placesList = placeList;
        mTabFragment = TabFragment.newInstance(this);
        mFragmentManager = getSupportFragmentManager();
        mFragmentTransaction = mFragmentManager.beginTransaction();
        mFragmentTransaction.replace(R.id.containerView, mTabFragment).addToBackStack(null).commit();
    }


    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        selectDrawerItem(menuItem);
                        return true;
                    }
                });
    }

    public void selectDrawerItem(MenuItem menuItem) {
        // Create a new fragment and specify the fragment to show based on nav item clicked
        switch (menuItem.getItemId()) {
            case R.id.nav_home_fragment:
                changeContainerFragment(TabFragment.newInstance(this));//reload the viewPager
                break;
        }
        // Close the navigation drawer
    }


    @Override
    public void changeContainerFragment(Fragment fragment) {
        mFragmentManager = getSupportFragmentManager();
        mFragmentTransaction = mFragmentManager.beginTransaction();
        mFragmentTransaction.replace(R.id.containerView, fragment).addToBackStack(null).commit();

    }

    @Override
    public void setToolbarTitle(String title) {
        mToolbar.setTitle(title);

    }
}
