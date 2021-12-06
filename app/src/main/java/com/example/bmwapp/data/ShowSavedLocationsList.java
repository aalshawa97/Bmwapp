package com.example.bmwapp.data;

import android.os.Bundle;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bmwapp.R;

public class ShowSavedLocationsList extends AppCompatActivity {
    ListView lv_savedLocations;

    //@Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_saved_locations_list);
        lv_savedLocations = findViewById(R.id.lv_waypoints);

        //MyApplication myApplication = (MyApplication) getApplicationContext();
    }
}
