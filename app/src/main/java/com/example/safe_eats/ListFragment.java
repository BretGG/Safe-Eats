package com.example.safe_eats;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.SupportMapFragment;

import java.util.List;

public class ListFragment extends Fragment {
    private List<Restaurant> restaurantsList;

    RestaurantDataManager manager = MapsActivity.manager;
    RecyclerView rvRestaurant;
    LinearLayoutManager layoutManager;
    RestaurantAdapter adapter;
    EditText etSearchKeyword;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_list, container, false);
        List<Restaurant>restaurantsList = manager.getRestaurantList();
        rvRestaurant = getActivity().findViewById(R.id.rvRestaurant);

        return root;
    }
}
