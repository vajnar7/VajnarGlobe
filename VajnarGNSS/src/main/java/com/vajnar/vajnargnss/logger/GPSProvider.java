package com.vajnar.vajnargnss.logger;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.SystemClock;
import android.view.View;

import androidx.annotation.NonNull;

import java.util.Objects;

public abstract class GPSProvider extends View implements LocationListener, View.OnTouchListener
{
    private boolean firstTime = true;
    private boolean mLogLocations = true;
    private boolean mLogNavigationMessages = true;
    private boolean mLogMeasurements = true;
    private boolean mLogStatuses = true;
    private boolean mLogNmeas = true;
    private long ttff = 0L;
    private long registrationTimeNanos = 0L;
    private long firstLocationTimeNanos = 0L;

    private MeasurementListener logger;

    public GPSProvider(Context context)
    {
        super(context);
    }

    @Override
    public void onLocationChanged(@NonNull Location location)
    {
        if (firstTime && Objects.equals(location.getProvider(), LocationManager.GPS_PROVIDER)) {
            if (mLogLocations) {
                firstLocationTimeNanos = SystemClock.elapsedRealtimeNanos();
                ttff = firstLocationTimeNanos - registrationTimeNanos;
                logger.onTTFFReceived(ttff);

            }
            firstTime = false;
        }
        if (mLogLocations) {
            logger.onLocationChanged(location);
        }
    }
}
