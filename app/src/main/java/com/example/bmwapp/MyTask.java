package com.example.bmwapp;

import android.app.Activity;
import android.app.MediaRouteButton;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.BreakIterator;

class MyTask extends AsyncTask<Integer, Integer, String> {

    public static JSONObject getJSONObjectFromURL(String urlString) throws IOException, JSONException {

        HttpURLConnection urlConnection = null;

        URL url = new URL(urlString);

        urlConnection = (HttpURLConnection) url.openConnection();

        urlConnection.setRequestMethod("GET");
        urlConnection.setReadTimeout(10000 /* milliseconds */);
        urlConnection.setConnectTimeout(15000 /* milliseconds */);

        urlConnection.setDoOutput(true);

        urlConnection.connect();

        BufferedReader br=new BufferedReader(new InputStreamReader(url.openStream()));

        char[] buffer = new char[1024];

        String jsonString = new String();

        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line+"\n");
        }
        br.close();

        jsonString = sb.toString();

        System.out.println("JSON: " + jsonString);
        urlConnection.disconnect();

        return new JSONObject(jsonString);

    }
    @Override
    protected String doInBackground(Integer... params) {
        Integer count = 0;
        for (; count <= params[0]; count++) {
            try {
                Thread.sleep(1000);

                publishProgress(count);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        try {
            //Calls method to get JSON object
            JSONObject response = getJSONObjectFromURL("http://localsearch.azurewebsites.net/api/Locations");
            Log.d("Get BMW data", "doInBackground: " + response);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "Task Completed.";
    }

    @Override
    protected void onPostExecute(String result) {
        MediaRouteButton progressBar = null;
        progressBar.setVisibility(View.GONE);
    }
    @Override
    protected void onPreExecute() {
        BreakIterator txt = null;
        txt.setText("Task Starting...");
    }
    @Override
    protected void onProgressUpdate(Integer... values) {
        BreakIterator txt = null;
        txt.setText("Running..."+ values[0]);
        Activity progressBar = null;
        progressBar.setProgress(values[0]);
    }
}
