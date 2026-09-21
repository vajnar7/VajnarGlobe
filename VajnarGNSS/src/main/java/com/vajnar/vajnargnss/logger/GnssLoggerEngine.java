package com.vajnar.vajnargnss.logger;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.GnssClock;
import android.location.GnssMeasurement;
import android.location.GnssMeasurementsEvent;
import android.location.GnssStatus;
import android.location.LocationManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class GnssLoggerEngine {

    private static final String TAG = "GnssLoggerEngine";
    private static final double SPEED_OF_LIGHT_M_PER_S = 299792458.0;
    private static final double WEEK_IN_NANOS = 604800e9; // 7 days in nanoseconds

    private final LocationManager locationManager;
    private GnssMeasurementsEvent.Callback gnssCallback;
    private final Context context;
    private final List<Rinex3Writer.SatObservation> epochObs = new ArrayList<>();

    public GnssLoggerEngine(Context context)
    {
        this.locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        this.context = context;
    }

    public void startLogging() throws IOException {
        if (locationManager == null) return;

        gnssCallback = new GnssMeasurementsEvent.Callback() {
            @Override
            public void onGnssMeasurementsReceived(@NonNull GnssMeasurementsEvent event) {
                processGnssEpoch(event);
            }
        };

        // Create RINEX file writer stream
        FileWriter fileWriter = new FileWriter(new File(context.getFilesDir(), "rover.obs"));
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

        Rinex3Writer rinexWriter = new Rinex3Writer();

        // 1. Write Header once at start
        rinexWriter.writeHeader(bufferedWriter);

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

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

    public void processGnssEpoch(GnssMeasurementsEvent event) {
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

            double cn0 = measurement.getCn0DbHz();
            double dopplerHz = calculateDopplerHz(measurement);
            String satPrn = formatSatPrn(constellation, svid);
            int lli = calculateLli(measurement);
            Log.i(TAG, String.format(
                    "RINEX params satPrn: %s | Pseudorange: %.3f m | Carrier Phase Cycles: %.3f m | Doppler: %.3f Hz | Signal Strength:  %.3f | LLI: %d",
                    satPrn, pseudorangeMeters, carrierPhaseCycles, dopplerHz, cn0, lli)
            );
            epochObs.add(new Rinex3Writer.SatObservation(
                    satPrn,
                    pseudorangeMeters,
                    carrierPhaseCycles,
                    dopplerHz,
                    cn0,
                    lli));
        }
    }

    private int calculateLli(GnssMeasurement measurement) {
        int adrState = measurement.getAccumulatedDeltaRangeState();

        // Check if ADR lost lock or was reset between epochs
        boolean hasCycleSlip = (adrState & GnssMeasurement.ADR_STATE_CYCLE_SLIP) != 0;
        boolean isReset = (adrState & GnssMeasurement.ADR_STATE_RESET) != 0;

        if (hasCycleSlip || isReset) {
            return 1; // Flag LLI = 1 (Loss of Lock / Cycle Slip)
        }

        // Optional: Check for half-cycle ambiguity (if available on API 28+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            boolean halfCycleUncertain = (adrState & GnssMeasurement.ADR_STATE_HALF_CYCLE_REPORTED) == 0;
            if (halfCycleUncertain) {
                return 2; // Flag LLI = 2 (Half-cycle ambiguity)
            }
        }

        return 0; // Default: 0 = Continuous valid tracking
    }

    private double calculateDopplerHz(GnssMeasurement measurement) {
        // 1. Get Pseudorange Rate in m/s
        double pseudorangeRateMps = measurement.getPseudorangeRateMetersPerSecond();

        // 2. Get Carrier Frequency (Hz)
        double carrierFrequencyHz = 1.57542e9; // Fallback to L1 frequency
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && measurement.hasCarrierFrequencyHz()) {
            carrierFrequencyHz = measurement.getCarrierFrequencyHz();
        }

        // 3. Compute Wavelength λ = c / f
        double wavelengthMeters = SPEED_OF_LIGHT_M_PER_S / carrierFrequencyHz;

        // 4. Convert Pseudorange Rate (m/s) to Doppler (Hz)
        return -pseudorangeRateMps / wavelengthMeters;
    }

    private String formatSatPrn(int constellationType, int svid) {
        String prefix;
        int prnNumber = svid;

        switch (constellationType) {
            case GnssStatus.CONSTELLATION_GPS:
                prefix = "G";
                break;

            case GnssStatus.CONSTELLATION_GLONASS:
                prefix = "R";
                // Note: If SVID is 255 (slot number unknown), handle exception if needed
                break;

            case GnssStatus.CONSTELLATION_GALILEO:
                prefix = "E";
                break;

            case GnssStatus.CONSTELLATION_BEIDOU:
                prefix = "C";
                break;

            case GnssStatus.CONSTELLATION_QZSS:
                prefix = "J";
                // Android QZSS SVIDs usually start at 1 or 193 depending on API version
                if (prnNumber > 192) {
                    prnNumber -= 192; // Maps 193 -> 01, 194 -> 02
                }
                break;

            case GnssStatus.CONSTELLATION_SBAS:
                prefix = "S";
                // SBAS PRNs in RINEX are offset by 100 (e.g., PRN 137 becomes S37)
                if (prnNumber > 100) {
                    prnNumber -= 100;
                }
                break;

            case GnssStatus.CONSTELLATION_IRNSS: // NavIC
                prefix = "I";
                break;

            default:
                return null; // Unknown or unsupported constellation
        }

        // Format string as Letter + 2-digit zero-padded number (e.g., "G01")
        return String.format(Locale.US, "%s%02d", prefix, prnNumber);
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
