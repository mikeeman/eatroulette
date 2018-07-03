package com.squarepi.eatroulette;

import android.content.Context;
import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by mmatkiws on 5/7/2017.
 */


public class YelpHelper {
    public static final String RESPONSE_ACCESS_TOKEN        = "access_token";
    public static final String RESPONSE_EXPIRES_IN          = "expires_in";
    public static final String RESPONSE_BUSINESSES          = "businesses";
    public static final String RESPONSE_TOTAL_BUSINESSES    = "total";
    public static final String RESPONSE_NAME                = "name";
    public static final String RESPONSE_PHONE               = "phone";
    public static final String RESPONSE_PRICE               = "price";
    public static final String RESPONSE_PHOTO               = "image_url";
    public static final String RESPONSE_RATING              = "rating";
    public static final String RESPONSE_LOCATION            = "location";
    public static final String RESPONSE_ADDRESS_COUNTRY     = "country";
    public static final String RESPONSE_ADDRESS_STATE       = "state";
    public static final String RESPONSE_ADDRESS_CITY        = "city";
    public static final String RESPONSE_ADDRESS_ZIP_CODE    = "zip_code";
    public static final String RESPONSE_ADDRESS_STREET      = "address1";
    public static final String RESPONSE_WEBSITE             = "url";
    public static final String RESPONSE_COORDINATES         = "coordinates";
    public static final String RESPONSE_LATITUDE            = "latitude";
    public static final String RESPONSE_LONGITUDE           = "longitude";
    public static final String RESPONSE_IS_CLOSED           = "is_closed";
    public static final String RESPONSE_TYPE                = "categories";
    public static final String RESPONSE_TYPE_NAME           = "title";

    public static String mCurrentLocationLat;
    public static String mCurrentLocationLon;
    public static String mOldLocationLat;
    public static String mOldLocationLon;

    private static Integer mOffset;

    private static String token;

    public static String getCurrentApiKey()
    {
        return BuildConfig.API_KEY;
    }

    public static String getCurrentToken()
    {
        //include validation, logic, logging or whatever you like here
        return token;
    }
    public static void setCurrentToken(String value)
    {
        //include more logic
        token = value;
    }
    public static String getCurrentLocationLat()
    {
        //include validation, logic, logging or whatever you like here
        return mCurrentLocationLat;
    }
    public static String getCurrentLocationLon(){
        return mCurrentLocationLon;
    }
    public static void setCurrentLocation(String lat, String lon)
    {
        Log.i("YelpHelper", "setCurrentLocation");
        mCurrentLocationLat = lat;
        mCurrentLocationLon = lon;
        Log.i("mCurrentLocationLat", lat);
        Log.i("mCurrentLocationLon", lon);

        if (getOldLocationLon() == null || getOldLocationLat() == null){
            setOldLocation();
        }
    }
    public static String getOldLocationLat()
    {
        //include validation, logic, logging or whatever you like here
        return mOldLocationLat;
    }
    public static String getOldLocationLon(){
        return mOldLocationLon;
    }
    public static void setOldLocation()
    {
        Log.i("YelpHelper", "setOldLocation");
        mOldLocationLat = getCurrentLocationLat();
        mOldLocationLon = getCurrentLocationLon();
        if (mOldLocationLat != null && mOldLocationLon != null){
            Log.i("mOldLocationLat", mOldLocationLat);
            Log.i("mOldLocationLon", mOldLocationLon);
        } else {
            Log.i("mOldLocationLat/Lon", "NULL");
        }

    }


