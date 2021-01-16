package com.oditly.audit.inspection.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.SystemClock;
import android.provider.Settings;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;


import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Random;

/*
 * Created by avengat(TechFidElite).
 * https://github.com/delight-im/Android-LocationUtils
 * https://github.com/mrmans0n/smart-location-lib*/ /*GeoFence -- Not using now
 * Utility class for easy access to the device location on Android
 */
public class LocationUtils {

    /**
     * Wrapper for two coordinates (latitude and longitude)
     */
    public static class Point implements Parcelable {

        /**
         * The latitude of the point
         */
        public final double latitude;
        /**
         * The longitude of the point
         */
        public final double longitude;

        /**
         * Constructs a new point from the given coordinates
         *
         * @param lat the latitude
         * @param lon the longitude
         */
        public Point(double lat, double lon) {
            latitude = lat;
            longitude = lon;
        }

        @Override
        public String toString() {
            return "(" + latitude + ", " + longitude + ")";
        }

        public static final Creator<Point> CREATOR = new Creator<Point>() {

            @Override
            public Point createFromParcel(Parcel in) {
                return new Point(in);
            }

            @Override
            public Point[] newArray(int size) {
                return new Point[size];
            }

        };

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            out.writeDouble(latitude);
            out.writeDouble(longitude);
        }

