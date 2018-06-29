package com.squarepi.eatroulette;

import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.RotateDrawable;
import android.net.ConnectivityManager;
import android.net.Network;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.view.menu.MenuView;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

/**
 * Created by PC on 6/9/2017.
 */

public class Splash extends Activity {
    private static final int PERMISSION_ALL = 0;
    private Handler h;
    private Runnable r;
    private LayerDrawable ldLogo;
    private RotateDrawable rdLogo;
    /*
      SharedPreferences mPrefs;
      final String settingScreenShownPref = "settingScreenShown";
      final String versionCheckedPref = "versionChecked";
    */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //ldLogo = (LayerDrawable) ContextCompat.getDrawable(Splash.this, R.id.llLogo);
        //rdLogo = (RotateDrawable) findViewById(R.id.rotatelogo).getDrawableState();
        //TODO: rotate logo animation

        /// /llLogo.setAnimation(AnimationUtils.loadAnimation(Splash.this, R.anim.loadingscreen));
        //button.setCompoundDrawablesWithIntrinsicBounds(R.drawable.button_progress_bar_small, 0, 0, 0);
        //LayerDrawable progressAnimationLeft = (LayerDrawable) button.getCompoundDrawables()[0];
        //((Animatable) progressAnimationLeft.getDrawable(0)).start();
        //((Animatable) progressAnimationLeft.getDrawable(1)).start();

        //iLogo.setAnimation(AnimationUtils.loadAnimation(Splash.this, R.anim.loadingscreen));

        h = new Handler();
        r = new Runnable() {
            @Override
            public void run() {
                //Toast.makeText(Splash.this, "Swipe away your hunger!", Toast.LENGTH_LONG).show();

  /*          // (OPTIONAL) these lines to check if the `First run` ativity is required
                int versionCode = BuildConfig.VERSION_CODE;
                String versionName = BuildConfig.VERSION_NAME;

                mPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor editor = mPrefs.edit();

                Boolean settingScreenShown = mPrefs.getBoolean(settingScreenShownPref, false);
                int savedVersionCode = mPrefs.getInt(versionCheckedPref, 1);

                if (!settingScreenShown || savedVersionCode != versionCode) {
                    startActivity(new Intent(Splash.this, FirstRun.class));
                    editor.putBoolean(settingScreenShownPref, true);
                    editor.putInt(versionCheckedPref, versionCode);
                    editor.commit();
                }
                else
  */

                //Check network connectivity on first launch
                NetworkConnectivityHelper nch = new NetworkConnectivityHelper();
                if (nch.checkNetworkConnectivity((ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE))){
                    Log.i("Splash", "isConnected");
                    startActivity(new Intent(Splash.this, Slider.class));
                    finish();
                } else {
                    Log.i("Splash", "isNOTConnected");
                    Toast.makeText(Splash.this, "No Network Connection...", Toast.LENGTH_LONG).show();
                    finish();
                    System.exit(0);
                }
                //startActivity(new Intent(Splash.this, Slider.class));
                //finish();

            }
        };

        String[] PERMISSIONS = {
                ACCESS_COARSE_LOCATION,
                ACCESS_FINE_LOCATION,
                WRITE_EXTERNAL_STORAGE
        };

        if(!PermissionsHelper.hasPermissions(this, PERMISSIONS)){
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }
        else
            h.postDelayed(r, 1500);
    }

    // Put the below OnRequestPermissionsResult code here
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {

        int index = 0;
        Map<String, Integer> PermissionsMap = new HashMap<String, Integer>();
        for (String permission : permissions){
            PermissionsMap.put(permission, grantResults[index]);
            index++;
        }

        if((PermissionsMap.get(ACCESS_FINE_LOCATION) != 0)
                || PermissionsMap.get(WRITE_EXTERNAL_STORAGE) != 0){
            Toast.makeText(this, "Aiyaaa! Location and Storage permissions are a must!", Toast.LENGTH_SHORT).show();
            finish();
        }
        else
        {
            h.postDelayed(r, 1500);
        }
    }
}