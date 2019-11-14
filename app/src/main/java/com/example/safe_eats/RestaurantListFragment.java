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
import android.widget.Button;
import android.widget.EditText;

import com.google.gson.Gson;

import java.util.List;

public class RestaurantListFragment extends Fragment implements RestaurantAdapter.OnRestaurantClickListener {

    private List<Restaurant> restaurantsList;

    RestaurantDataManager manager;
    RecyclerView rvRestaurant;
    LinearLayoutManager layoutManager;
    RestaurantAdapter adapter;

    EditText etSearchKeyword;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_list, container, false);

        // TODO Replace commented section with the actual keyword search EditText and Button widget
//        etSearchKeyword = v.findViewById(R.id.etKeywordSearch);
//        Button btnSearch = v.findViewById(R.id.btnKeywordSearch);

        manager = new RestaurantDataManager();
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        restaurantsList = manager.getRestaurantList();
        rvRestaurant = v.findViewById(R.id.rvRestaurant);
        layoutManager = new LinearLayoutManager(v.getContext(), LinearLayoutManager.VERTICAL, false);

//        btnSearch.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                String keyword = etSearchKeyword.getText().toString().trim();
//
//                if (!keyword.isEmpty()) {
//                    updateViewFromKeywordSearch(keyword);
//                } else {
//                    resetList();
//                }
//
//                etSearchKeyword.setText("");
//            }
//        });

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
    public void onItemClick(int position) {
        Restaurant clicked = restaurantsList.get(position);

        Intent intent = new Intent(getActivity(), DetailActivity.class);
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
