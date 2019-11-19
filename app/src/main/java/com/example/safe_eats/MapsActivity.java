package com.example.safe_eats;


import androidx.appcompat.app.AppCompatActivity;

import androidx.drawerlayout.widget.DrawerLayout;

import android.app.SearchManager;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.navigation.NavigationView;

import java.util.List;
import java.util.Map;


public class MapsActivity extends AppCompatActivity  {
    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;
    private AppBarConfiguration mAppBarConfiguration;
    static RestaurantDataManager manager;
    static LinearLayout rest_layout;
    static TextView rest_title;
    static TextView rest_address;
    static TextView rest_rating;
    public double[] latlng;
    SearchView searchView;
    NavController navController;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        RestaurantDataManager.initializeRestaurantDataManager();
        RestaurantDataManager.waitForInitialization();
        latlng = new double[]{49.1896, -122.8497};

        setContentView(R.layout.activity_maps);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        rest_layout = findViewById(R.id.rest_layout);
        rest_title = findViewById(R.id.rest_title);
        rest_address = findViewById(R.id.rest_address);
        rest_rating = findViewById(R.id.rest_rating);
        mNavigationView  = findViewById(R.id.nav_view);
        mDrawerLayout = findViewById(R.id.drawer_layout);
        mAppBarConfiguration = new AppBarConfiguration.Builder(R.id.nav_map, R.id.nav_list).setDrawerLayout(mDrawerLayout).build();


        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(mNavigationView, navController);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        MenuItem menuitem = menu.findItem(R.id.action_search);
        // Retrieve the SearchView and plug it into SearchManager
        searchView = (SearchView) menuitem.getActionView();
        searchView.setIconifiedByDefault(true);
        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextChange(String restoName) {
                // your text view here

                return true;
            }

            @Override
            public boolean onQueryTextSubmit(String restoName) {
                if(MapFragment.mapfragment.isVisible()){
                    List<Restaurant> restaurants = RestaurantDataManager.filterByName(restoName);
                    MapFragment.mMap.clear();
                    for (Restaurant holder : restaurants) {
                        Marker m = MapFragment.mMap.addMarker(new MarkerOptions()
                                .position(holder.getLocation()).title(holder.getName()));
                        m.setTag(holder);
                    }
                    MapFragment.mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(restaurants.get(0).getLocation(),15));
                    rest_title.setVisibility(View.VISIBLE);
                    Restaurant restaurant= restaurants.get(0);
                    MapsActivity.rest_title.setText(restaurant.getName());
                    MapsActivity.rest_address.setText(restaurant.getAddress());
                    if(restaurant.getInspections().size() != 0){
                        MapsActivity.rest_rating.setText(RestaurantDataManager
                                .convertRating(restaurant.getInspections()
                                        .get(0).getHazardRating()));
                    }
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                } else {
                    List<Restaurant> restaurants = RestaurantDataManager.filterByName(restoName);
                    RestaurantListFragment.adapter.setList(restaurants);
                    RestaurantListFragment.adapter.notifyDataSetChanged();
                }

                return true;
            }
        });
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.filterDialog:
                DialogFragment dialog = new filterDialog();
                Bundle bundle = new Bundle();
                bundle.putDoubleArray("latlng", latlng);
                dialog.setArguments(bundle);
                dialog.show(getSupportFragmentManager(), "Filter Dialog");
                return true;
            case R.id.action_search:
                searchView.setIconified(false);
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    @Override
    public boolean onSupportNavigateUp() {
//        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}

