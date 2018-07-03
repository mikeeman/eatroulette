package com.squarepi.eatroulette;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
//import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadLocalRandom;
import java.util.jar.Manifest;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class Slider extends AppCompatActivity {

    private TextView tvBusinessName, tvType, tvAddress, tvDistance, tvPrice;
    private ImageView ivPicture;
    private ExpandableListView elvHours;
    private RatingBar rbRatingBar;

    int expiresIn = -1;
    int maxBusinesses = 0;
    int a;
    int historyIndex = 0;

    Long lCurrentSystemTimeSeconds = System.currentTimeMillis()/1000;

    String response = new String();
    String token    = new String();
    String apiKey   = new String();
    String name     = new String();
    String address  = new String();
    String phone    = new String();
    String price    = new String();
    String photo    = new String();
    String rating   = new String();
    String country  = new String();
    String state    = new String();
    String city     = new String();
    String zip_code = new String();
    String street   = new String();
    String website  = new String();
    String type     = new String();
    String lat      = new String();
    String lon      = new String();

    DBHelper db     = new DBHelper(this);

    NetworkConnectivityHelper nConnHelper = new NetworkConnectivityHelper();

    ArrayList<Cursor> alHistory           = new ArrayList<>();
    ArrayList<Integer> alActiveBusinesses = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i("Slider","onCreate()");
        this.requestWindowFeature(Window.FEATURE_ACTION_BAR);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_slider);

        getPermissions(this);

        if (isConnected()) {
            initControls();
            //applyFonts();
            DBHelper.clearSearchData(db.getWritableDatabase());
            //DBHelper.dropSearchTable(db.getWritableDatabase());

            //TODO remove loadSearchResults() and call async task at app load
            loadSearchResults();

            getRandomizedBusiness();
            updateControls();
            //t = Toast.makeText(this, "Swipe left to see more!", Toast.LENGTH_LONG);
            //t.show();
        } else {
            Log.i("Slider", "No Network connectivity");
        }
    }

    private void getPermissions(Context context) {
        Log.i("Slider", "getPermissions()");
        if (ContextCompat.checkSelfPermission(context, ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            Log.i("Slider", "do not have ACCESS_FINE_LOCATION");
            ActivityCompat.requestPermissions((Activity) context, new String[]{ACCESS_FINE_LOCATION}, a);
        }
        if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_COARSE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            Log.i("Slider", "do not have ACCESS_COURSE_LOCATION");

        }
    }

    private boolean isConnected() {
        Log.i("Slider", "checkNetworkConnectivity()");
        return nConnHelper.checkNetworkConnectivity((ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE));
    }

    private void initControls() {
        Log.i("Slider", "initControls()");

        tvBusinessName = (TextView) findViewById(R.id.tvBusinessName);
        tvType = (TextView) findViewById(R.id.tvType);
        tvAddress = (TextView) findViewById(R.id.tvAddress);
        tvDistance = (TextView) findViewById(R.id.tvDistance);
        tvPrice = (TextView) findViewById(R.id.tvPrice);

        ivPicture = (ImageView) findViewById(R.id.ivPicture);

        elvHours = (ExpandableListView) findViewById(R.id.elvHours);

        rbRatingBar = (RatingBar) findViewById(R.id.rbRating);

        final RelativeLayout background = (RelativeLayout) findViewById(R.id.rlSliderBackground);
        background.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                //toggleSomething();
            }
        });
        background.setOnTouchListener(new OnSwipeTouchListener() {
            public boolean onSwipeTop() {
                //Toast.makeText(MainActivity.this, "top", Toast.LENGTH_SHORT).show();
                Log.i("Slider", "onSwipeTop()");
                if (isConnected()) {
                    Log.i("Slider", "isConnected()");
                    Intent myWebLink = new Intent(android.content.Intent.ACTION_VIEW);
                    myWebLink.setData(Uri.parse(website));
                    startActivity(myWebLink);
                } else {
                    Log.i("Slider", "isNOTConnected()");
                    Toast.makeText(Slider.this, "No Network Connection...", Toast.LENGTH_SHORT).show();
                }

                return true;
            }

            public boolean onSwipeRight() {
                //Toast.makeText(MainActivity.this, "right", Toast.LENGTH_SHORT).show();
                Log.i("Slider", "onSwipeRight()");

                //check if history has more than just current entry
                if (alHistory.size() > 1 && alActiveBusinesses.size() > 1 && historyIndex < alHistory.size()) {
                    for (int i = 0; i < alHistory.size(); i++) {
                        Log.i("alHistory[" + Integer.toString(i) + "]", alHistory.get(i).getString(alHistory.get(i).getColumnIndex(DBHelper.SEARCH_COLUMN_NAME)));
                    }
                    //remove current entry or else will get same business twice
                    //alHistory.remove(alHistory.size() - 1);
                    historyIndex += 1;
                    //something to do with removing duplicates
                    //alActiveBusinesses.remove(alActiveBusinesses.size() - 1);

                    //get last entry
                    updateBusinessLocals(alHistory.get(alHistory.size() - 1 - historyIndex));
                    background.setAnimation(AnimationUtils.loadAnimation(Slider.this, R.anim.lefttoright));
                    //TODO: Implement FragmentPagerAdapter
                    //TODO: https://github.com/codepath/android_guides/wiki/ViewPager-with-FragmentPagerAdapter
                    //TODO: Layout ViewPager
                    updateControls();
                }
                return true;
            }

            public boolean onSwipeLeft() {
                //Toast.makeText(MainActivity.this, "left", Toast.LENGTH_SHORT).show();
                Log.i("Slider", "onSwipeLeft()");

                if (alHistory.size() > 1 && alActiveBusinesses.size() > 1) {
                    for (int i = 0; i < alHistory.size(); i++) {
                        Log.i("alHistory[" + Integer.toString(i) + "]", alHistory.get(i).getString(alHistory.get(i).getColumnIndex(DBHelper.SEARCH_COLUMN_NAME)));
                    }
                }
                //check if user has back swiped and load those businesses first
                if (alHistory.size() > 1 && alActiveBusinesses.size() > 1 && historyIndex > 0) {
                    historyIndex -= 1;
                    updateBusinessLocals(alHistory.get(alHistory.size() - 1 - historyIndex));
                    background.setAnimation(AnimationUtils.loadAnimation(Slider.this, R.anim.righttoleft));
                    updateControls();
                } else {
                    //only load new businesses if we have a connection
                    if (isConnected()) {
                        Log.i("Slider", "isConnected()");
                        getRandomizedBusiness();
                        background.setAnimation(AnimationUtils.loadAnimation(Slider.this, R.anim.righttoleft));
                        updateControls();
                    } else {
                        Log.i("Slider", "isNOTConnected()");
                        Toast.makeText(Slider.this, "No Network Connection...", Toast.LENGTH_SHORT).show();
                    }
                }


                return true;
            }

            public boolean onSwipeBottom() {
                //Toast.makeText(MainActivity.this, "bottom", Toast.LENGTH_SHORT).show();
                return true;
            }
        });
    }

    private void loadSearchResults() {
        Log.i("Slider", "loadSearchResults()");
        try {
            response = new YelpHelper.GetRandomSearchTask(this, maxBusinesses).execute().get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        try {
            // Convert String to json object
            Log.i("response", response);
            JSONObject jsonResponse = new JSONObject(response);
            maxBusinesses = jsonResponse.getInt(YelpHelper.RESPONSE_TOTAL_BUSINESSES);

            JSONArray businessList = new JSONArray((jsonResponse.getString(YelpHelper.RESPONSE_BUSINESSES)));
            for (int i = 0; i < businessList.length(); i++) {
                JSONObject business = businessList.getJSONObject(i);
                ArrayList<String> dbCurrentBusinesses = db.getAllSearch();
                //Check if current business does not exist in db
                if (!dbCurrentBusinesses.contains(business.getString(YelpHelper.RESPONSE_NAME))) {
                    //Get location object for address info
                    JSONObject location   = business.getJSONObject(YelpHelper.RESPONSE_LOCATION);
                    JSONArray category    = business.getJSONArray(YelpHelper.RESPONSE_TYPE);
                    //Get first item in array
                    JSONObject categories = category.getJSONObject(0);
                    JSONObject coordinates = business.getJSONObject(YelpHelper.RESPONSE_COORDINATES);
                    //Check if current business has all entries and is not closed
                    if (business.has(YelpHelper.RESPONSE_PHOTO) && !business.isNull(YelpHelper.RESPONSE_PHOTO) &&
                        business.has(YelpHelper.RESPONSE_NAME) && !business.isNull(YelpHelper.RESPONSE_NAME) &&
                        business.has(YelpHelper.RESPONSE_PRICE) && !business.isNull(YelpHelper.RESPONSE_PRICE) &&
                        location.has(YelpHelper.RESPONSE_ADDRESS_CITY) && !location.isNull(YelpHelper.RESPONSE_ADDRESS_CITY) &&
                        location.has(YelpHelper.RESPONSE_ADDRESS_STREET) && !location.isNull(YelpHelper.RESPONSE_ADDRESS_STREET) &&
                        location.has(YelpHelper.RESPONSE_ADDRESS_STATE) && !location.isNull(YelpHelper.RESPONSE_ADDRESS_STATE) &&
                        //location.getString(YelpHelper.RESPONSE_ADDRESS_ZIP_CODE).length() > 0 &&
                        business.has(YelpHelper.RESPONSE_IS_CLOSED) && business.getString(YelpHelper.RESPONSE_IS_CLOSED) == "false" &&
                        business.has(YelpHelper.RESPONSE_RATING) && !business.isNull(YelpHelper.RESPONSE_RATING) &&
                        business.has(YelpHelper.RESPONSE_WEBSITE) && !business.isNull(YelpHelper.RESPONSE_WEBSITE) &&
                        categories.has(YelpHelper.RESPONSE_TYPE_NAME) && !categories.isNull(YelpHelper.RESPONSE_TYPE_NAME) &&
                        coordinates.has(YelpHelper.RESPONSE_LATITUDE) && !coordinates.isNull(YelpHelper.RESPONSE_LATITUDE) &&
                        coordinates.has(YelpHelper.RESPONSE_LONGITUDE) && !coordinates.isNull(YelpHelper.RESPONSE_LONGITUDE)) {
                            db.insertSearch(business);
                            Log.i("dbCurrentBusinesses", "Adding to db: " + business.getString(YelpHelper.RESPONSE_NAME));
                    } else {
                        Log.i("dbCurrentBusinesses", business.getString(YelpHelper.RESPONSE_NAME) + " is missing information...");
                    }
                } else {
                    Log.i("dbCurrentBusinesses", business.getString(YelpHelper.RESPONSE_NAME) + " already exists!");
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void getRandomizedBusiness() {
        Log.i("Slider", "getRandomizedBusiness()");
        Cursor res;
        updateLocation();

        int numberOfBusinesses = db.numberOfRows(DBHelper.SEARCH_TABLE_NAME);
        Log.i("numberOfBusinesses", Integer.toString(numberOfBusinesses));

        //verify random business is not a duplicate
        int currentBusinessID = -1;

        if (alActiveBusinesses.isEmpty()){
            currentBusinessID = ThreadLocalRandom.current().nextInt(1, numberOfBusinesses);
        } else {
            boolean exists = true;
            while (exists && alActiveBusinesses.size() < numberOfBusinesses - 1 ) {
                currentBusinessID = ThreadLocalRandom.current().nextInt(1, numberOfBusinesses);
                if (!alActiveBusinesses.contains(currentBusinessID)) {
                    exists = false;
                }
            }
        }

        //check if we need to load more businesses
        //if (db.getAllSearch().size() - alActiveBusinesses.size() == 3){
        //    new DownloadSearchResults().execute();
        //}

        Log.i("currentBusinessID", Integer.toString(currentBusinessID));
        if (currentBusinessID != -1){
            res = db.getSearchById(currentBusinessID);
            alHistory.add(res);
            alActiveBusinesses.add(currentBusinessID);
            updateBusinessLocals(res);
        } else {
            //Toast.makeText(this, "Loading more businesses...", Toast.LENGTH_LONG).show();
            //new DownloadSearchResults().execute();
            loadSearchResults();
            getRandomizedBusiness();
        }


        //Get list of all possible business ID's within max range specified

        //Filter list by user rules

        //Update sqlite db
    }

    private void updateBusinessLocals(Cursor res) {
        Log.i("Slider", "updateBusinessLocals(Cursor res)");
        if (res != null) {
            if (res.moveToFirst()) {
                //do {
                name     = res.getString(res.getColumnIndex(DBHelper.SEARCH_COLUMN_NAME));
                photo    = res.getString(res.getColumnIndex(DBHelper.SEARCH_COLUMN_PHOTO));
                rating   = res.getString(res.getColumnIndex(DBHelper.SEARCH_COLUMN_RATING));
                phone    = res.getString(res.getColumnIndex(DBHelper.SEARCH_COLUMN_PHONE));
                price    = res.getString(res.getColumnIndex(DBHelper.SEARCH_COLUMN_PRICE));
                website  = res.getString(res.getColumnIndex(DBHelper.SEARCH_COLUMN_WEBSITE));
                //country  = res.getString(res.getColumnIndex(DBHelper.SEARCH_COLUMN_ADDRESS_COUNTRY));
                state    = res.getString(res.getColumnIndex(DBHelper.SEARCH_COLUMN_ADDRESS_STATE));
                city     = res.getString(res.getColumnIndex(DBHelper.SEARCH_COLUMN_ADDRESS_CITY));
                //zip_code = res.getString(res.getColumnIndex(DBHelper.SEARCH_COLUMN_ADDRESS_ZIP_CODE));
                street   = res.getString(res.getColumnIndex(DBHelper.SEARCH_COLUMN_ADDRESS_STREET));
                address  = street + ", " + city + ", " + state;
                type     = res.getString(res.getColumnIndex(DBHelper.SEARCH_COLUMN_TYPE));
                lat      = res.getString(res.getColumnIndex(DBHelper.SEARCH_COLUMN_LATITUDE));
                lon      = res.getString(res.getColumnIndex(DBHelper.SEARCH_COLUMN_LONGITUDE));
                Log.i("name", name);
                /*Log.i("photo", photo);
                Log.i("rating", phone);
                Log.i("phone", price);
                Log.i("price", website);
                Log.i("website", country);
                Log.i("state", state);
                Log.i("city", city);
                Log.i("zip_code", zip_code);
                Log.i("street", street);
                */Log.i("address", address);
                Log.i("lat", lat);
                Log.i("lon", lon);
                //} while (res.moveToNext());
            }
        }
    }

    private void updateControls() {
        Log.i("Slider", "updateControls()");
        //Read SQLITE db and query for BUSINESS_ID provided
        CharSequence csBusinessName, csAddress, csType, csDistance, csPrice;
        Uri uImageUri;
        float fNumStars;

        /*csBusinessName = "Princess Cafe";
        csAddress = "4 Princess Street, Waterloo, ON";
        csDescription = "A charming minimalistic cafe epitomizing the heart of uptown waterloo.";
        uImageUri = Uri.parse("/sdcard/DCIM/Camera/IMG_20160922_020412.jpg");
        fNumStars = 4.1f;
*/
        csBusinessName  = name;
        csAddress       = address;
        csType          = type;
        csDistance      = getDistance(lat, lon);
        csPrice         = price;
        //uImageUri = Uri.parse("/sdcard/DCIM/Camera/IMG_20160922_020412.jpg");

        //uImageUri = Uri.parse(photo);


        fNumStars = Float.parseFloat(rating);

        new DownloadImageTask(ivPicture).execute(photo);



        tvBusinessName.setText(csBusinessName);
        rbRatingBar.setRating(fNumStars);
        tvAddress.setText(csAddress);
        tvType.setText(csType);
        tvDistance.setText(csDistance);
        tvPrice.setText(csPrice);

        //ivPicture.setImageBitmap();
        //ivPicture.setMaxWidth(100);
        //ivPicture.setMinimumWidth(100);
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        int height = metrics.heightPixels;
        int width = metrics.widthPixels;

        //TODO: set image height to constant
        ivPicture.setMaxHeight(height/2);
        //ivPicture.setMinHeight(height/2);
        ivPicture.setMinimumWidth(width);

        //if (t != null){
        //    t.cancel();
        //}
        //elvHours.set
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }

    public class DownloadSearchResults extends AsyncTask<Void, Void, Void> {

        public void DownloadImageTask() {
        }

        protected Void doInBackground(Void... notNeeded) {
            loadSearchResults();
            return null;
        }

        protected void onPostExecute() {
        }
    }

    private void updateLocation(){
        //check if location change greater than distance threshold
        Log.i("Slider","updateLocation()");
        if (YelpHelper.getOldLocationLat() != null && YelpHelper.getOldLocationLon() != null){
            float[] distance = new float[1];
            Location.distanceBetween(
                    Double.valueOf(YelpHelper.getOldLocationLat()),
                    Double.valueOf(YelpHelper.getOldLocationLon()),
                    Double.valueOf(YelpHelper.getCurrentLocationLat()),
                    Double.valueOf(YelpHelper.getCurrentLocationLon()),
                    distance);
            Log.i("distance", Float.toString(distance[0]));
            if (distance[0] > 100){
                Log.i("User has moved", Float.toString(distance[0]) + "m");
                YelpHelper.setOldLocation();
                //clear old results
                DBHelper.clearSearchData(db.getWritableDatabase());
                //get new results
                loadSearchResults();
            }
        } else {
            YelpHelper.setOldLocation();
        }
    }

    private CharSequence getDistance(CharSequence lat, CharSequence lon){
        Log.i("Slider","getDistance(" + lat + ", " + lon + ")");

        CharSequence ret = "...";

        String latitude     = lat.toString();
        String longitude    = lon.toString();

        String currentLat   = YelpHelper.getCurrentLocationLat();
        String currentLon   = YelpHelper.getCurrentLocationLon();

        try {
            ret = new GoogleHelper.GetDistanceTask(currentLat, currentLon, latitude, longitude).execute().get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return ret;
    }

    private CharSequence getFlyOverDistance(CharSequence lat, CharSequence lon){
        Log.i("Slider","getFlyOverDistance(" + lat + ", " + lon + ")");

        CharSequence ret;

        String latitude     = lat.toString();
        String longitude    = lon.toString();

        String currentLat   = YelpHelper.getCurrentLocationLat();
        String currentLon   = YelpHelper.getCurrentLocationLon();

        double lat1 = Double.parseDouble(currentLat);
        double lat2 = Double.parseDouble(latitude);

        double lon1 = Double.parseDouble(currentLon);
        double lon2 = Double.parseDouble(longitude);

        Log.i("lat1", String.valueOf(lat1));
        Log.i("lat2", String.valueOf(lat2));
        Log.i("lon1", String.valueOf(lon1));
        Log.i("lon2", String.valueOf(lon2));

        //Latitude: 1 deg = 110.574 km
        //Longitude: 1 deg = 111.320*cos(latitude) km

        double diffLat = Math.abs(lat2 - lat1);
        double diffLon = Math.abs(lon2 - lon1);

        Log.i("diffLat", String.valueOf(diffLat));
        Log.i("diffLon", String.valueOf(diffLon));

        double diffLatKm = diffLat * 110.574;
        double diffLonKm = Math.cos((diffLon * Math.PI) / 180) * 111.320 * diffLon;

        Log.i("diffLatKm", String.valueOf(diffLatKm));
        Log.i("diffLonKm", String.valueOf(diffLonKm));

        double distance = Math.sqrt(Math.pow(diffLatKm, 2) + Math.pow(diffLonKm, 2));
        Log.i("distance", String.valueOf(distance));

        int scale = (int) Math.pow(10, 1);
        double roundedDistance = (double) Math.round(distance * scale) / scale;
        Log.i("roundedDistance", String.valueOf(roundedDistance));

        ret = String.valueOf(roundedDistance) + " km";

        return ret;
    }

}
