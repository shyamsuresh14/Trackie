package com.test.shyam.trackie;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

public class GPSTracker extends Service implements LocationListener {

    private int MIN_TIME = 5000;
    private int MIN_DIST = 1000;

    private double lat;
    private double lon;
    private String mLocationProvider;
    private LocationManager mLocationManager;
    private Context mContext;
    private Location loc;

    public GPSTracker(Context context) {
        this.mContext = context;
        this.mLocationProvider = LocationManager.GPS_PROVIDER;
        //this.mLocationManager = (LocationManager) getSystemService(mContext.LOCATION_SERVICE);
        this.lat = 0.0;
        this.lon = 0.0;
        getLoc();
    }

    private void getLoc() {
        mLocationManager = (LocationManager) mContext.getSystemService(LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider Calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mLocationManager.requestLocationUpdates(mLocationProvider, MIN_TIME, MIN_DIST, this);
        if(mLocationManager != null){
            loc = mLocationManager.getLastKnownLocation(mLocationProvider);
            updateLoc();
        }
        //mLocationManager.removeUpdates(this);
    }

    private void updateLoc(){
        lat = loc.getLatitude();
        lon = loc.getLongitude();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onLocationChanged(Location location) {
        lat = location.getLatitude();
        lon = location.getLongitude();
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

    public Double getLatitude(){
        return lat;
    }

    public Double getLongitude(){
        return lon;
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
    }
}
