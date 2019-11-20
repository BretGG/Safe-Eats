package com.example.safe_eats;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

public class filterDialog extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View layout = inflater.inflate(R.layout.filter_layout, null);
        final Spinner spinner = (Spinner) layout.findViewById(R.id.rating_spinner);
        final SeekBar seekBar = (SeekBar) layout.findViewById(R.id.distance_bar);
        builder.setView(layout)
                .setPositiveButton(R.string.apply, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        double[] latlng = getArguments().getDoubleArray("latlng");
                        LatLng currentLoc = new LatLng(latlng[0], latlng[1]);
                        List<Restaurant> restaurants;
                        if(spinner.getSelectedItem().equals("All")){
                            restaurants = RestaurantDataManager.getRestaurants(
                                    seekBar.getProgress()*1000,  currentLoc);
                        } else{
                            restaurants = RestaurantDataManager.getRestaurants(
                                    RestaurantDataManager.convertRatingString(spinner.getSelectedItem().toString()),
                                    seekBar.getProgress()*1000,
                                    currentLoc);
                        }
                        if(MapFragment.mapfragment.isVisible()){
                            MapFragment.mMap.clear();
                            for (Restaurant holder : restaurants) {
                                Marker m = MapFragment.mMap.addMarker(new MarkerOptions()
                                        .position(holder.getLocation()).title(holder.getName()));
                                m.setTag(holder);
                            }
                        }else{
                            RestaurantListFragment.adapter.setList(restaurants);
                            RestaurantListFragment.adapter.notifyDataSetChanged();
                        }

                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });

        AlertDialog alertDialog = builder.create();


        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(), R.array.rating_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        final TextView progressContainer = layout.findViewById(R.id.progress);
        SeekBar.OnSeekBarChangeListener seekBarListener = new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }


            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onProgressChanged(SeekBar seekBark, int progress, boolean fromUser) {
                progressContainer.setText(Integer.toString(progress) + "KM");
            }
        };
        seekBar.setOnSeekBarChangeListener(seekBarListener);
        // Create the AlertDialog object and return it
        return alertDialog;
    }
}
