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
import java.sql.Date;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import retrofit2.Retrofit;

public class RestaurantDataManager {

    public RestaurantDataManager() {
        restaurants = new HashMap<String, Restaurant>();
        inspections = new ArrayList<Inspection>();
        loadRestaurants();
        loadInspections();
        addInspectionsToRestaurantsWhenDataReady();
    }

    public HashMap<String, Restaurant> getRestaurants() {
        return restaurants;
    }

    private void loadRestaurants() {
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
                restaurantDataLoaded = true;
            }
        });
    }

    private void loadInspections() {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                // Create URL
                URL inspectionEndpoint = null;
                try {
                    inspectionEndpoint = new URL("https://data.surrey.ca/api/action/datastore_search?resource_id=30b38b66-649f-4507-a632-d5f6f5fe87f1&limit=200");
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }

                // Create connection
                HttpsURLConnection connection = null;
                try {
                    connection = (HttpsURLConnection) inspectionEndpoint.openConnection();

                    if (connection.getResponseCode() == 200) {
                        InputStream responseBody = connection.getInputStream();

                        InputStreamReader responseBodyReader =
                                new InputStreamReader(responseBody, "UTF-8");

                        JsonElement rootElem = new JsonParser().parse(responseBodyReader);
                        JsonArray jsonArr = rootElem.getAsJsonObject()
                                .getAsJsonObject("result").getAsJsonArray("records");

                        parseInspectionJSON(jsonArr);

                        connection.disconnect();
                    } else {
                        // Throw something out the window
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                inspectionDataLoaded = true;
            }
        });
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

    private void parseInspectionJSON(JsonArray jsonArray) throws IOException {
        int id;
        int inspectionDate;
        int numCritical;
        int numNonCritical;
        String trackingNumber;
        String description;
        HazardRating hRating;
        InspectionType iType;

        for (final JsonElement objElem : jsonArray) {
            final JsonObject jsonObj = objElem.getAsJsonObject();

            id = jsonObj.get("_id").getAsInt();
            inspectionDate = jsonObj.get("InspectionDate").getAsInt();
            numCritical = jsonObj.get("NumCritical").getAsInt();
            numNonCritical = jsonObj.get("NumNonCritical").getAsInt();
            trackingNumber = jsonObj.get("TrackingNumber").getAsString();
            description = jsonObj.get("ViolLump").getAsString();
            hRating = stringToHazard(jsonObj.get("HazardRating").getAsString());
            iType = stringToInspectionType(jsonObj.get("InspType").getAsString());

            Inspection insp = new Inspection(trackingNumber, id);
            insp.setInspectionDate(new Date(inspectionDate));
            insp.setNumCritical(numCritical);
            insp.setNumNonCritical(numNonCritical);
            insp.setDescription(description);
            insp.setHazardRating(hRating);
            insp.setInspectionType(iType);

            inspections.add(insp);
        }
    }

    private void addInspectionsToRestaurantsWhenDataReady() {

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                Boolean finished = false;
                while (!finished) {

                    // data not ready yet
                    if (!(inspectionDataLoaded && restaurantDataLoaded)) {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        continue; // Start another loop
                    }

                    for (Inspection insp : inspections) {
                        Restaurant rest = restaurants.get(insp.getTrackingNumber());
                        if (rest != null)
                            rest.addInspection(insp);
                    }

                    finished = true; // Stop loop
                }
            }
        });
    }

    private InspectionType stringToInspectionType(String insp) {
        switch (insp) {
            case "Routine":
                return InspectionType.Routine;
            case "Follow-Up":
                return InspectionType.Followup;
            default:
                return null;
        }
    }

    private HazardRating stringToHazard(String haz) {
        switch (haz) {
            case "Low":
                return HazardRating.Low;
            case "Moderate":
                return HazardRating.Medium;
            case "High":
                return HazardRating.High;
            default:
                return null;
        }
    }

    private HashMap<String, Restaurant> restaurants;
    private ArrayList<Inspection> inspections;
    private Boolean restaurantDataLoaded = false;
    private Boolean inspectionDataLoaded = false;
}
