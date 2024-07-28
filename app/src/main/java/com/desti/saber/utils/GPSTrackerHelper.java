package com.desti.saber.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import java.util.Arrays;
import java.util.List;

public class GPSTrackerHelper{

    private String providerInfo;
    private Location location;
    protected LocationManager locationManager;
    private final Activity activity;

    public GPSTrackerHelper(Activity activity) {
        this.activity = activity;
        onLocationChanged(null);
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

    private void setLocation(Location location){
        this.location = location;
    }

    public void onLocationChanged(GPSLocationChanged locationChanged){
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    int checkPermissionFineLoc = ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION);
                    int checkPermissionCoarse = ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION);
                    int permissionGrantedCode = PackageManager.PERMISSION_GRANTED;

                    if (checkPermissionFineLoc != permissionGrantedCode &&
                            checkPermissionCoarse != permissionGrantedCode) {
                        requestPermission(activity);
                    }else{
                        locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);

                        if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
                            providerInfo = LocationManager.GPS_PROVIDER;
                        } else if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
                            providerInfo = LocationManager.NETWORK_PROVIDER;
                        } else if (locationManager.isProviderEnabled(LocationManager.PASSIVE_PROVIDER)) {
                            providerInfo = LocationManager.PASSIVE_PROVIDER;
                        } else {
                            Toast.makeText(activity, "Provider Tidak Ditemukan ".concat(providerInfo), Toast.LENGTH_LONG).show();
                            return;
                        }


                        locationManager.requestLocationUpdates(providerInfo,0,0, new LocationListener(){
                            @Override
                            public void onLocationChanged(@NonNull Location location) {
                                setLocation(location);
                                if(locationChanged != null){
                                    locationChanged.onLocationChanged(location);
                                }
                            }

                            @Override
                            public void onStatusChanged(String provider, int status, Bundle extras) {
                               if(status == 1 && locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
                                   providerInfo = LocationManager.NETWORK_PROVIDER;
                               } else {
                                   providerInfo = LocationManager.GPS_PROVIDER;
                               }
                            }
                        });
                        location = locationManager.getLastKnownLocation(providerInfo);
                        Toast.makeText(activity, "Lokasi Ditemukan Menggunakan ".concat(providerInfo), Toast.LENGTH_LONG).show();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }
}
