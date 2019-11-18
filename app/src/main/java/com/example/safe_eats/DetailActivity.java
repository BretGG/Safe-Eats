package com.example.safe_eats;

import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

public class DetailActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    Restaurant restaurant;

    TextView tvName;
    TextView tvAddress;
    TextView tvType;
    TextView tvCrit;
    TextView tvNonCrit;
    TextView tvDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String clickedRestaurantJson = getIntent().getStringExtra("Restaurant");
        restaurant = (new Gson()).fromJson(clickedRestaurantJson, Restaurant.class);

        if (restaurant.getInspections().isEmpty()) {
            Toast.makeText(this, "Restaurant have no inspection records.", Toast.LENGTH_LONG).show();
            finish();
        }

        Spinner spinner = findViewById(R.id.spinner);

        InspectionSpinnerAdapter adapter = new InspectionSpinnerAdapter(getApplicationContext(), restaurant.getInspections());

        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();

        tvName = findViewById(R.id.tvDetailName);
        tvAddress = findViewById(R.id.tvDetailAddress);
        tvType = findViewById(R.id.tvDetailType);
        tvCrit = findViewById(R.id.tvDetailCritNum);
        tvNonCrit = findViewById(R.id.tvDetailNonCritNum);
        tvDescription = findViewById(R.id.tvDetailDescription);

        tvDescription.setMovementMethod(new ScrollingMovementMethod());

        tvName.setText(restaurant.getName());
        tvAddress.setText(restaurant.getAddress());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return true;
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        Inspection selectedInspection = restaurant.getInspections().get(i);

        tvType.setText(selectedInspection.getInspectionType().toString());
        tvCrit.setText(String.valueOf(selectedInspection.getNumCritical()));
        tvNonCrit.setText(String.valueOf(selectedInspection.getNumNonCritical()));

        if (selectedInspection.getDescription().equals("")) {
            tvDescription.setText(R.string.no_description);
        } else {
            tvDescription.setText(
                    getReadableDescription(selectedInspection.getDescription()));
        }

        tvDescription.scrollTo(0, 0);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    public String getReadableDescription(String desc) {

        String result = "";
        int startOfLine = 0;
        int endOfLine = 0;

        for (int i = 0; i < desc.length(); i++) {

            if (desc.charAt(i) == '[') {
                endOfLine = i;
            } else if (desc.charAt(i) == '|') {
                result += desc.substring(startOfLine, endOfLine);
                result += "\n\n";

                startOfLine = i + 1;
            } else if (i == desc.length() - 1) {
                result += desc.substring(startOfLine, endOfLine);
            }
        }

        return result;
    }
}
