package com.codeflowdevelopers.safedrive;

import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.codeflowdevelopers.safedrive.LocationUtil.LocationHelper;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;


public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, ActivityCompat.OnRequestPermissionsResultCallback, LocationListener {

    LocationHelper locationHelper;
    private Location mLastLocation;
    private static double latitude;
    private static double longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button startServiceButton=(Button)findViewById(R.id.start_service_button);
        final TextView locationText=(TextView)findViewById(R.id.location_text);



        locationHelper = new LocationHelper(this);

        locationHelper.checkpermission();

        startServiceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent serviceIntent = new Intent(getApplicationContext(), LocationService.class);
                startService(serviceIntent);

                Toast.makeText(getApplicationContext(), "Location Sharing Started", Toast.LENGTH_SHORT).show();
            }
        });

        if (locationHelper.checkPlayServices()) {

            locationHelper.buildGoogleApiClient();
        }

        Handler locationShowHandler = new Handler();

        locationShowHandler.postDelayed(new Runnable() {

            @Override
            public void run() {

                mLastLocation = locationHelper.getLocation();

                if (mLastLocation != null) {
                    latitude = mLastLocation.getLatitude();
                    longitude = mLastLocation.getLongitude();

                    String str="Location Co-ordinates :"+latitude+" "+longitude;

                    locationText.setText(str);


                } else {

                    showToast("Make sure location is enabled on the device");
                }

            }
        }, 2000);

    }
    public void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        locationHelper.onRequestPermissionsResult(requestCode, permissions, grantResults);


    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        locationHelper.onActivityResult(requestCode, resultCode, data);
    }

}
