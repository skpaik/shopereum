package io.left.core.utils.helper;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

public class GPSTracker implements LocationListener {

    private final Context mContext;

    // flag for GPS status
    boolean isGPSEnabled = false;

    // flag for network status
    boolean isNetworkEnabled = false;

    // flag for GPS status
    boolean canGetLocation = false;

    Location location; // location
    double latitude; // latitude
    double longitude; // longitude

    // The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 1L; // 10 meters

    // The minimum headerTime between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 1000 * 15; // 1 minute

    // Declaring a Location Manager
    protected LocationManager locationManager;
    private LocationProvider locationProvider;

    public GPSTracker(Context context, LocationProvider locationProvider) {
        this.mContext = context;
        this.locationProvider = locationProvider;
        getLocation();
    }

    public Location getLocation() {

        locationManager = (LocationManager) mContext.getSystemService(mContext.LOCATION_SERVICE);
        // getting GPS status
        isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        // getting network status
        isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);


        if (isGPSEnabled || isNetworkEnabled) {

            this.canGetLocation = true;
            // First get location from Network Provider
            if (isNetworkEnabled) {
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES,
                        MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                Log.d("Network", "Network");
                if (locationManager != null) {
                    location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    if (location != null) {
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();
                    }
                }
            }
            // if GPS Enabled get lat/long using GPS Services
            if (isGPSEnabled) {
                if (location == null) {
                    if (ActivityCompat.checkSelfPermission((Activity) mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                        return null;
                    }
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                    Log.d("GPS", "GPS Enabled");
                    if (locationManager != null) {
                        location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                        }
                    }
                }
            }

        }

        return location;
    }

    public Location getCurrentLOcation() {
        return location;
    }

    /**
     * Stop using GPS listener
     * Calling this function will stop using GPS in your app
     */
    public void stopUsingGPS() {
        if (locationManager != null) {
            locationManager.removeUpdates(GPSTracker.this);
        }
    }

    /**
     * Function to get latitude
     */
    public double getLatitude() {
        if (location != null) {
            latitude = location.getLatitude();
        }

        // return latitude
        return latitude;
    }

    /**
     * Function to get longitude
     */
    public double getLongitude() {
        if (location != null) {
            longitude = location.getLongitude();
        }

        // return longitude
        return longitude;
    }

    /**
     * Function to check GPS/wifi enabled
     *
     * @return boolean
     */
    public boolean canGetLocation() {
        return this.canGetLocation;
    }

    @Override
    public void onLocationChanged(Location location) {
        this.location = location;
        locationProvider.onLocationUpdate(location);
    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }


    public void requestPermission(String strPermission, int perCode, Context _c, Activity _a) {

        if (ActivityCompat.shouldShowRequestPermissionRationale(_a, strPermission)) {
            //Toast.makeText(this, "GPS permission allows us to access location data. Please allow in FlareApplication Settings for additional functionality.", Toast.LENGTH_LONG).show();
        } else {

            ActivityCompat.requestPermissions(_a, new String[]{strPermission}, perCode);
        }
    }

    public void getLocation(Context context, Activity activity, int requestCode) {
        if (checkPermission(Manifest.permission.ACCESS_FINE_LOCATION, context, activity)) {
            fetchLocationData();
            Toaster.show("LAT : " + getLatitude() + " Long " + getLongitude());
        } else {
            //GPSTracker gps = new GPSTracker(StatusActivity.this);
            // gps.showSettingsAlert();
            requestPermission(Manifest.permission.ACCESS_FINE_LOCATION, requestCode, context, activity);
        }

    }

    public static boolean checkPermission(String strPermission, Context _c, Activity _a) {
        int result = ContextCompat.checkSelfPermission(_c, strPermission);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    public Location fetchLocationData() {
        if (canGetLocation()) {
            double latitude = getLatitude();
            double longitude = getLongitude();
            Toaster.show("LAT : " + getLatitude() + " Long " + getLongitude());
           /* location.setLatitude(latitude);
            location.setLongitude(longitude);*/
            return location;
          /*  Toast.makeText(this, String.valueOf(latitude), Toast.LENGTH_SHORT).show();
            Toast.makeText(this, String.valueOf(longitude), Toast.LENGTH_SHORT).show();*/
        } else
            return null;
    }

    public interface LocationProvider {
        void onLocationUpdate(Location location);
    }

}
