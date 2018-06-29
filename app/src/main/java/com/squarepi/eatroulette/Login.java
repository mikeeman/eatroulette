package com.squarepi.eatroulette;

import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadLocalRandom;

import static com.squarepi.eatroulette.YelpHelper.getNewToken;

/**
 * Created by PC on 9/22/2016.
 */

public class Login extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        TextView tvResponse = (TextView) findViewById(R.id.tvResponse);

        int expiresIn = -1;
        Long lCurrentSystemTimeSeconds = System.currentTimeMillis()/1000;

        String  response    = new String(),
                token       = new String(),
                name        = new String(),
                address     = new String(),
                phone       = new String(),
                price       = new String(),
                photo       = new String(),
                rating      = new String(),
                location    = new String(),
                country     = new String(),
                state       = new String(),
                city        = new String(),
                zip_code    = new String(),
                street      = new String(),
                website     = new String();

        DBHelper db = new DBHelper(this);

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

            tvResponse = (TextView) findViewById(R.id.tvResponse);
            tvResponse.setText(response);

            //Get new expiry date
            Long lExpiryDateSeconds = lCurrentSystemTimeSeconds + expiresIn;

            //Write new token and expiry to db
            db.insertSetting(DBHelper.SETTINGS_NAME_TOKEN, token);
            db.insertSetting(DBHelper.SETTINGS_NAME_TOKENEXPIRES, lExpiryDateSeconds.toString());
        }

        //Now we have a valid token
        YelpHelper.setCurrentToken(token);

        TextView tvToken = (TextView) findViewById(R.id.tvToken);
        tvToken.setText(token);

        //try {
            //response = new YelpHelper.GetRandomSearchTask(this, this).execute().get();
        //}
        //catch (ExecutionException e) {
        //    e.printStackTrace();
        //} catch (InterruptedException e) {
        //    e.printStackTrace();
        //}

        try {
            // Convert String to json object
            JSONObject jsonResponse = new JSONObject(response);
            JSONArray businessList = new JSONArray((jsonResponse.getString(YelpHelper.RESPONSE_BUSINESSES)));
            int numberOfBusinesses = businessList.length();
            JSONObject randomBusiness = businessList.getJSONObject(ThreadLocalRandom.current().nextInt(0, numberOfBusinesses + 1));

            name = randomBusiness.getString(YelpHelper.RESPONSE_NAME);
            //address = randomBusiness.getString(YelpHelper.RESPONSE_ADDRESS_STREET);
            photo = randomBusiness.getString(YelpHelper.RESPONSE_PHOTO);
            rating = randomBusiness.getString(YelpHelper.RESPONSE_RATING);
            address = randomBusiness.getString(YelpHelper.RESPONSE_NAME);
            phone = randomBusiness.getString(YelpHelper.RESPONSE_PHONE);
            price = randomBusiness.getString(YelpHelper.RESPONSE_PRICE);
            photo = randomBusiness.getString(YelpHelper.RESPONSE_NAME);
            rating = randomBusiness.getString(YelpHelper.RESPONSE_NAME);
            location = randomBusiness.getString(YelpHelper.RESPONSE_NAME);
            country = randomBusiness.getString(YelpHelper.RESPONSE_ADDRESS_COUNTRY);
            state = randomBusiness.getString(YelpHelper.RESPONSE_ADDRESS_STATE);
            city = randomBusiness.getString(YelpHelper.RESPONSE_ADDRESS_CITY);
            zip_code = randomBusiness.getString(YelpHelper.RESPONSE_ADDRESS_ZIP_CODE);
            street = randomBusiness.getString(YelpHelper.RESPONSE_ADDRESS_STREET);
            website = randomBusiness.getString(YelpHelper.RESPONSE_ADDRESS_WEBSITE);

            //public static final String RESPONSE_LOCATION            = "coordinates";


            //expiresIn = jsonResponse.getInt(YelpHelper.RESPONSE_EXPIRES_IN);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        tvResponse.setText(name);

    }


}
