package com.example.safe_eats;

import android.os.AsyncTask;
import android.util.JsonReader;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import retrofit2.Retrofit;

public class RestaurantDataManager {


    public RestaurantDataManager() {
        restaurants = new HashMap<String, Restaurant>();
        loadRestaurants();
    }

    public HashMap<String, Restaurant> getRestaurants() {
        return restaurants;
    }

    private HashMap<String, Restaurant> loadRestaurants() {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                // Create URL
                URL restaurantEndpoint = null;
                try {
                    restaurantEndpoint = new URL("https://data.surrey.ca/api/action/datastore_search?resource_id=0e5d04a2-be9b-40fe-8de2-e88362ea916b");
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }

                // Create connection
                HttpsURLConnection connection = null;
                try {
                    connection = (HttpsURLConnection) restaurantEndpoint.openConnection();

                    if (connection.getResponseCode() == 200) {
                        InputStream responseBody = connection.getInputStream();

                        InputStreamReader responseBodyReader =
                                new InputStreamReader(responseBody, "UTF-8");

                        JsonElement rootElem = new JsonParser().parse(responseBodyReader);
                        JsonArray jsonArr = rootElem.getAsJsonObject()
                                .getAsJsonObject("result").getAsJsonArray("records");

                        parseRestaurantJSON(jsonArr);

                        connection.disconnect();
                    } else {
                        // Throw something out the window
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }


            }
        });

        return null;
    }

    private void parseRestaurantJSON(JsonArray jsonArray) throws IOException {
        String name;
        String trackingNumber;
        double latitude;
        double longitude;

        for (final JsonElement objElem : jsonArray) {
            final JsonObject jsonObj = objElem.getAsJsonObject();
            name = jsonObj.get("NAME").getAsString();
            trackingNumber = jsonObj.get("TRACKINGNUMBER").getAsString();
            longitude = jsonObj.get("LATITUDE").getAsDouble();
            latitude = jsonObj.get("LONGITUDE").getAsDouble();

            LatLng location = new LatLng(longitude, latitude);

            Restaurant restaurant = new Restaurant(name, trackingNumber);
            restaurant.setLocation(location);

            restaurants.put(trackingNumber, restaurant);
        }

    }

    private HashMap<String, Restaurant> restaurants;
    private HashMap<String, Inspection> inspections;
}
