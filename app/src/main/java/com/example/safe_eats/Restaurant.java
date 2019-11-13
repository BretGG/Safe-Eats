package com.example.safe_eats;


import com.google.android.gms.maps.model.LatLng;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Restaurant {
    public Restaurant(String name, String trackingNumber) {
        setName(name);
        setTrackingNumber(trackingNumber);
        this.inspections = new ArrayList<Inspection>();
    }

    private LatLng location;
    @SerializedName("NAME")
    private String name;
    @SerializedName("TRACKINGNUMBER")
    private String trackingNumber;
    @SerializedName("CITY")
    private String city;
    @SerializedName("ADDRESS")
    private String address;

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
