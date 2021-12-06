package com.example.bmwapp;

import android.location.Location;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import java.util.List;

public class MapsActivity extends FragmentActivity /*implements OnMapReadyCallback*/ {
    List<Location> savedLoc;
    //private GoogleMap mMap;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}
