package com.example.safe_eats;


import com.google.android.gms.maps.model.LatLng;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Date;

public class Restaurant {
    public Restaurant(String name, String trackingNumber, String address, String city) {
        setName(name);
        setTrackingNumber(trackingNumber);
        this.inspections = new ArrayList<Inspection>();
        this.address = address;
        this.city = city;
    }

    private LatLng location;
    @SerializedName("NAME")
    private String name;
    @SerializedName("TRACKINGNUMBER")
    private String trackingNumber;
    @SerializedName("ADDRESS")
    private String address;
    @SerializedName("CITY")
    private String city;

    Inspection getLatestInspection() {

        if (inspections.isEmpty()) {
            return null;
        }

        Inspection latestInspection = inspections.get(0);

        for (Inspection inspection : inspections) {
            Date date = inspection.getInspectionDate();

            if (date.compareTo(latestInspection.getInspectionDate()) > 0) {
                latestInspection = inspection;
            }
        }

        return latestInspection;
    }

    private ArrayList<Inspection> inspections;

    public LatLng getLocation() {
        return location;
    }

    public void setLocation(LatLng location) {
        this.location = location;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTrackingNumber() {
        return trackingNumber;
    }


    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public ArrayList<Inspection> getInspections() {
        return inspections;
    }

    public void addInspection(Inspection inspection) {
        this.inspections.add(inspection);
    }

    private void setTrackingNumber(String trackingNumber) {
        this.trackingNumber = trackingNumber;
    }
}
