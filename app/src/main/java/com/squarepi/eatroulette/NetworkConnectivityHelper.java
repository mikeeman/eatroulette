package com.squarepi.eatroulette;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

/**
 * Created by PC on 6/21/2017.
 */

public class NetworkConnectivityHelper {

    public boolean checkNetworkConnectivity(ConnectivityManager cm) {
        Log.i("NetworkConnectivityHelp", "checkNetworkConnectivity()");
        boolean status = false;
        try{
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            if (netInfo != null && netInfo.getState()==NetworkInfo.State.CONNECTED) {
                status= true;
            }else {
                netInfo = cm.getActiveNetworkInfo();
                if(netInfo!=null && netInfo.getState()==NetworkInfo.State.CONNECTED)
                    status= true;
            }
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
        return status;
    }
}
