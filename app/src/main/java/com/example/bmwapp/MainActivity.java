package com.example.bmwapp;

import static java.lang.String.valueOf;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.PendingIntent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
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
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
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
    public LocationCallback locationCallBack;
    public LocationRequest locationRequest;
    public PendingIntent pendingIntent;
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

        //Event that is triggered whenever the update interval is met
        locationCallBack = new LocationCallback() {
            @Override
            public void onLocationAvailability(@NonNull LocationAvailability locationAvailability) {
                super.onLocationAvailability(locationAvailability);
            }

            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);

                //Save the location
                Location location = locationResult.getLastLocation();
                updateUIValue(locationResult.getLastLocation());
            }
        };

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

        sw_locationUpdates.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                //Turn on location tracking
                if(sw_locationUpdates.isChecked()){
                    startLocationUpdates();
                }
                else
                {
                    //Turn off tracking
                    stopLocationUpdates();
                }
            }
        });

        updateGPS();
        try {
            MyTask.getJSONObjectFromURL("https://localsearch.azurewebsites.net/api/Locations");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void startLocationUpdates() {
        tv_updates.setText("Location is being tracked.");
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallBack, null);
        updateGPS();
    }

    private void stopLocationUpdates() {
        tv_updates.setText("Location is not being tracked.");
        tv_lat.setText("Location is not being tracked.");
        tv_lon.setText("Location is not being tracked.");
        tv_speed.setText("Location is not being tracked.");
        tv_address.setText("Location is not being tracked.");
        tv_altitude.setText("Location is not being tracked.");
        tv_sensor.setText("Location is not being tracked.");
        fusedLocationProviderClient.requestLocationUpdates(null, locationCallBack, null);

    }

    //End onCreate method
    private void updateGPS(){
        //Get permissions from the user to track GPS
        //Get the current location from the fused client
        //Update the UI - i.e. set all properties in their associated text view items.
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(MainActivity.this);
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            //User provided the permission
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    //We got permissions. Put the values of location. XXX into the UI components
                    updateUIValue(location);
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

    private void updateUIValue(Location location){
        //Update all of the text view objects with a new location.
        try{
            tv_lat.setText(String.valueOf(location.getLatitude()));
            tv_lon.setText(String.valueOf(location.getLongitude()));
            tv_accuracy.setText(String.valueOf(location.getAccuracy()));
            if(location.hasAltitude()){
                tv_altitude.setText(String.valueOf(location.getAltitude()));
            }
            else{
                tv_altitude.setText("Not available");
            }
            if(location.hasSpeed())
            {
                tv_speed.setText(String.valueOf(location.getSpeed()));
            }
            else
            {
                tv_speed.setText("Not available");
            }
            Geocoder geocoder = new Geocoder(MainActivity.this);
            try{
                List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                tv_address.setText(addresses.get(0).getAddressLine(0));
            }
            catch(Exception e){
                tv_address.setText("Unable to get street address");
            }
        }
        catch (Exception e)
        {

        }
    }
}
