package com.codeflowdevelopers.safedrive;

import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Timer;
import java.util.TimerTask;

public class LocationService extends Service implements LocationListener {

    public static String str_receiver = "servicetutorial.service.receiver";
    boolean isGPSEnable = false;
    boolean isNetworkEnable = false;
    LocationManager locationManager;
    Location location;
    long notify_interval = 10000;
    Intent intent;
    boolean isInterntAvailableField;
    private Handler mHandler = new Handler();
    private Timer mTimer = null;
    private double latitude;
    private double longitude;
    private double preLatitude;
    private double preLongitude;
    long atimeinmillis;
    double distanceInKm;
    String TAG="Speed";
    long currentTimeInMillies;
    long preTimeInMillies;
    double speedInKmPerH;


    public LocationService() {
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();


        mTimer = new Timer();
        mTimer.schedule(new TimerTaskToGetLocation(), 5, notify_interval);
        intent = new Intent(str_receiver);
        preLatitude = 0.0;
        preLongitude = 0.0;
        preTimeInMillies=0;
        currentTimeInMillies=System.currentTimeMillis();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        mTimer.cancel();
        stopSelf();
    }

    @Override
    public void onLocationChanged(Location location) {

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


    private void fn_getlocation() {
        locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
        isGPSEnable = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        isNetworkEnable = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if (!isGPSEnable && !isNetworkEnable) {

        } else {

            checkInternetAvailable();

            if (isGPSEnable && isInterntAvailableField) {

                location = null;
                if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 50, this);
                if (locationManager != null) {
                    location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                    if (location != null) {
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();
                        fn_update(location);

                        if (preLatitude != 0.0 && preLongitude != 0.0 && preTimeInMillies!=0) {
                            currentTimeInMillies=System.currentTimeMillis();
                            long timeInSecs=(currentTimeInMillies-preTimeInMillies)/1000;
                             distanceInKm=distance(latitude,longitude,preLatitude,preLongitude);

                             speedInKmPerH=(double) (distanceInKm*1000*5/(timeInSecs*18));


                        }
                        preTimeInMillies=currentTimeInMillies;
                        preLatitude = latitude;
                        preLongitude = longitude;


                        Toast.makeText(this,"Speed in Km/h : "+speedInKmPerH ,Toast.LENGTH_SHORT).show();
                        Log.e(TAG,"Speed in Km/h : "+speedInKmPerH);
                    }
                }
            }
        }

    }

        private double distance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1))
                * Math.sin(deg2rad(lat2))
                + Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        return (dist);
    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }

    private void fn_update(Location location) {


        sendBroadcast(intent);
    }

    private void checkInternetAvailable() {

        ServiceCheckNetwork checkNetwork = new ServiceCheckNetwork();
        checkNetwork.execute();
    }

    private class TimerTaskToGetLocation extends TimerTask {
        @Override
        public void run() {

            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    fn_getlocation();

                }
            });
        }
    }

    private class ServiceCheckNetwork extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                final InetAddress address = InetAddress.getByName("www.google.com");
                if (!address.equals("")) {
                    isInterntAvailableField = true;
                } else isInterntAvailableField = false;

            } catch (UnknownHostException e) {
                isInterntAvailableField = false;
            }
            return null;
        }
    }
}
