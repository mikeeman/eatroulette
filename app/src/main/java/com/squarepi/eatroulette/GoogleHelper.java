package com.squarepi.eatroulette;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by PC on 9/6/2017.
 */

public class GoogleHelper {


    public static class GetDistanceTask extends AsyncTask<Void, Integer, String> {
        String mLat1, mLon1, mLat2, mLon2;

        public GetDistanceTask(String lat1, String lon1, String lat2, String lon2){
            Log.i("GoogleHelper", "GetDistanceTask(" + lat1 + ", " + lon1 + ", " + lat2 + ", " + lon2 + ")");
            mLat1 = lat1;
            mLon1 = lon1;
            mLat2 = lat2;
            mLon2 = lon2;
        }

        protected String doInBackground(Void... v) {
            String distance = GoogleHelper.getDistance(mLat1, mLon1, mLat2, mLon2);
            return distance;
        }

        protected void onProgressUpdate(Integer... progress) {
            //setProgressPercent(progress[0]);
        }

        protected void onPostExecute(String result) {
            //showDialog("Downloaded " + result + " bytes");

        }
    }

    private static String getDistance(String lat1, String lon1, String lat2, String lon2){
        Log.i("GoogleHelper","getDistance(" + lat1 + ", " + lon1 + ", " + lat2 + ", " + lon2 + ")");
        String ret = "-1";
        String res = "";
        /*
        try {
            URL url = new URL("https://maps.googleapis.com/maps/api/distancematrix/json");
            String params = "origins=" + currentLat + "," + currentLon + "&destinations=" + lat + "," + lon + "&key=AIzaSyCHZTNjueKQUmcjCn4wqOJfxJjbO2rAxfU";
            res = YelpHelper.httpGet(url, params);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        try {
            // Convert String to json object
            Log.i("response", res);
            JSONObject jsonResponse = new JSONObject(response);
            JSONArray rows = new JSONArray((jsonResponse.getString("rows")));
            JSONObject row = rows.getJSONObject(0);
            JSONObject elements = row.getJSONObject("elements");
            distance = elements.getString("text");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        */


        try {
            URL url = new URL("https://maps.googleapis.com/maps/api/distancematrix/json");
            String params = "origins=" + lat1 + "," + lon1 + "&destinations=" + lat2 + "," + lon2 + "&key=AIzaSyCHZTNjueKQUmcjCn4wqOJfxJjbO2rAxfU";
            res = YelpHelper.httpGet(url, params);

            ret = httpGet(url, "");
        } catch (MalformedURLException e){
            e.printStackTrace();
        }

        try {
            // Convert String to json object
            Log.i("response", res);
            JSONObject jsonResponse = new JSONObject(res);
            Log.i("jsonResponse", jsonResponse.toString());
            JSONArray rows = new JSONArray((jsonResponse.getString("rows")));
            Log.i("rows", rows.toString());
            JSONObject row = rows.getJSONObject(0);
            Log.i("row", row.toString());
            JSONArray elements = new JSONArray(row.getString("elements"));
            Log.i("elements", elements.toString());
            JSONObject element = elements.getJSONObject(0);
            Log.i("element", element.toString());
            JSONObject distance = element.getJSONObject("distance");
            Log.i("distance", distance.toString());
            ret = distance.getString("text");
            Log.i("ret", ret);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return ret;
    }

    private static String httpGet(URL url, String params){
        Log.i("GoogleHelper", "httpGet(" + url.toString() + "?" + params + ")");
        InputStream is = new InputStream() {
            @Override
            public int read() throws IOException {
                return 0;
            }
        };

        String response = "[]";

        try {
            URL _url = new URL(url.toString() + "?" + params);
            HttpURLConnection urlConn =(HttpURLConnection)_url.openConnection();
            urlConn.setRequestMethod("GET");
            urlConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");


            if(urlConn.getResponseCode() == HttpURLConnection.HTTP_OK){
                is = urlConn.getInputStream();// is is inputstream
            } else {
                is = urlConn.getErrorStream();
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            is.close();
            response = sb.toString();
            Log.e("JSON", response);
        } catch (Exception e) {
            Log.e("Buffer Error", "Error converting result " + e.toString());
        }

        return response ;
    }

}
