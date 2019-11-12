package com.example.safe_eats;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;

import com.google.gson.Gson;

import java.util.List;

public class RestaurantListViewActivity extends AppCompatActivity implements RestaurantAdapter.OnRestaurantClickListener {

    private List<Restaurant> restaurantsList;

    RestaurantDataManager manager;
    RecyclerView rvRestaurant;
    LinearLayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_list_view);

        manager = new RestaurantDataManager();
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        restaurantsList = manager.getRestaurantList();
        rvRestaurant = findViewById(R.id.rvRestaurant);
        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
    }

    @Override
    protected void onStart() {
        super.onStart();

        RestaurantAdapter adapter = new RestaurantAdapter(restaurantsList, this);
        rvRestaurant.setAdapter(adapter);
        rvRestaurant.setLayoutManager(layoutManager);
        rvRestaurant.addItemDecoration(new DividerItemDecoration(rvRestaurant.getContext(), DividerItemDecoration.VERTICAL));
    }

    @Override
    public void onItemClick(int position) {
        Restaurant clicked = restaurantsList.get(position);

        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra("Restaurant", (new Gson()).toJson(clicked));
        startActivity(intent);
    }
}
