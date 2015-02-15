package com.master_igor.findme;

import android.app.Activity;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;

public class GPSHandler extends Service {

    private static final String TAG = "GPS message";
    final static String GET_GPS = "GET_GPS";
    private LocationManager mLocationManager = null;
    private static final int LOCATION_INTERVAL = 1000;
    private static final float LOCATION_DISTANCE = 10f;
    public int userID;
    public Intent intent;

    /*public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        Log.d("GetId", "" + userID);
        this.userID = userID;
    }*/

    private class LocationListener implements android.location.LocationListener {
        Location mLastLocation;

        public LocationListener(String provider) {
            String strLat = "", strLon = "";

            try {
                strLat = String.valueOf(mLastLocation.getLatitude()).replace(",", ".");
            } catch (NullPointerException e) {
                strLat = "0";
            }

            try {
                strLon = String.valueOf(mLastLocation.getLongitude()).replace(",", ".");
            } catch (NullPointerException e) {
                strLon = "0";
            }

            // TODO:: adding first coordinates
            Log.e(TAG, "LocationListener " + provider);
            mLastLocation = new Location(provider);
            String firstCoord = "http://master-igor.com/findme/setcoord/" + userID + "/" +
                    strLat + "/" + strLon + "/";
            Log.d("FIRST", firstCoord);
            try {
                URL url = new URL(firstCoord);
                ServerAPIHandler server = new ServerAPIHandler(url);
                Thread thr = new Thread(server);
                thr.start();
                thr.join();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onLocationChanged(Location location) {

            String strLat = "", strLon = "";

            try {
                strLat = String.valueOf(location.getLatitude()).replace(",", ".");
            } catch (NullPointerException e) {
                strLat = "0";
            }

            try {
                strLon = String.valueOf(location.getLongitude()).replace(",", ".");
            } catch (NullPointerException e) {
                strLon = "0";
            }

            SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(GPSHandler.this);
            int dist = Integer.parseInt(settings.getString("max_distance", "1000"));
            String coord = "http://master-igor.com/findme/setcoord/" + userID + "/" +
                    strLat + "/" + strLon + "/" + dist + "/";

            try {
                URL url = new URL(coord);
                ServerAPIHandler server = new ServerAPIHandler(url);
                Thread thr = new Thread(server);
                thr.start();
                thr.join();
                intent = new Intent();
                intent.setAction(GET_GPS);
                intent.putExtra("friendsGPS", server.getServerMessage());
                sendBroadcast(intent);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Log.e(TAG, "onLocationChanged: " + location);
            mLastLocation.set(location);

        }

        @Override
        public void onProviderDisabled(String provider) {
            Log.e(TAG, "onProviderDisabled: " + provider);
        }

        @Override
        public void onProviderEnabled(String provider) {
            Log.e(TAG, "onProviderEnabled: " + provider);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.e(TAG, "onStatusChanged: " + provider);
        }
    }

    LocationListener[] mLocationListeners = new LocationListener[]{
            new LocationListener(LocationManager.GPS_PROVIDER),
            new LocationListener(LocationManager.NETWORK_PROVIDER)
    };

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        this.intent = intent;
        Log.e(TAG, "onStartCommand");
        super.onStartCommand(intent, flags, startId);
        userID = intent.getIntExtra("userID", 0);
        Log.d("StrCmd", "" + userID);
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        Log.e(TAG, "onCreate");
        initializeLocationManager();
        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                    mLocationListeners[1]);
        } catch (java.lang.SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "network provider does not exist, " + ex.getMessage());
        }
        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                    mLocationListeners[0]);
        } catch (java.lang.SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "gps provider does not exist " + ex.getMessage());
        }

//        LocalBroadcastManager.getInstance(this).registerReceiver(
//                mMessageReceiver, new IntentFilter("GPSuserID"));
    }

    @Override
    public void onDestroy() {
        Log.e(TAG, "onDestroy");
        super.onDestroy();
        if (mLocationManager != null) {
            for (int i = 0; i < mLocationListeners.length; i++) {
                try {
                    mLocationManager.removeUpdates(mLocationListeners[i]);
                } catch (Exception ex) {
                    Log.i(TAG, "fail to remove location listners, ignore", ex);
                }
            }
        }
    }

    private void initializeLocationManager() {
        Log.e(TAG, "initializeLocationManager");
        if (mLocationManager == null) {
            mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        }
    }

}