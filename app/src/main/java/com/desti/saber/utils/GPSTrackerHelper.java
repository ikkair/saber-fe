package com.desti.saber.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import java.util.Arrays;
import java.util.List;

public class GPSTrackerHelper implements LocationListener {

    private String providerInfo;
    private Location location;
    protected LocationManager locationManager;

    public GPSTrackerHelper(Activity activity) {

        try {
            int checkPermissionFineLoc = ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION);
            int checkPermissionCoarse = ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION);
            int permissionGrantedCode = PackageManager.PERMISSION_GRANTED;

            if (checkPermissionFineLoc != permissionGrantedCode &&
                checkPermissionCoarse != permissionGrantedCode) {
                requestPermission(activity);
            }else{

                locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
                boolean isPassiveEnable = locationManager.isProviderEnabled(LocationManager.PASSIVE_PROVIDER);
                boolean isNetworkEnable = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
                boolean isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

                if (isPassiveEnable) {
                    providerInfo = LocationManager.PASSIVE_PROVIDER;
                }

                if (isNetworkEnable) {
                    providerInfo = LocationManager.NETWORK_PROVIDER;
                }

                if (isGpsEnabled) {
                    providerInfo = LocationManager.GPS_PROVIDER;
                }

                locationManager.requestLocationUpdates(providerInfo,0,1, this);
                location = locationManager.getLastKnownLocation(providerInfo);
            }
        }catch (Exception e){
            System.out.println(Arrays.toString(e.getStackTrace()));
        }
    }

    public double getLatitude() {
        if(location != null){
            return location.getLatitude();
        }
        return -6.4929255;
    }

    public double getLongitude() {
        if(location != null){
            return location.getLatitude();
        }
        return 106.7945535;
    }

    public String getProviderInfo() {
        return providerInfo;
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {

    }

    @Override
    public void onLocationChanged(@NonNull List<Location> locations) {

    }

    @Override
    public void onFlushComplete(int requestCode) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {

    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {

    }

    private void requestPermission(Activity activity){
        String[] permissionList = {
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
        };

        ActivityCompat.requestPermissions(
            activity,
            permissionList,
            PackageManager.PERMISSION_GRANTED
        );
    }

}
