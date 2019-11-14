package com.example.safe_eats;

import android.os.AsyncTask;

import com.google.android.gms.maps.model.LatLng;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.net.ssl.HttpsURLConnection;

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

    public List<Restaurant> getRestaurantList() {
        return new ArrayList<>(restaurants.values());
    }

    public List<Restaurant> getRestaurants(final HazardRating recentRating, double distance) {
        List<Object> holder = restaurants.values().stream().filter(new Predicate<Restaurant>() {
            @Override
            public boolean test(Restaurant restaurant) {
                return restaurant.getInspections().get(0).getHazardRating() == recentRating
                        && checkDistance(restaurant, -1);
            }
        }).collect(Collectors.toList());

        ArrayList<Restaurant> returnList = new ArrayList<>();
        for (Object r : holder) {
            returnList.add((Restaurant) r);
        }

        return returnList;
    }

    private Boolean checkDistance(Restaurant restaurant, double distance) {
        // TODO: Check distance
        return true;
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
                    inspectionEndpoint = new URL("https://data.surrey.ca/api/action/datastore_search?resource_id=30b38b66-649f-4507-a632-d5f6f5fe87f1&limit=500");
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

    public List<Restaurant> filterByName(String name) {
        List<Restaurant> filteredList = new ArrayList<>();

        for (Restaurant res : restaurantList) {
            String rName = res.getName().toLowerCase();

            if (rName.contains(name.toLowerCase())) {
                filteredList.add(res);
            }
        }

        return filteredList;
    }

    /**
     * Method to remove unnecessary parts of the name of restaurants
     * ex. 7-Eleven #123 -> 7-Eleven
     *
     * @param name
     * @return
     */
    private String processName(String name) {
        int toRemoveS = 0; // start of to remove char
        int toRemoveE = 0; // end of to remove char
        boolean getRemoveE = false; // start looking for end of remove char

        for (int i = 0; i < name.length(); i++) {
            if (!getRemoveE && name.charAt(i) == '#') {
                toRemoveS = i;
                getRemoveE = true;
            } else if (getRemoveE && (name.charAt(i) == ')' || name.charAt(i) == '(')) {
                toRemoveE = i - 1;
                break;
            }
        }

        if (toRemoveS == 0) {
            return name;
        } else if (toRemoveE == 0) {
            return name.substring(0, toRemoveS);
        } else {
            return name.substring(0, toRemoveS) + name.substring(toRemoveE);
        }
    }

    private void parseRestaurantJSON(JsonArray jsonArray) throws IOException {
        String name;
        String trackingNumber;
        String address;
        String city;
        double latitude;
        double longitude;

        restaurants.clear();

        for (final JsonElement objElem : jsonArray) {
            final JsonObject jsonObj = objElem.getAsJsonObject();
            name = processName(jsonObj.get("NAME").getAsString());
            trackingNumber = jsonObj.get("TRACKINGNUMBER").getAsString();
            address = jsonObj.get("PHYSICALADDRESS").getAsString();
            city = jsonObj.get("PHYSICALCITY").getAsString();
            longitude = jsonObj.get("LATITUDE").getAsDouble();
            latitude = jsonObj.get("LONGITUDE").getAsDouble();
            LatLng location = new LatLng(longitude, latitude);

            Restaurant restaurant = new Restaurant(name, trackingNumber, address, city);
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

        inspections.clear();
        SimpleDateFormat formatDate = new SimpleDateFormat("yyyyMMdd");

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
            insp.setNumCritical(numCritical);
            insp.setNumNonCritical(numNonCritical);
            insp.setDescription(description);
            insp.setHazardRating(hRating);
            insp.setInspectionType(iType);

            try {
                insp.setInspectionDate(formatDate.parse(Integer.toString(inspectionDate)));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            inspections.add(insp);
        }

        inspections.sort(new Comparator<Inspection>() {
            @Override
            public int compare(Inspection o1, Inspection o2) {
                return o1.getInspectionDate().compareTo(o2.getInspectionDate());
            }
        });
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
                return HazardRating.Moderate;
            case "High":
                return HazardRating.High;
            default:
                return null;
        }
    }

    private HashMap<String, Restaurant> restaurants;
    private List<Restaurant> restaurantList;
    private ArrayList<Inspection> inspections;
    private Boolean restaurantDataLoaded = false;
    private Boolean inspectionDataLoaded = false;
}
