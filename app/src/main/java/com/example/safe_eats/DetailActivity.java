package com.example.safe_eats;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.gson.Gson;

public class DetailActivity extends AppCompatActivity {

    Restaurant restaurant;

    TextView tvName;
    TextView tvAddress;
    TextView tvDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String clickedRestaurantJson = getIntent().getStringExtra("Restaurant");
        restaurant = (new Gson()).fromJson(clickedRestaurantJson, Restaurant.class);

        tvName = findViewById(R.id.tvDetailName);
        tvAddress = findViewById(R.id.tvDetailAddress);
        tvDescription = findViewById(R.id.tvDetailDescription);

        tvName.setText(restaurant.getName());
        tvAddress.setText(restaurant.getAddress());

        tvDescription.setMovementMethod(new ScrollingMovementMethod());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return true;
    }
}
