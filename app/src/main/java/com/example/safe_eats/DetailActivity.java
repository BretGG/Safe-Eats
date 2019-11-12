package com.example.safe_eats;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;

public class DetailActivity extends AppCompatActivity {

    Restaurant restaurant;

    TextView tvName;
    TextView tvAddress;
    Button btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        String clickedRestaurantJson = getIntent().getStringExtra("Restaurant");
        restaurant = (new Gson()).fromJson(clickedRestaurantJson, Restaurant.class);

        tvName = findViewById(R.id.tvDetailName);
        tvAddress = findViewById(R.id.tvDetailAddress);
        btnBack = findViewById(R.id.btnBackToList);

        tvName.setText(restaurant.getName());
        tvAddress.setText(restaurant.getAddress());

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
