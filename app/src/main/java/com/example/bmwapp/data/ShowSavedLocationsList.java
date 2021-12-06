package com.example.bmwapp.data;

import android.app.Application;
import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bmwapp.R;

import java.util.List;

public class ShowSavedLocationsList extends AppCompatActivity {
    ListView lv_savedLocations;

    //@Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_saved_locations_list);
        lv_savedLocations = findViewById(R.id.lv_waypoints);
        //Application myApplication = (Application) ;
        //MyApplication myApplication = (MyApplication) getApplicationContext();
        //List<Location> savedLocations = MyApplication.getMyLocations();
        lv_savedLocations.setAdapter(new ArrayAdapter<Location>(lv_savedLocations.getContext(), android.R.layout.simple_list_item_1));
    }
}
