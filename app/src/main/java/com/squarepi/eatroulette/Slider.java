package com.squarepi.eatroulette;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadLocalRandom;

import static android.support.v7.appcompat.R.attr.background;

/**
 * Created by PC on 9/22/2016.
 */

public class Slider extends AppCompatActivity {
    private TextView tvBusinessName, tvDescription, tvAddress;
    private ImageView ivPicture;
    private ExpandableListView elvHours;
    private RatingBar rbRatingBar;

    int expiresIn = -1;
    int maxBusinesses = 0;

    Long lCurrentSystemTimeSeconds = System.currentTimeMillis()/1000;

    String response = new String();
    String token    = new String();
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

    DBHelper db     = new DBHelper(this);

    ArrayList<Cursor> alHistory = new ArrayList<>();
    ArrayList<Integer> alActiveBusinesses = new ArrayList<>();

    static Toast t;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i("Slider","onCreate");
        this.requestWindowFeature(Window.FEATURE_ACTION_BAR);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_slider);
        initControls();
        initSearchApi();
        DBHelper.clearSearchData(db.getWritableDatabase());

        //TODO remove loadSearchResults() and call async task at app load
        loadSearchResults();

        //JSONObject business = getRandomizedBusiness();
        //updateBusinessLocals(business);
        getRandomizedBusiness();
        updateControls();
        t = Toast.makeText(this, "Swipe left to see more!", Toast.LENGTH_LONG);
        t.show();
        //Toast.makeText(this, "Swipe left to see more!", Toast.LENGTH_SHORT).show();
    }

    private void initControls() {
        Log.i("Slider","initControls()");
        tvBusinessName = (TextView) findViewById(R.id.tvBusinessName);
        tvDescription = (TextView) findViewById(R.id.tvDescription);
        tvAddress = (TextView) findViewById(R.id.tvAddress);

        ivPicture = (ImageView) findViewById(R.id.ivPicture);

        elvHours = (ExpandableListView) findViewById(R.id.elvHours);

        rbRatingBar = (RatingBar) findViewById(R.id.rbRating);
        RelativeLayout background =(RelativeLayout)findViewById(R.id.rlSliderBackground);
        background.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                //toggleSomething();
            }
        });
        background.setOnTouchListener(new OnSwipeTouchListener() {
            public boolean onSwipeTop() {
                //Toast.makeText(MainActivity.this, "top", Toast.LENGTH_SHORT).show();
                return true;
            }
            public boolean onSwipeRight() {
                //Toast.makeText(MainActivity.this, "right", Toast.LENGTH_SHORT).show();
                Log.i("Slider", "onSwipeRight()");

                //check if history has more than just current entry
                if (alHistory.size() > 1 && alActiveBusinesses.size() > 1){
                    for (int i=0; i<alHistory.size();i++){
                        Log.i("alHistory["+Integer.toString(i) + "]", alHistory.get(i).getString(alHistory.get(i).getColumnIndex(DBHelper.SEARCH_COLUMN_NAME)));
                    }
                    //remove current entry or else will get same business twice
                    alHistory.remove(alHistory.size() - 1);
                    alActiveBusinesses.remove(alActiveBusinesses.size() - 1);
                    //get last entry
                    updateBusinessLocals(alHistory.get(alHistory.size() - 1));
                    updateControls();
                }
                return true;
            }
            public boolean onSwipeLeft() {
                //Toast.makeText(MainActivity.this, "left", Toast.LENGTH_SHORT).show();
                //JSONObject business = getRandomizedBusiness();
                //updateBusinessLocals(business);
                Log.i("Slider", "onSwipeLeft()");
                getRandomizedBusiness();
                updateControls();
                return true;
            }
            public boolean onSwipeBottom() {
               //Toast.makeText(MainActivity.this, "bottom", Toast.LENGTH_SHORT).show();
                return true;
            }
        });

    }

    private void initSearchApi(){
        Log.i("Slider","initSearchApi()");
        //Check if we can use existing token
        if ( db.isValidToken() ) {
            //Can use existing token
            token = db.getToken();
        }
        else{
            //Get new token
            //Clean up any old tokens
            db.deleteSetting(DBHelper.SETTINGS_NAME_TOKEN);
            db.deleteSetting(DBHelper.SETTINGS_NAME_TOKENEXPIRES);
            //Get a new token
            try {
                response = new YelpHelper.GetTokenTask().execute().get();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            try {
                // Convert String to json object
                JSONObject jsonResponse = new JSONObject(response);
                token = jsonResponse.getString(YelpHelper.RESPONSE_ACCESS_TOKEN);
                expiresIn = jsonResponse.getInt(YelpHelper.RESPONSE_EXPIRES_IN);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            //Get new expiry date
            Long lExpiryDateSeconds = lCurrentSystemTimeSeconds + expiresIn;

            //Write new token and expiry to db
            db.insertSetting(DBHelper.SETTINGS_NAME_TOKEN, token);
            db.insertSetting(DBHelper.SETTINGS_NAME_TOKENEXPIRES, lExpiryDateSeconds.toString());
        }

        //Now we have a valid token
        YelpHelper.setCurrentToken(token);
    }

    private void loadSearchResults() {
        //Search everything around current location and load to db
        Log.i("Slider", "loadSearchResults");
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
                if (!dbCurrentBusinesses.contains(business.getString(YelpHelper.RESPONSE_NAME))){
                    db.insertSearch(business);
                } else {
                    Log.i("dbCurrentBusinesses",business.getString(YelpHelper.RESPONSE_NAME) + " already exists!");
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void getRandomizedBusiness() {
        Log.i("Slider", "getRandomizedBusiness");
        //Randomize a business ID following appropriate rules

        //Is it best to load whole JSON Array or just one object at a time?
        //Most likely do just one db read, load everything to memory and then apply filtering
        //Going to just use db as it is and minimize phone memory usage

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
            while (exists == true && alActiveBusinesses.size() < numberOfBusinesses - 1 ) {
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
            Toast.makeText(this, "Loading more businesses...", Toast.LENGTH_LONG).show();
            //new DownloadSearchResults().execute();
            loadSearchResults();
            getRandomizedBusiness();
        }


        //Get list of all possible business ID's within max range specified

        //Filter list by user rules

        //Update sqlite db
    }


    private void updateBusinessLocals(JSONObject business) {
        Log.i("slider", "updateBusinessLocals(business)");
        try {
            name = business.getString(YelpHelper.RESPONSE_NAME);
            photo = business.getString(YelpHelper.RESPONSE_PHOTO);
            rating = business.getString(YelpHelper.RESPONSE_RATING);
            phone = business.getString(YelpHelper.RESPONSE_PHONE);
            price = business.getString(YelpHelper.RESPONSE_PRICE);
            website = business.getString(YelpHelper.RESPONSE_ADDRESS_WEBSITE);

            JSONObject location = new JSONObject(business.getString(YelpHelper.RESPONSE_LOCATION));
            country = location.getString(YelpHelper.RESPONSE_ADDRESS_COUNTRY);
            state = location.getString(YelpHelper.RESPONSE_ADDRESS_STATE);
            city = location.getString(YelpHelper.RESPONSE_ADDRESS_CITY);
            zip_code = location.getString(YelpHelper.RESPONSE_ADDRESS_ZIP_CODE);
            street = location.getString(YelpHelper.RESPONSE_ADDRESS_STREET);

            address = street + ", " + city + ", " + state;
            //address = street + ", " + city + ", " + state;
            //coordinates = "";
            //public static final String RESPONSE_LOCATION            = "coordinates";
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void updateBusinessLocals(Cursor res){
        Log.i("Slider", "updateBusinessLocals(Cursor)");
        if (res != null) {
            if (res.moveToFirst()) {
                //do {
                name     = res.getString(res.getColumnIndex(DBHelper.SEARCH_COLUMN_NAME));
                photo    = res.getString(res.getColumnIndex(DBHelper.SEARCH_COLUMN_PHOTO));
                rating   = res.getString(res.getColumnIndex(DBHelper.SEARCH_COLUMN_RATING));
                phone    = res.getString(res.getColumnIndex(DBHelper.SEARCH_COLUMN_PHONE));
                price    = res.getString(res.getColumnIndex(DBHelper.SEARCH_COLUMN_PRICE));
                website  = res.getString(res.getColumnIndex(DBHelper.SEARCH_COLUMN_WEBSITE));
                country  = res.getString(res.getColumnIndex(DBHelper.SEARCH_COLUMN_ADDRESS_COUNTRY));
                state    = res.getString(res.getColumnIndex(DBHelper.SEARCH_COLUMN_ADDRESS_STATE));
                city     = res.getString(res.getColumnIndex(DBHelper.SEARCH_COLUMN_ADDRESS_CITY));
                zip_code = res.getString(res.getColumnIndex(DBHelper.SEARCH_COLUMN_ADDRESS_ZIP_CODE));
                street   = res.getString(res.getColumnIndex(DBHelper.SEARCH_COLUMN_ADDRESS_STREET));
                address  = street + ", " + city + ", " + state;
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
                Log.i("address", address);*/
                //} while (res.moveToNext());
            }
        }
    }

    private void updateControls(){
        //Read SQLITE db and query for BUSINESS_ID provided
        CharSequence csBusinessName, csAddress, csDescription;
        Uri uImageUri;
        float fNumStars;

        /*csBusinessName = "Princess Cafe";
        csAddress = "4 Princess Street, Waterloo, ON";
        csDescription = "A charming minimalistic cafe epitomizing the heart of uptown waterloo.";
        uImageUri = Uri.parse("/sdcard/DCIM/Camera/IMG_20160922_020412.jpg");
        fNumStars = 4.1f;
*/
        csBusinessName = name;
        csAddress = address;
        csDescription = "TBD";
        //uImageUri = Uri.parse("/sdcard/DCIM/Camera/IMG_20160922_020412.jpg");

        //uImageUri = Uri.parse(photo);


        fNumStars = Float.parseFloat(rating);

        new DownloadImageTask(ivPicture).execute(photo);



        tvBusinessName.setText(csBusinessName);
        rbRatingBar.setRating(fNumStars);
        tvAddress.setText(csAddress);
        tvDescription.setText(csDescription);

        //ivPicture.setImageBitmap();
        //ivPicture.setMaxWidth(100);
        //ivPicture.setMinimumWidth(100);
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        int height = metrics.heightPixels;
        int width = metrics.widthPixels;

        ivPicture.setMaxHeight(height/2);
        //ivPicture.setMinHeight(height/2);
        ivPicture.setMinimumWidth(width);

        if (t != null){
            t.cancel();
        }
        //elvHours.set
    }

    // show The Image in a ImageView
    //new DownloadImageTask((ImageView) findViewById(R.id.imageView1))
    //        .execute("http://java.sogeti.nl/JavaBlog/wp-content/uploads/2009/04/android_icon_256.png");

    //public void onClick(View v) {
    //    startActivity(new Intent(this, IndexActivity.class));
    //    finish();

//    }

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
}
