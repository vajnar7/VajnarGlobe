package com.vajnar.vajnargnss.logger;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.GnssClock;
import android.location.GnssMeasurement;
import android.location.GnssMeasurementsEvent;
import android.location.LocationManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import androidx.annotation.NonNull;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

public class GnssLoggerEngine {

    private static final String TAG = "GnssLoggerEngine";
    private static final double SPEED_OF_LIGHT_M_PER_S = 299792458.0;
    private static final double WEEK_IN_NANOS = 604800e9; // 7 days in nanoseconds

    private final LocationManager locationManager;
    private GnssMeasurementsEvent.Callback gnssCallback;
    private Context context;

    public GnssLoggerEngine(Context context)
    {
        this.locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        this.context = context;
    }

    @SuppressLint("MissingPermission")
    public void startLogging() {
        if (locationManager == null) return;

        gnssCallback = new GnssMeasurementsEvent.Callback() {
            @Override
            public void onGnssMeasurementsReceived(@NonNull GnssMeasurementsEvent event) {
                processGnssEpoch(event);
            }
        };

        // Register the callback on the main looper or a background handler thread
        boolean success = locationManager.registerGnssMeasurementsCallback(
                gnssCallback,
                new Handler(Looper.getMainLooper())
        );

        Log.d(TAG, "GNSS Measurement Callback registered: " + success);
    }

    public void stopLogging() {
        if (locationManager != null && gnssCallback != null) {
            locationManager.unregisterGnssMeasurementsCallback(gnssCallback);
            Log.d(TAG, "GNSS Measurement Callback unregistered");
        }
    }

    private void processGnssEpoch(GnssMeasurementsEvent event) {
        GnssClock clock = event.getClock();

        // 1. Calculate Receiver GPS Time (t_rx) in nanoseconds
        // Note: FullBiasNanos is a negative number representing hardware clock offset from 1980
        if (!clock.hasFullBiasNanos()) {
            Log.w(TAG, "No FullBiasNanos present; receiver clock time not calibrated.");
            return;
        }

        long timeNanos = clock.getTimeNanos();
        long fullBiasNanos = clock.getFullBiasNanos();
        double biasNanos = clock.hasBiasNanos() ? clock.getBiasNanos() : 0.0;

        // Total hardware receiver clock time in nanoseconds
        double t_rx_nanos = timeNanos - (fullBiasNanos + biasNanos);

        // 2. Iterate through each satellite observation in the current epoch
        for (GnssMeasurement measurement : event.getMeasurements()) {

            // Filter out un-synced observations (must have code lock / time of week decoded)
            int state = measurement.getState();
            boolean isCodeLocked = (state & GnssMeasurement.STATE_CODE_LOCK) != 0;
            boolean isTowDecoded = (state & GnssMeasurement.STATE_TOW_DECODED) != 0;

            if (!isCodeLocked || !isTowDecoded) {
                continue; // Skip invalid measurements
            }

            // A. Compute Pseudorange (C)
            double pseudorangeMeters = calculatePseudorange(measurement, t_rx_nanos);

            // B. Compute Carrier Phase (L) in cycles
            double carrierPhaseCycles = calculateCarrierPhase(measurement);

            // Log output details
            int svid = measurement.getSvid();
            int constellation = measurement.getConstellationType();
            float signalStrength = (float) measurement.getCn0DbHz();

            Log.i(TAG, String.format(
                    "Constellation: %d | SVID: %2d | C/N0: %.1f | Pseudorange: %.3f m | Carrier Phase: %.3f cycles",
                    constellation, svid, signalStrength, pseudorangeMeters, carrierPhaseCycles
            ));
        }
    }

    private double calculatePseudorange(GnssMeasurement measurement, double t_rx_nanos) {
        // Satellite transmission time in nanoseconds (time of week)
        long t_tx_nanos = measurement.getReceivedSvTimeNanos();

        // Convert receiver time (t_rx) into modulo GPS Week Time in nanoseconds
        double t_rx_week_nanos = t_rx_nanos % WEEK_IN_NANOS;

        // Signal travel time in nanoseconds
        double travelTimeNanos = t_rx_week_nanos - t_tx_nanos;

        // Check for GPS week roll-over bounds
        if (travelTimeNanos < 0) {
            travelTimeNanos += WEEK_IN_NANOS;
        }

        // Multiply travel time (in seconds) by speed of light (m/s)
        return (travelTimeNanos * 1e-9) * SPEED_OF_LIGHT_M_PER_S;
    }

    private double calculateCarrierPhase(GnssMeasurement measurement) {
        int adrState = measurement.getAccumulatedDeltaRangeState();

        // Check if the Accumulated Delta Range (ADR) is valid and free of cycle slips
        boolean isValid = (adrState & GnssMeasurement.ADR_STATE_VALID) != 0;
        boolean isReset = (adrState & GnssMeasurement.ADR_STATE_RESET) != 0;
        boolean hasCycleSlip = (adrState & GnssMeasurement.ADR_STATE_CYCLE_SLIP) != 0;

        if (!isValid || isReset || hasCycleSlip) {
            return Double.NaN; // Carrier phase invalid or interrupted
        }

        // 1. Get ADR value in meters
        double adrMeters = measurement.getAccumulatedDeltaRangeMeters();

        // 2. Obtain Carrier Frequency (Hz) to determine signal wavelength (λ)
        double carrierFrequencyHz = 1.57542e9; // Default L1 frequency (1575.42 MHz)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && measurement.hasCarrierFrequencyHz()) {
            carrierFrequencyHz = measurement.getCarrierFrequencyHz();
        }

        // Wavelength = c / f
        double wavelengthMeters = SPEED_OF_LIGHT_M_PER_S / carrierFrequencyHz;

        // 3. Convert ADR meters to carrier cycles (L = ADR / λ)
        return adrMeters / wavelengthMeters;
    }
}
