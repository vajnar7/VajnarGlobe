package com.vajnar.vajnargnss.logger;

import android.content.Context;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public final class LoggerTest
{
    private final Context context;
    public LoggerTest(Context ctx) throws IOException
    {
        context = ctx;
        writeData();
    }

    public void writeData() throws IOException {

        // Create RINEX file writer stream
        FileWriter fileWriter = new FileWriter(new File(context.getFilesDir(), "rover.obs"));
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

        Rinex3Writer rinexWriter = new Rinex3Writer();

        // 1. Write Header once at start
        rinexWriter.writeHeader(bufferedWriter);

        // 2. Add satellite observations extracted from GnssMeasurementsEvent
        List<Rinex3Writer.SatObservation> epochObs = new ArrayList<>();

        // Example GPS Satellite #1 observation
        epochObs.add(new Rinex3Writer.SatObservation(
                "G01",
                22415123.456,  // Pseudorange (m)
                117793421.123, // Carrier Phase (cycles)
                -1234.567,     // Doppler (Hz)
                42.5,           // C/N0 (dB-Hz)
                0
        ));

        // Example Galileo Satellite #5 observation
        epochObs.add(new Rinex3Writer.SatObservation(
                "E05",
                24102938.881,
                126782910.450,
                892.110,
                38.0,
                0
        ));

        // 3. Write Epoch (Timestamp: Sept 17, 2026, 08:17:02.000 UTC)
        rinexWriter.writeEpoch(bufferedWriter, 2026, 9, 17, 8, 17, 2.0000000, epochObs);
    }
}
