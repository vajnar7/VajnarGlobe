package com.vajnar.vajnargnss.logger;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.GnssMeasurementsEvent;
import android.location.GnssNavigationMessage;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

public abstract class GPSProvider extends View implements View.OnTouchListener
{
    private static final long LOCATION_RATE_GPS_MS = TimeUnit.SECONDS.toMillis(1L);
    private static final long LOCATION_RATE_NETWORK_MS = TimeUnit.SECONDS.toMillis(60L);

    private LocationManager locationManager;
    Executor executor;
    protected GnssNavigationMessage.Callback callbackNavigation;
    protected GnssMeasurementsEvent.Callback callbackMeasurement;
    protected LocationListener callbackLocation;
    protected GnssLogger logger;
    private boolean isRegistered = false;

    public GPSProvider(Context ctx)
    {
        super(ctx);
        initGPSService(ctx);
    }

    protected void initGPSService(Context ctx)
    {
        logger = new GnssLogger(ctx);
        locationManager = (LocationManager) ctx.getSystemService(Context.LOCATION_SERVICE);
        executor = ContextCompat.getMainExecutor(ctx);

        callbackMeasurement = new GnssMeasurementsEvent.Callback() {
            @Override
            public void onGnssMeasurementsReceived(GnssMeasurementsEvent event) {
                logger.onGnssMeasurementsReceived(event);
            }
        };

        callbackNavigation = new GnssNavigationMessage.Callback() {
            @Override
            public void onGnssNavigationMessageReceived(GnssNavigationMessage event) {
                logger.onGnssNavigationMessageReceived(event);
            }
        };

        callbackLocation = location -> {
            logger.onLocationChanged(location);
            onLocationChanged(location);
        };
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        register(getContext());
    }

    protected abstract void onLocationChanged(@NonNull Location loc);

    public void register(Context ctx)
    {
        if (isRegistered) return;

        if (ActivityCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        locationManager.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER,
                LOCATION_RATE_NETWORK_MS,
                0.0f /* minDistance */,
                callbackLocation);
        locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                LOCATION_RATE_GPS_MS,
                0.0f /* minDistance */,
                callbackLocation);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            locationManager.registerGnssMeasurementsCallback(executor, callbackMeasurement);
            locationManager.registerGnssNavigationMessageCallback(executor, callbackNavigation);
        }
        isRegistered = true;
    }

    public GnssLogger getLogger() {
        return logger;
    }
}
