package com.example.bmwapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bmwapp.data.LocationPojo;
import com.example.bmwapp.data.remote.ApiClient;
import com.example.bmwapp.data.remote.ApiInterface;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONException;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Executor;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    public static final int DEFAULT_UPGRADE_INTERVAL = 30;
    public static final int FAST_UPDATE_INTERVAL = 5;
    private static final int PERMISSIONS_FINE_LOCATION = 99;
    TextView tv_lat, tv_lon, tv_altitude, tv_accuracy, tv_speed, tv_sensor, tv_updates, tv_address;
    Switch sw_locationUpdates, sw_gps;
    FusedLocationProviderClient fusedLocationProviderClient;

    //Variable to remember if we are tracking location or not.
    boolean updateOn = false;

    static  String TAG = MainActivity.class.getSimpleName();
    //http://localsearch.azurewebsites.net/api/Locations
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        //Give each UI variable a value
        tv_lat = findViewById(R.id.tv_lat);
        tv_lon = findViewById(R.id.tv_lon);
        tv_altitude = findViewById(R.id.tv_altitude);
        tv_accuracy = findViewById(R.id.tv_accuracy);
        tv_speed = findViewById(R.id.tv_speed);
        tv_sensor = findViewById(R.id.tv_sensor);
        tv_updates = findViewById(R.id.tv_updates);
        tv_address = findViewById(R.id.tv_address);
        sw_gps = findViewById(R.id.sw_gps);
        sw_locationUpdates = findViewById(R.id.sw_locationsupdates);

        //Set all properties of LocationRequest
        LocationRequest locationRequest = new LocationRequest();

        //How ofter does the default location check occur?
        locationRequest.setInterval(1000 * 30);
        //How often does the location check occur when set to the most frequent update?
        locationRequest.setFastestInterval(1000 * 5);

        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        sw_gps.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                 if(sw_gps.isChecked())
                 {
                     //Most accurate - use GPS
                     locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                     tv_sensor.setText("Using GPS sensors");
                 }
                 else
                 {  locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
                    tv_sensor.setText("Using towers + WIFI");
                 }
            }
        });

        try {
            MyTask.getJSONObjectFromURL("https://localsearch.azurewebsites.net/api/Locations");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
    //End onCreate method
    private void updateGPS(){
        //Get permissions from the user to track GPS
        //Get the current location from the fused client
        //Update the UI - i.e. set all properties in their associated text view items.
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(MainActivity.this);
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            //User provided the permission
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener((Executor) this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    //We got permissions. Put the values of location. XXX into the UI components

                }
            });
        }
        else
        {
            //Permissions not granted yet.
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_FINE_LOCATION);
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        getJson();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case PERMISSIONS_FINE_LOCATION:
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    updateGPS();
                }
                else{
                    Toast.makeText(MainActivity.this, "This app requires permission to be granted to work properly", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + requestCode);
        }
    }

    private void getJson() {
        ApiInterface service = ApiClient.getClient().create(ApiInterface.class);

        service.getLocations();

        Call<List<LocationPojo>> call = service.getLocations();
        call.enqueue(new Callback<List<LocationPojo>>() {
            @Override
            public void onResponse(Call<List<LocationPojo>> call, Response<List<LocationPojo>> response) {
                Log.i(TAG,response.body().get(0).getName());
            }

            @Override
            public void onFailure(Call<List<LocationPojo>> call, Throwable t) {

            }
        });
    }
}
