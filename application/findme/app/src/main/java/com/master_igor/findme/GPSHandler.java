package com.master_igor.findme;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class GPSHandler extends Activity implements LocationListener, Runnable {
    private LocationManager locationManager;

    /**
     * ********** Called after each 3 sec *********
     */
    @Override
    public void onLocationChanged(Location location) {
        String str = "Latitude: " + location.getLatitude() + " \nLongitude: " + location.getLongitude();
        Log.d("GPS msg", str);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        /******** Called when User off Gps *********/
    }

    @Override
    public void onProviderEnabled(String provider) {

        /******** Called when User on Gps  *********/
    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void run() {
        /*locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                3000,   // 3 sec
                10, this);*/
    }

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                3000,   // 3 sec
                10, this);
    }

}