        private Point(Parcel in) {
            latitude = in.readDouble();
            longitude = in.readDouble();
        }

    }

    /**
     * Callback that can be implemented in order to listen for events
     */
    public interface Listener {
        /**
         * Called whenever the device's position changes so that you can call {@link LocationUtils#getPosition()}
         */
        public void onPositionChanged();

        public void canUpdateLocationToServer();

        public void canUpdateLocationToFBDB();
    }

    /**
     * The internal name of the provider for the coarse location
     */
    private static final String PROVIDER_COARSE = LocationManager.NETWORK_PROVIDER;
    /**
     * The internal name of the provider for the fine location
     */
    private static final String PROVIDER_FINE = LocationManager.GPS_PROVIDER;
    /**
     * The internal name of the provider for the fine location in passive mode
     */
    private static final String PROVIDER_FINE_PASSIVE = LocationManager.PASSIVE_PROVIDER;
    /**
     * The default interval to receive new location updates after (in milliseconds)
     */
    private static final long INTERVAL_DEFAULT = 45 * 1000;
    /**
     * The factor for conversion from kilometers to meters
     */
    private static final float KILOMETER_TO_METER = 1000.0f;
    /**
     * The factor for conversion from latitude to kilometers
     */
    private static final float LATITUDE_TO_KILOMETER = 111.133f;
    /**
     * The factor for conversion from longitude to kilometers at zero degree in latitude
     */
    private static final float LONGITUDE_TO_KILOMETER_AT_ZERO_LATITUDE = 111.320f;
    /**
     * The PRNG that is used for location blurring
     */
    private static final Random mRandom = new Random();
    private static final double SQUARE_ROOT_TWO = Math.sqrt(2);
    /**
     * The last location that was internally cached when creating new instances in the same process
     */
    private static Location mCachedPosition;
    private static Location mCachedPosition1;
    /**
     * The LocationManager instance used to query the device location
     */
    private final LocationManager mLocationManager;
    /**
     * Whether a fine location should be required or coarse location can be used
     */
    private final boolean mRequireFine;
    /**
     * Whether passive mode shall be used or not
     */
    private final boolean mPassive;
    /**
     * The internal after which new location updates are requested (in milliseconds) where longer intervals save battery
     */
    private final long mInterval;
    /**
     * Whether to require a new location (`true`) or accept old (last known) locations as well (`false`)
     */
    private final boolean mRequireNewLocation;
    /**
     * The blur radius (in meters) that will be used to blur the location for privacy reasons
     */
    private int mBlurRadius;
    /**
     * The LocationListener instance used internally to listen for location updates
     */
    private LocationListener mLocationListener;
    /**
     * The current location with latitude, longitude, speed and altitude
     */
    private Location mPosition;
    private Listener mListener;

    /**
     * Constructs a new instance with default granularity, mode and interval
     *
     * @param context the Context reference to get the system service from
     */
    public LocationUtils(final Context context) {
        this(context, false);
    }

    /**
     * Constructs a new instance with default mode and interval
     *
     * @param context     the Context reference to get the system service from
     * @param requireFine whether to require fine location or use coarse location
     */
    public LocationUtils(final Context context, final boolean requireFine) {
        this(context, requireFine, false);
    }

    /**
     * Constructs a new instance with default interval
     *
     * @param context     the Context reference to get the system service from
     * @param requireFine whether to require fine location or use coarse location
     * @param passive     whether to use passive mode (to save battery) or active mode
     */
    public LocationUtils(final Context context, final boolean requireFine, final boolean passive) {
        this(context, requireFine, passive, INTERVAL_DEFAULT);
    }

    /**
     * Constructs a new instance
     *
     * @param context     the Context reference to get the system service from
     * @param requireFine whether to require fine location or use coarse location
     * @param passive     whether to use passive mode (to save battery) or active mode
     * @param interval    the interval to request new location updates after (in milliseconds) where longer intervals save battery
     */
    public LocationUtils(final Context context, final boolean requireFine, final boolean passive, final long interval) {
        this(context, requireFine, passive, interval, false);
    }

    /**
     * Constructs a new instance
     *
     * @param context            the Context reference to get the system service from
     * @param requireFine        whether to require fine location or use coarse location
     * @param passive            whether to use passive mode (to save battery) or active mode
     * @param interval           the interval to request new location updates after (in milliseconds) where longer intervals save battery
     * @param requireNewLocation whether to require a new location (`true`) or accept old (last known) locations as well (`false`)
     */
    public LocationUtils(final Context context, final boolean requireFine, final boolean passive, final long interval, final boolean requireNewLocation) {
        mLocationManager = (LocationManager) context.getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        mRequireFine = requireFine;
        mPassive = passive;
        mInterval = interval;
        mRequireNewLocation = requireNewLocation;

        if (!mRequireNewLocation) {
            mPosition = getCachedPosition();
            cachePosition();
        }
    }

    /**
     * Attaches or detaches a listener that informs about certain events
     *
     * @param listener the `LocationUtils.Listener` instance to attach or `null` to detach
     */
    public void setListener(final Listener listener) {
        mListener = listener;
    }

    /**
     * Whether the device has location access enabled in the settings
     *
     * @return whether location access is enabled or not
     */
    public boolean hasLocationEnabled() {
        return hasLocationEnabled(getProviderName());
    }

    private boolean hasLocationEnabled(final String providerName) {
        try {
            return mLocationManager.isProviderEnabled(providerName);
        } catch (Exception e) {
            return false;
        }
    }

    private FusedLocationProviderClient mFusedLocationClient;

    /**
     * Starts updating the location and requesting new updates after the defined interval
     */
    @SuppressLint("MissingPermission")
    public void beginUpdates(Activity activity) {
        /*Ref : https://github.com/rrifafauzikomara/GoogleMapsSample*/
        endUpdates();

        if (!mRequireNewLocation) {
            mPosition = getCachedPosition();
        }

        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(300); // two minute interval
        mLocationRequest.setFastestInterval(300);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(activity);
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, null);
    }

    @SuppressLint("MissingPermission")
    public void beginUpdates1() {
        if (mLocationListener != null) {
            endUpdates();
        }

        if (!mRequireNewLocation) {
            mPosition = getCachedPosition();
        }

        mLocationListener = createLocationListener();
        mLocationManager.requestLocationUpdates(getProviderName(), mInterval, 0, mLocationListener);
    }

    /**
     * Stops the location updates when they aren't needed anymore so that battery can be saved
     */
    private void endUpdates() {
        /*Ref : https://github.com/rrifafauzikomara/GoogleMapsSample*/
        if (mFusedLocationClient != null) {
            mFusedLocationClient.removeLocationUpdates(mLocationCallback);
            mFusedLocationClient = null;
        }
    }

    public void endUpdates1() {
        if (mLocationListener != null) {
            mLocationManager.removeUpdates(mLocationListener);
            mLocationListener = null;
        }
    }

    private LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            List<Location> locationList = locationResult.getLocations();
            if (locationList.size() > 0) {
                //The last location in the list is the newest
                Location location = locationList.get(locationList.size() - 1);
                mPosition = location;
                cachePosition();

                Location location1 = getCachedPosition1();
                if (location1 != null) {
                    // in meters
                    double distance = calculateDistance(new Point(location1.getLatitude(), location1.getLongitude()),
                            new Point(location.getLatitude(), location.getLongitude()));
                    if (distance >/*500*/0) {
                        if (mListener != null) {
                            mListener.canUpdateLocationToServer();
                            cachePosition1();
                        }
                    }
                }

                if (mListener != null) {
                    mListener.onPositionChanged();
                }

                if (mListener != null) {
                    mListener.canUpdateLocationToFBDB();
                }
            }
        }
    };

    /**
     * Blurs the specified location with the defined blur radius or returns an unchanged location if no blur radius is set
     *
     * @param originalLocation the original location received from the device
     * @return the blurred location
     */
    private Location blurWithRadius(final Location originalLocation) {
        if (mBlurRadius <= 0) {
            return originalLocation;
        } else {
            Location newLocation = new Location(originalLocation);

            double blurMeterLong = calculateRandomOffset(mBlurRadius) / SQUARE_ROOT_TWO;
            double blurMeterLat = calculateRandomOffset(mBlurRadius) / SQUARE_ROOT_TWO;

            newLocation.setLongitude(newLocation.getLongitude() + meterToLongitude(blurMeterLong, newLocation.getLatitude()));
            newLocation.setLatitude(newLocation.getLatitude() + meterToLatitude(blurMeterLat));

            return newLocation;
        }
    }

    /**
     * For any radius `n`, calculate a random offset in the range `[-n, n]`
     *
     * @param radius the radius
     * @return the random offset
     */
    private static int calculateRandomOffset(final int radius) {
        return mRandom.nextInt((radius + 1) * 2) - radius;
    }

    /**
     * Returns the current position as a Point instance
     *
     * @return the current location (if any) or `null`
     */
    public Point getPosition() {
        if (mPosition == null) {
            return null;
        } else {
            Location position = blurWithRadius(mPosition);
            return new Point(position.getLatitude(), position.getLongitude());
        }
    }

    /**
     * Returns the latitude of the current location
     *
     * @return the current latitude (if any) or `0`
     */
    public double getLatitude() {
        if (mPosition == null) {
            return 0.0f;
        } else {
            Location position = blurWithRadius(mPosition);
            return position.getLatitude();
        }
    }

    /**
     * Returns the longitude of the current location
     *
     * @return the current longitude (if any) or `0`
     */
    public double getLongitude() {
        if (mPosition == null) {
            return 0.0f;
        } else {
            Location position = blurWithRadius(mPosition);
            return position.getLongitude();
        }
    }

    /**
     * Returns the timestamp of the current location as a number of milliseconds since January 1, 1970 (UTC)
     *
     * @return the timestamp (if any) or `0`
     */
    public long getTimestampInMilliseconds() {
        if (mPosition == null) {
            return 0L;
        } else {
            return mPosition.getTime();
        }
    }

    /**
     * Returns the elapsed time since system boot of the current location in nanoseconds
     *
     * @return the elapsed time (if any) or `0`
     */
    public long getElapsedTimeInNanoseconds() {
        if (mPosition == null) {
            return 0L;
        } else {
            if (Build.VERSION.SDK_INT >= 17) {
                return mPosition.getElapsedRealtimeNanos();
            } else {
                return (SystemClock.elapsedRealtime() + getTimestampInMilliseconds() - System.currentTimeMillis()) * 1000000;
            }
        }
    }

    /**
     * Returns the current speed
     *
     * @return the current speed (if detected) or `0`
     */
    public float getSpeed() {
        if (mPosition == null) {
            return 0.0f;
        } else {
            return mPosition.getSpeed();
        }
    }

    /**
     * Returns the current altitude
     *
     * @return the current altitude (if detected) or `0`
     */
    public double getAltitude() {
        if (mPosition == null) {
            return 0.0f;
        } else {
            return mPosition.getAltitude();
        }
    }

    /**
     * Sets the blur radius (in meters) to use for privacy reasons
     *
     * @param blurRadius the blur radius (in meters)
     */
    public void setBlurRadius(final int blurRadius) {
        mBlurRadius = blurRadius;
    }

    /**
     * Creates a new LocationListener instance used internally to listen for location updates
     *
     * @return the new LocationListener instance
     */
    private LocationListener createLocationListener() {
        return new LocationListener() {

            @Override
            public void onLocationChanged(Location location) {
                mPosition = location;
                cachePosition();

                Location location1 = getCachedPosition1();
                if (location1 != null) {
                    // in meters
                    double distance = calculateDistance(new Point(location1.getLatitude(), location1.getLongitude()),
                            new Point(location.getLatitude(), location.getLongitude()));
                    if (distance > 500) {
                        if (mListener != null) {
                            mListener.canUpdateLocationToServer();
                            cachePosition1();
                        }
                    }
                }

                if (mListener != null) {
                    mListener.canUpdateLocationToFBDB();
                }

                if (mListener != null) {
                    mListener.onPositionChanged();
                }
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

        };
    }

    /**
     * Returns the name of the location provider that matches the specified settings
     *
     * @return the provider's name
     */
    private String getProviderName() {
        return getProviderName(mRequireFine);
    }

    /**
     * Returns the name of the location provider that matches the specified settings and depends on the given granularity
     *
     * @param requireFine whether to require fine location or use coarse location
     * @return the provider's name
     */
    private String getProviderName(final boolean requireFine) {
        // if fine location (GPS) is required
        if (requireFine) {
            // we just have to decide between active and passive mode

            if (mPassive) {
                return PROVIDER_FINE_PASSIVE;
            } else {
                return PROVIDER_FINE;
            }
        }
        // if both fine location (GPS) and coarse location (network) are acceptable
        else {
            // if we can use coarse location (network)
            if (hasLocationEnabled(PROVIDER_COARSE)) {
                // if we wanted passive mode
                if (mPassive) {
                    // throw an exception because this is not possible
                    throw new RuntimeException("There is no passive provider for the coarse location");
                }
                // if we wanted active mode
                else {
                    // use coarse location (network)
                    return PROVIDER_COARSE;
                }
            }
            // if coarse location (network) is not available
            else {
                // if we can use fine location (GPS)
                if (hasLocationEnabled(PROVIDER_FINE) || hasLocationEnabled(PROVIDER_FINE_PASSIVE)) {
                    // we have to use fine location (GPS) because coarse location (network) was not available
                    return getProviderName(true);
                }
                // no location is available so return the provider with the minimum permission level
                else {
                    return PROVIDER_COARSE;
                }
            }
        }
    }

    /**
     * Returns the last position from the cache
     *
     * @return the cached position
     */
    @SuppressLint("MissingPermission")
    private Location getCachedPosition() {
        if (mCachedPosition != null) {
            return mCachedPosition;
        } else {
            try {
                return mLocationManager.getLastKnownLocation(getProviderName());
            } catch (Exception e) {
                return null;
            }
        }
    }

    @SuppressLint("MissingPermission")
    private Location getCachedPosition1() {
        if (mCachedPosition1 != null) {
            return mCachedPosition1;
        } else {
            try {
                return mLocationManager.getLastKnownLocation(getProviderName());
            } catch (Exception e) {
                return null;
            }
        }
    }

    /**
     * Caches the current position
     */
    private void cachePosition() {
        if (mPosition != null) {
            mCachedPosition = mPosition;
        }
    }

    private void cachePosition1() {
        if (mPosition != null) {
            mCachedPosition1 = mPosition;
        }
    }

    /**
     * Opens the device's settings screen where location access can be enabled
     *
     * @param context the Context reference to start the Intent from
     */
    public static void openSettings(final Context context) {
        context.startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
    }

    /**
     * Converts a difference in latitude to a difference in kilometers (rough estimation)
     *
     * @param latitude the latitude (difference)
     * @return the kilometers (difference)
     */
    public static double latitudeToKilometer(double latitude) {
        return latitude * LATITUDE_TO_KILOMETER;
    }

    /**
     * Converts a difference in kilometers to a difference in latitude (rough estimation)
     *
     * @param kilometer the kilometers (difference)
     * @return the latitude (difference)
     */
    public static double kilometerToLatitude(double kilometer) {
        return kilometer / latitudeToKilometer(1.0f);
    }

    /**
     * Converts a difference in latitude to a difference in meters (rough estimation)
     *
     * @param latitude the latitude (difference)
     * @return the meters (difference)
     */
    public static double latitudeToMeter(double latitude) {
        return latitudeToKilometer(latitude) * KILOMETER_TO_METER;
    }

    /**
     * Converts a difference in meters to a difference in latitude (rough estimation)
     *
     * @param meter the meters (difference)
     * @return the latitude (difference)
     */
    public static double meterToLatitude(double meter) {
        return meter / latitudeToMeter(1.0f);
    }

    /**
     * Converts a difference in longitude to a difference in kilometers (rough estimation)
     *
     * @param longitude the longitude (difference)
     * @param latitude  the latitude (absolute)
     * @return the kilometers (difference)
     */
    public static double longitudeToKilometer(double longitude, double latitude) {
        return longitude * LONGITUDE_TO_KILOMETER_AT_ZERO_LATITUDE * Math.cos(Math.toRadians(latitude));
    }

    /**
     * Converts a difference in kilometers to a difference in longitude (rough estimation)
     *
     * @param kilometer the kilometers (difference)
     * @param latitude  the latitude (absolute)
     * @return the longitude (difference)
     */
    public static double kilometerToLongitude(double kilometer, double latitude) {
        return kilometer / longitudeToKilometer(1.0f, latitude);
    }

    /**
     * Converts a difference in longitude to a difference in meters (rough estimation)
     *
     * @param longitude the longitude (difference)
     * @param latitude  the latitude (absolute)
     * @return the meters (difference)
     */
    public static double longitudeToMeter(double longitude, double latitude) {
        return longitudeToKilometer(longitude, latitude) * KILOMETER_TO_METER;
    }

    /**
     * Converts a difference in meters to a difference in longitude (rough estimation)
     *
     * @param meter    the meters (difference)
     * @param latitude the latitude (absolute)
     * @return the longitude (difference)
     */
    public static double meterToLongitude(double meter, double latitude) {
        return meter / longitudeToMeter(1.0f, latitude);
    }

    /**
     * Calculates the difference from the start position to the end position (in meters)
     *
     * @param start the start position
     * @param end   the end position
     * @return the distance in meters
     */
    public static double calculateDistance(Point start, Point end) {
        return calculateDistance(start.latitude, start.longitude, end.latitude, end.longitude);
    }

    /**
     * Calculates the difference from the start position to the end position (in meters)
     *
     * @param startLatitude  the latitude of the start position
     * @param startLongitude the longitude of the start position
     * @param endLatitude    the latitude of the end position
     * @param endLongitude   the longitude of the end position
     * @return the distance in meters
     */
    public static double calculateDistance(double startLatitude, double startLongitude, double endLatitude, double endLongitude) {
        float[] results = new float[3];
        Location.distanceBetween(startLatitude, startLongitude, endLatitude, endLongitude, results);
        return results[0];
    }

    public static void getAddressFromLocation(final Context context,
                                              final double latitude,
                                              final double longitude,
                                              final Handler handler) {
        Thread thread = new Thread() {
            @Override
            public void run() {
                Geocoder geocoder = new Geocoder(context, Locale.getDefault());
                String result = null;
                try {
                    List<Address> addressList = geocoder.getFromLocation(
                            latitude, longitude, 1);
                    if (addressList != null && addressList.size() > 0) {
                        Address address = addressList.get(0);
                        StringBuilder sb = new StringBuilder();
                        for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                            sb.append(address.getAddressLine(i)).append(", ");
                        }
                        sb.append(address.getLocality()).append(", ");
                        sb.append(address.getPostalCode()).append(", ");
                        sb.append(address.getCountryName());
                        result = sb.toString();
                    }
                } catch (IOException e) {
                    Log.e("GeoCoder_", "Unable connect to GeoCoder", e);
                } finally {
                    Message message = Message.obtain();
                    message.setTarget(handler);
                    if (result != null) {
                        message.what = 1;
                        Bundle bundle = new Bundle();
                        bundle.putString("address", result);
                        message.setData(bundle);
                    } else {
                        message.what = 1;
                        Bundle bundle = new Bundle();
                        result = latitude + ", " + longitude;
                        bundle.putString("address", result);
                        message.setData(bundle);
                    }
                    message.sendToTarget();
                }
            }
        };
        thread.start();
    }

    public  void getAddressFromLocation(final Context context) {
        double latitude=getLatitude();
        double longitude=getLongitude();
        Thread thread = new Thread() {
            @Override
            public void run() {
                Geocoder geocoder = new Geocoder(context, Locale.getDefault());
                String result = null;
                try {
                    List<Address> addressList = geocoder.getFromLocation(latitude, longitude, 1);
                    if (addressList != null && addressList.size() > 0) {
                        Address address = addressList.get(0);
                        StringBuilder sb = new StringBuilder();
                        for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                            sb.append(address.getAddressLine(i)).append(", ");
                        }
                        sb.append(address.getLocality()).append(" ");
                       // sb.append(address.getPostalCode()).append(", ");
                        //sb.append(address.getCountryName());
                        result = sb.toString();
                       // AppPreference.INSTANCE.setCurrentLocation(result);
                    }
                } catch (IOException e) {
                    Log.e("GeoCoder_", "Unable connect to GeoCoder", e);
                }
            }
        };
        thread.start();
    }
}