    public static String httpGet(URL url, String params){
        Log.i("httpGet", url.toString() + "&" + params);
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
            //String basicAuth = "Bearer " + getCurrentToken();
            String basicAuth = "Bearer " + getCurrentApiKey();
            urlConn.setRequestProperty ("Authorization", basicAuth);
            urlConn.setRequestMethod("GET");
            //urlConn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            urlConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
            //urlConn.setRequestProperty("Accept", "application/x-www-form-urlencoded");
            //urlConn.setRequestProperty("Content-Length", Integer.toString(params.toString().getBytes().length));
            //urlConn.setRequestProperty("Content-Language", "en-US");
            //urlConn.setDoOutput(true);
            //urlConn.setDoInput(true);
            //urlConn.setUseCaches(false);
            //urlConn.setInstanceFollowRedirects(true);

            //urlConn.connect();

            //DataOutputStream writer = new DataOutputStream(urlConn.getOutputStream());
            //OutputStream writer = urlConn.getOutputStream();
            //BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(urlConn.getOutputStream(), "UTF-8"));
            //writer.write(params.toString());
            //writer.write(params.toString().getBytes("UTF-8"));
            // writer.writeBytes(params);
            //writer.write(params.getBytes(StandardCharsets.US_ASCII));
            //writer.flush();
            //writer.close();

            //urlConn.connect();

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


    public static String httpPost(URL url, String params){

        InputStream is = new InputStream() {
            @Override
            public int read() throws IOException {
                return 0;
            }
        };

        String response = "[]";

        try {
            URL _url = new URL(url.toString());
            HttpURLConnection urlConn =(HttpURLConnection)_url.openConnection();
            urlConn.setRequestMethod("POST");
            //urlConn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            urlConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
            //urlConn.setRequestProperty("Accept", "application/x-www-form-urlencoded");
            //urlConn.setRequestProperty("Content-Length", Integer.toString(params.toString().getBytes().length));
            //urlConn.setRequestProperty("Content-Language", "en-US");
            //urlConn.setDoOutput(true);
            //urlConn.setDoInput(true);
            //urlConn.setUseCaches(false);
            //urlConn.setInstanceFollowRedirects(true);

            //urlConn.connect();

            DataOutputStream writer = new DataOutputStream(urlConn.getOutputStream());
            //OutputStream writer = urlConn.getOutputStream();
            //BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(urlConn.getOutputStream(), "UTF-8"));
            //writer.write(params.toString());
            //writer.write(params.toString().getBytes("UTF-8"));
            // writer.writeBytes(params);
            writer.write(params.getBytes(StandardCharsets.US_ASCII));
            writer.flush();
            writer.close();

            //urlConn.connect();

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


    public static String getNewSearchResult(Context c, Integer offset, Boolean firstTime) {
        Log.i("YelpHelper", "getNewSearchResult(" + c.toString()+ ")");
        String response = "...";
/*
        float lat, lon;

        SingleShotLocationProvider.requestSingleUpdate(c, new SingleShotLocationProvider.LocationCallback() {
                    @Override
                    public void onNewLocationAvailable(SingleShotLocationProvider.GPSCoordinates location) {
                        Log.d("Location", "my location is " + location.toString());
                        setCurrentLocation(location.toString());
                    }
                }
        );
*/
        try {
            String urlstring = "https://api.yelp.com/v3/businesses/search";
            //String urlstring = "https://api.yelp.com/v3/businesses/north-india-restaurant-san-francisco";
            //String params = "term=food";
            //String params = "term=food";
            //String params = "term=food&latitude=37.786882&longitude=-122.399972";
            //String params = "term=food&" + getCurrentLocation();

            //setCurrentLocation("37.786882", "-122.399972");
            //if (YelpHelper.getCurrentLocationLat() == null && YelpHelper.getCurrentLocationLon() == null) {
            //Log.i("getCurrentLat/Lon", "NULL");
            SingleShotLocationProvider.requestSingleUpdate(c,
                    new SingleShotLocationProvider.LocationCallback() {
                        @Override
                        public void onNewLocationAvailable(SingleShotLocationProvider.GPSCoordinates location) {
                            Log.i("onNewLocationAvailable", location.toString());
                            String lat = Float.toString(location.latitude);
                            String lon = Float.toString(location.longitude);
                            setCurrentLocation(lat, lon);
                        }
                    });

            //TODO: for emulator set lat lon for Markham
            setCurrentLocation("43.8382668","-79.3012424");

            if (getCurrentLocationLat() == null || getCurrentLocationLon() == null) {
                Location l = SingleShotLocationProvider.requestLastKnownLocation(c);
                setCurrentLocation(Double.toString(l.getLatitude()),Double.toString(l.getLongitude()));
            }
            //} else {
            //    Log.i("getCurrentLat/Lon", "(" + getCurrentLocationLat() + ", " + getCurrentLocationLon() + ")");
            //}

            //if (ContextCompat.checkSelfPermission(c, android.Manifest.permission.ACCESS_FINE_LOCATION) ==
            //        PackageManager.PERMISSION_GRANTED &&
            //        ContextCompat.checkSelfPermission(c, android.Manifest.permission.ACCESS_COARSE_LOCATION) ==
            //                PackageManager.PERMISSION_GRANTED) {
            //final LocationManager locationManager = (LocationManager) c.getSystemService(Context.LOCATION_SERVICE);
            //lat = Double.toString(locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER).getLatitude());
            //lon = Double.toString(locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER).getLongitude());

            //} else {
            //    Toast.makeText(c, "Turn On Location", Toast.LENGTH_LONG).show();
            //ActivityCompat.requestPermissions(c, new String[] {
            //  Manifest.permission.ACCESS_FINE_LOCATION,
            // Manifest.permission.ACCESS_COARSE_LOCATION }, 1
            //TAG_CODE_PERMISSION_LOCATION);
            //);
            //}

            /*try {
                lat = Double.toString(locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER).getLatitude());
                lon = Double.toString(locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER).getLongitude());
            } catch (SecurityException e)
            {
                e.printStackTrace();
            }
            */
            //TODO change this search term based on time of day OR filter
            String foodCategory = "Restaurant";
            String params = "term=" + foodCategory + "&latitude=" + getCurrentLocationLat() + "&longitude=" + getCurrentLocationLon() + "&offset=" + Integer.toString(offset) + "&open_now=true" + "&sort_by=rating";

            if (firstTime){
                params += "&limit=20";
            }

            URL url = new URL(urlstring);

            response = httpGet(url, params);
        }
        catch (MalformedURLException e)
        {
            e.printStackTrace();
        } //catch (IOException e) {
        // e.printStackTrace();
        // } //catch (JSONException e) {
        //e.printStackTrace();
        //}
        return response;
    }

    public static String getNewToken() {
        String response = "...";
        try {
            String urlstring = "https://api.yelp.com/oauth2/token";
            String params = "grant_type=client_credentials&client_id=v9qiezGxNAYcJn0n4yxWZQ&client_secret=V1Kbb26oMpWx892Jynfz1NsBMwjFDRaiiWZgai3VEbnQq2SVP1Qm3uYQyUkI59g6";

            URL url = new URL(urlstring);

            response = httpPost(url, params);
        }
        catch (MalformedURLException e)
        {
            e.printStackTrace();
        } //catch (IOException e) {
        //    e.printStackTrace();
        //} //catch (JSONException e) {
        //e.printStackTrace();
        //}
        return response;
    }

    public static class GetRandomSearchTask extends AsyncTask<Void, Integer, String> {
        private static Slider parent;
        private Context mContext;
        private Boolean bFirstTime;

        public GetRandomSearchTask(Context context, int maxNumBusinesses) {
            Log.i("YelpHelper", "GetRandomSearchTask(c," + Integer.toString(maxNumBusinesses) + ")");
            mContext = context;
            if (0 == maxNumBusinesses) {
                //First Time
                mOffset = ThreadLocalRandom.current().nextInt(0, 10);
                bFirstTime = true;
            } else {
                mOffset = ThreadLocalRandom.current().nextInt(0, maxNumBusinesses);
                bFirstTime = false;
            }
        }

        protected String doInBackground(Void... v) {
            String newResult = YelpHelper.getNewSearchResult(mContext, mOffset, bFirstTime);
            return newResult;
        }

        protected void onProgressUpdate(Integer... progress) {
            //setProgressPercent(progress[0]);
        }

        protected void onPostExecute(String result) {
            //showDialog("Downloaded " + result + " bytes");

        }
    }


    public static class GetTokenTask extends AsyncTask<Void, Integer, String> {

        protected String doInBackground(Void... v) {
            String newToken = YelpHelper.getNewToken();
            return newToken;
        }

        protected void onProgressUpdate(Integer... progress) {
            //setProgressPercent(progress[0]);
        }

        protected void onPostExecute(String result) {
            //showDialog("Downloaded " + result + " bytes");

        }
    }



}
