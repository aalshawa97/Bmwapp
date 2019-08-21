package com.example.bmwapp.data;

import com.google.gson.annotations.SerializedName;

public class LocationPojo {
    @SerializedName("ID") //get the value from the key ID in json
    int id;
    @SerializedName("Name")
    String name;
    @SerializedName("Latitude")
    Double latitude;
    @SerializedName("Longitude")
    Double longitude;
    @SerializedName("Address")
    String Address;
    @SerializedName("ArrivalTime")
    String arrivalTime;

    public LocationPojo(int id, String name, Double latitude, Double longitude, String address, String arrivalTime) {
        this.id = id;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        Address = address;
        this.arrivalTime = arrivalTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public String getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(String arrivalTime) {
        this.arrivalTime = arrivalTime;
    }
}
