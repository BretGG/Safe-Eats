package com.example.safe_eats;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import java.util.List;

public class RestaurantListViewActivity extends AppCompatActivity implements RestaurantAdapter.OnRestaurantClickListener {

    private List<Restaurant> restaurantsList;

    RestaurantDataManager manager;
    RecyclerView rvRestaurant;
    LinearLayoutManager layoutManager;
    RestaurantAdapter adapter;

    EditText etSearchKeyword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_list_view);

        etSearchKeyword = findViewById(R.id.etKeywordSearch);
        Button btnSearch = findViewById(R.id.btnKeywordSearch);

        manager = new RestaurantDataManager();
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        restaurantsList = manager.getRestaurantList();
        rvRestaurant = findViewById(R.id.rvRestaurant);
        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String keyword = etSearchKeyword.getText().toString().trim();

                if (!keyword.isEmpty()) {
                    updateViewFromKeywordSearch(keyword);
                } else {
                    resetList();
                }

                etSearchKeyword.setText("");
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        adapter = new RestaurantAdapter(restaurantsList, this);
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

    private void updateViewFromKeywordSearch(String keyword) {
        restaurantsList = manager.filterByName(keyword);
        adapter.setList(restaurantsList);
        adapter.notifyDataSetChanged();
    }

    private void resetList() {
        restaurantsList = manager.getRestaurantList();
        adapter.setList(restaurantsList);
        adapter.notifyDataSetChanged();
    }
}
