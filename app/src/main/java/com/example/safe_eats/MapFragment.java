package com.example.safe_eats;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;

import java.util.List;

public class MapFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 100;
    private boolean mLocationPermissionGranted = false;
    static SupportMapFragment mapfragment;


    public static GoogleMap mMap;
    RestaurantDataManager manager = MapsActivity.manager;
    Restaurant clickedRestaurant;

    public LatLng surreyCentral = new LatLng(49.1896, -122.8479);
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View v =  inflater.inflate(R.layout.fragment_map, container, false);
        mapfragment = (SupportMapFragment)getChildFragmentManager().findFragmentById(R.id.map);
        mapfragment.getMapAsync(this);

        MapsActivity.rest_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), DetailActivity.class);
                intent.putExtra("Restaurant", (new Gson()).toJson(clickedRestaurant));
                startActivity(intent);
            }
        });

        return v;
    }

    @Override
    public void onMapReady (GoogleMap googleMap){
        mMap = googleMap;
        getLocationPermission();


        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(surreyCentral,15));
        updateLocationUI();
//        List<Restaurant> restaurants = manager.getRestaurants(HazardRating.High, 10000, surreyCentral);
//        if (!getArguments().getParcelableArrayList("restaurants").isEmpty()){
//            List<Restaurant> restaurants = getArguments().getParcelableArrayList("restaurants");
//        }
        List<Restaurant> restaurants = manager.getRestaurants(RestaurantDataManager.filter.distance, surreyCentral);

        for (Restaurant holder : restaurants) {
            Marker m = mMap.addMarker(new MarkerOptions()
                    .position(holder.getLocation()).title(holder.getName()));
            m.setTag(holder);
        }
        mMap.setOnMarkerClickListener(this);
    }

    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */

        if (ContextCompat.checkSelfPermission(getActivity().getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
            Log.d("Tag", "WTFFFFFFFFFFFFFFFFFFFFFFf");
        } else {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        Log.d("Tag", "FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFffffucka");
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
            }
        }
        updateLocationUI();
    }

    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }
        try {
            if (mLocationPermissionGranted) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                getLocationPermission();
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        MapsActivity.rest_layout.setVisibility(View.VISIBLE);
        Restaurant restaurant= (Restaurant) marker.getTag();
        MapsActivity.rest_title.setText(marker.getTitle());
        MapsActivity.rest_address.setText(restaurant.getAddress());
        if(restaurant.getInspections().size() != 0){
            MapsActivity.rest_rating.setText(RestaurantDataManager
                    .convertRating(restaurant.getInspections()
                            .get(0).getHazardRating()));

        }
        return false;
    }
    public GoogleMap getmMap() {
        return mMap;
    }
}

