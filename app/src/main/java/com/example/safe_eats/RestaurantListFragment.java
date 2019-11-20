package com.example.safe_eats;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;

import java.util.List;

public class RestaurantListFragment extends Fragment implements RestaurantAdapter.OnRestaurantClickListener {


    RestaurantDataManager manager = MapsActivity.manager;
    RecyclerView rvRestaurant;
    LinearLayoutManager layoutManager;
    public static RestaurantAdapter adapter;

    private List<Restaurant> restaurantsList;


    private int lastVisiblePosition;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_list, container, false);


        restaurantsList = manager.getRestaurants();

        MapsActivity.rest_layout.setVisibility(View.INVISIBLE);
        lastVisiblePosition = 0;


        rvRestaurant = v.findViewById(R.id.rvRestaurant);
        layoutManager = new LinearLayoutManager(v.getContext(), LinearLayoutManager.VERTICAL, false);
        MapsActivity.rest_layout.setVisibility(View.GONE);
        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter = new RestaurantAdapter(restaurantsList, this);
        rvRestaurant.setAdapter(adapter);
        rvRestaurant.setLayoutManager(layoutManager);
        rvRestaurant.addItemDecoration(new DividerItemDecoration(rvRestaurant.getContext(), DividerItemDecoration.VERTICAL));
    }

    @Override
    public void onResume() {
        super.onResume();
        layoutManager.scrollToPosition(lastVisiblePosition);
    }

    @Override
    public void onItemClick(int position) {
        lastVisiblePosition = layoutManager.findFirstCompletelyVisibleItemPosition();

        Restaurant clicked = restaurantsList.get(position);
        Intent intent = new Intent(getActivity(), DetailActivity.class);
        intent.putExtra("Restaurant", (new Gson()).toJson(clicked));
        startActivity(intent);
    }

    private void updateViewFromKeywordSearch(String keyword) {
        restaurantsList = RestaurantDataManager.filterByName(keyword);
        adapter.setList(restaurantsList);
        adapter.notifyDataSetChanged();
    }

    private void resetList() {

        restaurantsList = manager.getRestaurants();

        adapter.setList(restaurantsList);
        adapter.notifyDataSetChanged();
    }
}
