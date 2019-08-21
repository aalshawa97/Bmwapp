package com.example.bmwapp.data.remote;

import com.example.bmwapp.data.LocationPojo;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiInterface {

    @GET("api/locations")
    Call<List<LocationPojo>> getLocations();
}
