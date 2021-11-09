package com.example.bmwapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.example.bmwapp.data.LocationPojo;
import com.example.bmwapp.data.remote.ApiClient;
import com.example.bmwapp.data.remote.ApiInterface;

import org.json.JSONException;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    static  String TAG = MainActivity.class.getSimpleName();
    //http://localsearch.azurewebsites.net/api/Locations
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        try {
            MyTask.getJSONObjectFromURL("https://localsearch.azurewebsites.net/api/Locations");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        getJson();
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
