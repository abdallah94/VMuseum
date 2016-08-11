package com.exalt.vmuseum.ui.activities;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.exalt.vmuseum.R;
import com.exalt.vmuseum.VMuseum;
import com.exalt.vmuseum.models.PlaceDetails;
import com.exalt.vmuseum.services.PlacesResponseService;
import com.exalt.vmuseum.services.VMuseumService;
import com.exalt.vmuseum.ui.fragments.RecylerViewFragment;
import com.exalt.vmuseum.utilities.interfaces.DisplayActivityCallback;
import com.exalt.vmuseum.utilities.interfaces.PlacesResponseServiceCallback;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class DisplayActivity extends AppCompatActivity implements PlacesResponseServiceCallback, DisplayActivityCallback {
    public static VMuseumService mService;
    private static String tag = DisplayActivity.class.getSimpleName();
    private ImageView mprofileImageView;
    private TextView mnameTextView;
    private FragmentManager mFragmentManager;
    private DrawerLayout mDrawerLayout;
    private Toolbar mToolbar;
    private ProgressBar mProgressBar;
    private SwipeRefreshLayout mswipeRefreshLayout;
    private ToggleButton mToggleButton;
    private NavigationView mNavigationView;

    //for communicating with the service
    private ServiceConnection mConnection = new ServiceConnection() {
        // Called when the connection with the service is established
        public void onServiceConnected(ComponentName className, IBinder service) {
            VMuseumService.LocalBinder binder = (VMuseumService.LocalBinder) service;
            mService = binder.getService();
            mService.setBeaconListeners();
            setToggleListener(mNavigationView);//set listeners for automatic detection
            mToggleButton.setChecked(true);
        }

        // Called when the connection with the service disconnects unexpectedly
        public void onServiceDisconnected(ComponentName className) {
            Log.e(tag, "onServiceDisconnected");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);
        initViews();
        Intent intent = new Intent(this, VMuseumService.class);//start the audio service
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        PlacesResponseService.getPlaces(this);//get the list of places to display in viewPager

    }

    private void initViews() {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);
        mswipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_to_refresh_layout);
        mNavigationView = (NavigationView) mDrawerLayout.findViewById(R.id.navigation_view);
        View headerLayout = mNavigationView.getHeaderView(0);
        mprofileImageView = (CircleImageView) headerLayout.findViewById(R.id.imageViewProfile);
        mnameTextView = (TextView) headerLayout.findViewById(R.id.nameView);
        ImageView imageView = (ImageView) headerLayout.findViewById(R.id.header_background);
        //mnameTextView.setText(Profile.getCurrentProfile().getName());    set Name
        Picasso.with(this).load(R.drawable.no_image).resize(100, 100).into(mprofileImageView);      //set picture
        Picasso.with(this).load(R.drawable.background).resize(250, 250).into(imageView);
        setupDrawerContent(mNavigationView);//add listener for the menu items
        mToolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.app_name,
                R.string.app_name);
        mDrawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();
        setSwipeRefresh();//set listener for swipe refresh layout

    }

    private void setToggleListener(NavigationView navigationView) {
        Menu menu = navigationView.getMenu();
        mToggleButton = (ToggleButton) menu.findItem(R.id.automatic_detection).getActionView().findViewById(R.id.switch_automatic_beacons);
        mToggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    mService.setBeaconListeners();
                } else {
                    mService.removeBeaconListeners();
                }
            }
        });
    }

    private void setSwipeRefresh() {
        final PlacesResponseServiceCallback placesResponseServiceCallback = this;
        mswipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                PlacesResponseService.getPlaces(placesResponseServiceCallback);//get the list of places to display in viewPager
            }
        });
        mswipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_light, R.color.colorAccent, R.color.colorPrimaryDark);

    }

    @Override
    public void onFailure(Object error) {
        Log.v(tag, "Couldn't load places list data", (Throwable) error);
        Toast.makeText(this, "Couldn't load data ", Toast.LENGTH_LONG).show();
        mProgressBar.setVisibility(View.GONE);
        mswipeRefreshLayout.setRefreshing(false);
    }

    //successfully retrieved the list of places from the server
    @Override
    public void onSuccess(List<PlaceDetails> placeList) {
        mswipeRefreshLayout.setRefreshing(false);
        VMuseum.placesList = placeList;
        setToolbarTitle("VMuseum");
        changeContainerFragment(RecylerViewFragment.newInstance(this));
    }


    //set the listener for the drawer's menu
    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        selectDrawerItem(menuItem);
                        //Checking if the item is in checked state or not, if not make it in checked state
                        if (menuItem.isChecked()) menuItem.setChecked(false);
                        else menuItem.setChecked(true);

                        //Closing drawer on item click
                        mDrawerLayout.closeDrawers();
                        return true;
                    }
                });
    }

    //decide the action for each option
    public void selectDrawerItem(MenuItem menuItem) {
        // Create a new fragment and specify the fragment to show based on nav item clicked
        switch (menuItem.getItemId()) {
            case R.id.nav_home_fragment:
                changeContainerFragment(RecylerViewFragment.newInstance(this));//reload the viewPager
                break;
        }
        // Close the navigation drawer
    }


    //change the fragment in the container frame layout
    @Override
    public void changeContainerFragment(Fragment fragment) {
        mFragmentManager = getSupportFragmentManager();
        mFragmentManager.beginTransaction().replace(R.id.containerView, fragment).addToBackStack(null).commit();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setToolbarTitle("VMuseum");
    }

    @Override
    public void setToolbarTitle(String title) {
        mToolbar.setTitle(title);

    }
}
