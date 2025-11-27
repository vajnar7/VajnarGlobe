package com.vajnar.vajnargnss.logger;

import android.content.Context;
import android.location.GnssClock;
import android.location.GnssMeasurement;
import android.location.GnssMeasurementsEvent;
import android.location.GnssNavigationMessage;
import android.location.GnssStatus;
import android.location.Location;
import android.os.Build;
import android.os.SystemClock;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class GnssLogger implements MeasurementListener
{
    private static final String TAG = "FileLogger";
    private static final String FILE_PREFIX = "gnss_log";
    private static final String ERROR_WRITING_FILE = "Problem writing to file.";
    private static final String COMMENT_START = "# ";
    private static final char RECORD_DELIMITER = ',';
    private static final String VERSION_TAG = "Version: ";
    private static final int MAX_FILES_STORED = 100;
    private static final int MINIMUM_USABLE_FILE_SIZE_BYTES = 1000;
    private final Object fileLock = new Object();
    private BufferedWriter fileWriter;
    protected Context ctx;
    private File file;


    public GnssLogger(Context ctx)
    {
        this.ctx = ctx;
    }
    public void startNewLog() {
        synchronized (fileLock) {
            File baseDirectory = new File(ctx.getFilesDir(), FILE_PREFIX);
            if (!baseDirectory.exists() && !baseDirectory.mkdirs()) {
                logError("Could not create directory");
                return;
            }

            SimpleDateFormat formatter = new SimpleDateFormat("yyy_MM_dd_HH_mm_ss", Locale.GERMAN);
            Date now = new Date();
            String fileName = String.format("%s_%s.txt", FILE_PREFIX, formatter.format(now));
            File currentFile = new File(baseDirectory, fileName);
            String currentFilePath = currentFile.getAbsolutePath();
            BufferedWriter currentFileWriter;
            try {
                currentFileWriter = new BufferedWriter(new FileWriter(currentFile));
            } catch (IOException e) {
                logException("Could not open file zumra: " + currentFilePath, e);
                return;
            }

            // initialize the contents of the file
            try {
                currentFileWriter.write(COMMENT_START);
                currentFileWriter.newLine();
                currentFileWriter.write(COMMENT_START);
                currentFileWriter.write("Header Description:");
                currentFileWriter.newLine();
                currentFileWriter.write(COMMENT_START);
                currentFileWriter.newLine();
                currentFileWriter.write(COMMENT_START);
                currentFileWriter.write(VERSION_TAG);
                String manufacturer = Build.MANUFACTURER;
                String model = Build.MODEL;
                String fileVersion =
                        ctx.getString(R.string.app_version)
                                + " Platform: "
                                + Build.VERSION.RELEASE
                                + " "
                                + "Manufacturer: "
                                + manufacturer
                                + " "
                                + "Model: "
                                + model;
                currentFileWriter.write(fileVersion);
                currentFileWriter.newLine();
                currentFileWriter.write(COMMENT_START);
                currentFileWriter.newLine();
                currentFileWriter.write(COMMENT_START);
                currentFileWriter.write(
                        "Raw,ElapsedRealtimeMillis,TimeNanos,LeapSecond,TimeUncertaintyNanos,FullBiasNanos,"
                                + "BiasNanos,BiasUncertaintyNanos,DriftNanosPerSecond,DriftUncertaintyNanosPerSecond,"
                                + "HardwareClockDiscontinuityCount,Svid,TimeOffsetNanos,State,ReceivedSvTimeNanos,"
                                + "ReceivedSvTimeUncertaintyNanos,Cn0DbHz,PseudorangeRateMetersPerSecond,"
                                + "PseudorangeRateUncertaintyMetersPerSecond,"
                                + "AccumulatedDeltaRangeState,AccumulatedDeltaRangeMeters,"
                                + "AccumulatedDeltaRangeUncertaintyMeters,CarrierFrequencyHz,CarrierCycles,"
                                + "CarrierPhase,CarrierPhaseUncertainty,MultipathIndicator,SnrInDb,"
                                + "ConstellationType,AgcDb");
                currentFileWriter.newLine();
                currentFileWriter.write(COMMENT_START);
                currentFileWriter.newLine();
                currentFileWriter.write(COMMENT_START);
                currentFileWriter.write(
                        "Fix,Provider,Latitude,Longitude,Altitude,Speed,Accuracy,(UTC)TimeInMs");
                currentFileWriter.newLine();
                currentFileWriter.write(COMMENT_START);
                currentFileWriter.newLine();
                currentFileWriter.write(COMMENT_START);
                currentFileWriter.write("Nav,Svid,Type,Status,MessageId,Sub-messageId,Data(Bytes)");
                currentFileWriter.newLine();
                currentFileWriter.write(COMMENT_START);
                currentFileWriter.newLine();
            } catch (IOException e) {
                logException("Count not initialize file: " + currentFilePath, e);
                return;
            }

            if (fileWriter != null) {
                try {
                    fileWriter.close();
                } catch (IOException e) {
                    logException("Unable to close all file streams.", e);
                    return;
                }
            }

            file = currentFile;
            fileWriter = currentFileWriter;
            Toast.makeText(ctx, "File opened: " + currentFilePath, Toast.LENGTH_SHORT).show();

            // To make sure that files do not fill up the external storage:
            // - Remove all empty files
            FileFilter filter = new FileToDeleteFilter(file);
            for (File existingFile : baseDirectory.listFiles(filter)) {
                existingFile.delete();
            }
            // - Trim the number of files with data
            File[] existingFiles = baseDirectory.listFiles();
            int filesToDeleteCount = existingFiles.length - MAX_FILES_STORED;
            if (filesToDeleteCount > 0) {
                Arrays.sort(existingFiles);
                for (int i = 0; i < filesToDeleteCount; ++i) {
                    existingFiles[i].delete();
                }
            }
        }
    }

    @Override
    public void onProviderEnabled(String provider)
    {
    }

    @Override
    public void onProviderDisabled(String provider)
    {
    }

    @Override
    public void onLocationChanged(Location location)
    {
        synchronized (fileLock) {
            if (fileWriter == null) {
                return;
            }
            String locationStream =
                    String.format(
                            Locale.US,
                            "Fix,%s,%f,%f,%f,%f,%f,%d",
                            location.getProvider(),
                            location.getLatitude(),
                            location.getLongitude(),
                            location.getAltitude(),
                            location.getSpeed(),
                            location.getAccuracy(),
                            location.getTime());
            try {
                fileWriter.write(locationStream);
                fileWriter.newLine();
            } catch (IOException e) {
                logException(ERROR_WRITING_FILE, e);
            }
        }
    }

    @Override
    public void onLocationStatusChanged(String provider, int status, Bundle extras)
    {
    }

    @Override
    public void onGnssMeasurementsReceived(GnssMeasurementsEvent event)
    {
        synchronized (fileLock) {
            if (fileWriter == null) {
                return;
            }
            GnssClock gnssClock = event.getClock();
            for (GnssMeasurement measurement : event.getMeasurements()) {
                try {
                    writeGnssMeasurementToFile(gnssClock, measurement);
                } catch (IOException e) {
                    logException(ERROR_WRITING_FILE, e);
                }
            }
        }
    }

    @Override
    public void onGnssMeasurementsStatusChanged(int status)
    {
    }

    @Override
    public void onGnssNavigationMessageReceived(GnssNavigationMessage navigationMessage)
    {
        synchronized (fileLock) {
            if (fileWriter == null) {
                return;
            }
            StringBuilder builder = new StringBuilder("Nav");
            builder.append(RECORD_DELIMITER);
            builder.append(navigationMessage.getSvid());
            builder.append(RECORD_DELIMITER);
            builder.append(navigationMessage.getType());
            builder.append(RECORD_DELIMITER);

            int status = navigationMessage.getStatus();
            builder.append(status);
            builder.append(RECORD_DELIMITER);
            builder.append(navigationMessage.getMessageId());
            builder.append(RECORD_DELIMITER);
            builder.append(navigationMessage.getSubmessageId());
            byte[] data = navigationMessage.getData();
            for (byte word : data) {
                builder.append(RECORD_DELIMITER);
                builder.append(word);
            }
            try {
                fileWriter.write(builder.toString());
                fileWriter.newLine();
            } catch (IOException e) {
                logException(ERROR_WRITING_FILE, e);
            }
        }
    }

    @Override
    public void onGnssNavigationMessageStatusChanged(int status)
    {
    }

    @Override
    public void onGnssStatusChanged(GnssStatus gnssStatus)
    {
    }

    @Override
    public void onListenerRegistration(String listener, boolean result) {}

    @Override
    public void onNmeaReceived(long timestamp, String s)
    {
        synchronized (fileLock) {
            if (fileWriter == null) {
                return;
            }
            String nmeaStream = String.format(Locale.US, "NMEA,%s,%d", s.trim(), timestamp);
            try {
                fileWriter.write(nmeaStream);
                fileWriter.newLine();
            } catch (IOException e) {
                logException(ERROR_WRITING_FILE, e);
            }
        }
    }

    @Override
    public void onTTFFReceived(long l) {}

    private void writeGnssMeasurementToFile(GnssClock clock, GnssMeasurement measurement)
            throws IOException {
        String clockStream =
                String.format(
                        "Raw,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s",
                        SystemClock.elapsedRealtime(),
                        clock.getTimeNanos(),
                        clock.hasLeapSecond() ? clock.getLeapSecond() : "",
                        clock.hasTimeUncertaintyNanos() ? clock.getTimeUncertaintyNanos() : "",
                        clock.getFullBiasNanos(),
                        clock.hasBiasNanos() ? clock.getBiasNanos() : "",
                        clock.hasBiasUncertaintyNanos() ? clock.getBiasUncertaintyNanos() : "",
                        clock.hasDriftNanosPerSecond() ? clock.getDriftNanosPerSecond() : "",
                        clock.hasDriftUncertaintyNanosPerSecond()
                                ? clock.getDriftUncertaintyNanosPerSecond()
                                : "",
                        clock.getHardwareClockDiscontinuityCount() + ",");
        fileWriter.write(clockStream);

        String measurementStream =
                String.format(
                        "%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s",
                        measurement.getSvid(),
                        measurement.getTimeOffsetNanos(),
                        measurement.getState(),
                        measurement.getReceivedSvTimeNanos(),
                        measurement.getReceivedSvTimeUncertaintyNanos(),
                        measurement.getCn0DbHz(),
                        measurement.getPseudorangeRateMetersPerSecond(),
                        measurement.getPseudorangeRateUncertaintyMetersPerSecond(),
                        measurement.getAccumulatedDeltaRangeState(),
                        measurement.getAccumulatedDeltaRangeMeters(),
                        measurement.getAccumulatedDeltaRangeUncertaintyMeters(),
                        measurement.hasCarrierFrequencyHz() ? measurement.getCarrierFrequencyHz() : "",
                        measurement.hasCarrierCycles() ? measurement.getCarrierCycles() : "",
                        measurement.hasCarrierPhase() ? measurement.getCarrierPhase() : "",
                        measurement.hasCarrierPhaseUncertainty()
                                ? measurement.getCarrierPhaseUncertainty()
                                : "",
                        measurement.getMultipathIndicator(),
                        measurement.hasSnrInDb() ? measurement.getSnrInDb() : "",
                        measurement.getConstellationType(),
                        Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
                                && measurement.hasAutomaticGainControlLevelDb()
                                ? measurement.getAutomaticGainControlLevelDb()
                                : "");
        fileWriter.write(measurementStream);
        fileWriter.newLine();
    }

    private void logException(String errorMessage, Exception e) {
        Log.e(MeasurementProvider.TAG + TAG, errorMessage, e);
        Toast.makeText(ctx, errorMessage, Toast.LENGTH_LONG).show();
    }

    private void logError(String errorMessage) {
        Log.e(MeasurementProvider.TAG + TAG, errorMessage);
        Toast.makeText(ctx, errorMessage, Toast.LENGTH_LONG).show();
    }

    private static class FileToDeleteFilter implements FileFilter
    {
        private final List<File> retainedFiles;

        public FileToDeleteFilter(File... retainedFiles) {
            this.retainedFiles = Arrays.asList(retainedFiles);
        }

        @Override
        public boolean accept(File pathname) {
            if (pathname == null || !pathname.exists()) {
                return false;
            }
            if (retainedFiles.contains(pathname)) {
                return false;
            }
            return pathname.length() < MINIMUM_USABLE_FILE_SIZE_BYTES;
        }
    }
}
