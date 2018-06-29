package com.squarepi.eatroulette;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by PC on 10/13/2016.
 */

public class SingleShotLocationProvider {

    public static interface LocationCallback {
        public void onNewLocationAvailable(GPSCoordinates location);
    }

    public static Location requestLastKnownLocation(final Context context) {
        Log.i("SingleLocationProvider", "requestLastKnownLocation");

        Location lastLocation = new Location("dummyprovider");

        if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_COARSE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED) {
            Log.i("checkPermissions", "granted");
            final LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);


            if (isNetworkEnabled) {
                Log.i("isNetworkEnabled", "TRUE");
                Criteria criteria = new Criteria();
                criteria.setAccuracy(Criteria.ACCURACY_COARSE);
                lastLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                Log.i("getLastKnownLocation", lastLocation.toString());
            } else {
                Log.i("isNetworkEnabled", "FALSE");
                boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
                if (isGPSEnabled) {
                    Log.i("isGPSEnabled", "TRUE");
                    Criteria criteria = new Criteria();
                    criteria.setAccuracy(Criteria.ACCURACY_FINE);
                    lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                }
                else {
                    Log.i("isGPSEnabled", "FALSE");
                }
            }

        }
        return lastLocation;
    }
    // calls back to calling thread, note this is for low grain: if you want higher precision, swap the
    // contents of the else and if. Also be sure to check gps permission/settings are allowed.
    // call usually takes <10ms
    public static void requestSingleUpdate(final Context context, final LocationCallback callback) {
        Log.i("SingleLocationProvider", "requestSingleUpdate");
        if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_COARSE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED) {
            Log.i("checkPermissions", "granted");
            final LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            //REALLY WANT THIS
            boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            //EMULATOR WANTS THIS
            //boolean isNetworkEnabled = false;
            //Looper looper = null;
            //looper.prepare();

            if (isNetworkEnabled) {
                Log.i("isNetworkEnabled", "TRUE");
                Criteria criteria = new Criteria();
                criteria.setAccuracy(Criteria.ACCURACY_COARSE);
                locationManager.requestSingleUpdate(criteria, new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        Log.i("onLocationChanged", location.toString());
                        callback.onNewLocationAvailable(new GPSCoordinates(location.getLatitude(), location.getLongitude()));
                    }

                    @Override
                    public void onStatusChanged(String provider, int status, Bundle extras) {
                        Log.i("onStatusChanges", provider);
                    }

                    @Override
                    public void onProviderEnabled(String provider) {
                        Log.i("onProviderEnabled", provider);
                    }

                    @Override
                    public void onProviderDisabled(String provider) {
                        Log.i("onProviderDisabled", provider);
                    }
                }, Looper.getMainLooper());
            } else {
                Log.i("isNetworkEnabled", "FALSE");
                boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
                if (isGPSEnabled) {
                    Log.i("isGPSEnabled", "TRUE");
                    Criteria criteria = new Criteria();
                    criteria.setAccuracy(Criteria.ACCURACY_FINE);
                    locationManager.requestSingleUpdate(criteria, new LocationListener() {
                        @Override
                        public void onLocationChanged(Location location) {
                            Log.i("SingleShotLocation", "onChanged");
                            callback.onNewLocationAvailable(new GPSCoordinates(location.getLatitude(), location.getLongitude()));
                        }

                        @Override
                        public void onStatusChanged(String provider, int status, Bundle extras) {
                        }

                        @Override
                        public void onProviderEnabled(String provider) {
                        }

                        @Override
                        public void onProviderDisabled(String provider) {
                        }
                    }, Looper.getMainLooper());
                } else {
                    Log.i("isGPSEnabled","FALSE");
                }

            }
        } else {
            Log.e("CheckPermissions", "DENIED");
            //parent.runOnUiThread(new Runnable() {
            //    public void run() {
             //       Toast.makeText(parent.getBaseContext(), "Turn On Location", Toast.LENGTH_LONG).show();
             //   }
            //});
        }

    }


    // consider returning Location instead of this dummy wrapper class
    public static class GPSCoordinates {
        public float latitude = -1;
        public float longitude = -1;

        public GPSCoordinates(float theLatitude, float theLongitude) {
            latitude = theLatitude;
            longitude = theLongitude;
        }

        public GPSCoordinates(double theLatitude, double theLongitude) {
            latitude = (float) theLatitude;
            longitude = (float) theLongitude;
        }
    }
}