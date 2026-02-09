package com.vajnar.vajnargnss.logger;

import android.content.Context;
import android.location.GnssMeasurementsEvent;
import android.location.LocationListener;
import android.location.LocationManager;
import android.view.View;

import java.util.concurrent.TimeUnit;

// TODO: !!!! Najprej nared measurements logger, potem pa ce se rab se NMEA, Status, Navigation messages in Locations
//  Poglej to v raw-gps projekt
public abstract class GPSProvider extends View implements LocationListener
{
    private static final long LOCATION_RATE_NETWORK_MS = TimeUnit.SECONDS.toMillis(60L);
    private static final long LOCATION_RATE_GPS_MS = TimeUnit.SECONDS.toMillis(1L);
    private final LocationManager mLocationManager;
    private final boolean mLogMeasurements = true;


    private final MeasurementListener logger;

    private final GnssMeasurementsEvent.Callback gnssMeasurementsEventListener =
            new GnssMeasurementsEvent.Callback() {
                @Override
                public void onGnssMeasurementsReceived(GnssMeasurementsEvent event) {
                    if (mLogMeasurements) {
                        logger.onGnssMeasurementsReceived(event);
                    }
                }

                @Override
                public void onStatusChanged(int status) {
                    if (mLogMeasurements) {
                        logger.onGnssMeasurementsStatusChanged(status);
                    }
                }
            };

    public GPSProvider(Context context)
    {
        super(context);

        this.logger = new GnssLogger(context);
        mLocationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    }

    protected void registerLocation()
    {
        boolean isGpsProviderEnabled = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (isGpsProviderEnabled) {
            try {
                mLocationManager.requestLocationUpdates(
                        LocationManager.NETWORK_PROVIDER,
                        LOCATION_RATE_NETWORK_MS,
                        0.0f /* minDistance */,
                        this);
                mLocationManager.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER,
                        LOCATION_RATE_GPS_MS,
                        0.0f /* minDistance */,
                        this);
            } catch (SecurityException e) {
                // TODO(adaext)
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
            }
        }
        logRegistration("LocationUpdates", isGpsProviderEnabled);
    }

    protected void registerMeasurements()
    {
        try {
            logRegistration(
                    "GnssMeasurements",
                    mLocationManager.registerGnssMeasurementsCallback(gnssMeasurementsEventListener));
        } catch (SecurityException e) {
            // TODO(adaext):
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
        }
        logRegistration("Measurements", true);
    }

    private void logRegistration(String listener, boolean result)
    {
        logger.onListenerRegistration(listener, result);
    }
}
