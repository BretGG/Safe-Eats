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

import java.util.HashMap;

public class MapFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 100;
    private boolean mLocationPermissionGranted = false;
    GoogleMap mMap;
    RestaurantDataManager manager = MapsActivity.manager;
    Restaurant clickedRestaurant;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View v =  inflater.inflate(R.layout.fragment_map, container, false);
        SupportMapFragment mapfragment = (SupportMapFragment)getChildFragmentManager().findFragmentById(R.id.map);
        mapfragment.getMapAsync(this);

        MapsActivity.rest_detail.setOnClickListener(new View.OnClickListener() {
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

        LatLng surreyCentral = new LatLng(49.1896, -122.8479);


        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(surreyCentral,15));
        updateLocationUI();
        HashMap<String, Restaurant> restaurants = manager.getRestaurants();

        for (Restaurant holder : restaurants.values()) {
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
        MapsActivity.rest_detail.setVisibility(View.VISIBLE);
        clickedRestaurant = (Restaurant) marker.getTag();
        String content  = marker.getTitle() + '\n' + clickedRestaurant.getAddress();
        if(clickedRestaurant.getInspections().size() != 0){
            content += "" + "\t\t\t" + clickedRestaurant.getInspections().get(0).getHazardRating();
        }
        MapsActivity.rest_detail.setText(content);
        Log.d("Tag", marker.getTitle());
        return false;
    }
}

