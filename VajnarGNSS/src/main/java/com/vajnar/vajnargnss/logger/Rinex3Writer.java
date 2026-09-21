package com.vajnar.vajnargnss.logger;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class Rinex3Writer {

    // Helper data structure for a single satellite observation
    public static class SatObservation {
        public String satPrn;       // e.g., "G01", "E05", "R12"
        public double pseudorange;  // C1C (meters), NaN if invalid
        public double carrierPhase; // L1C (cycles), NaN if invalid
        public double doppler;      // D1C (Hz), NaN if invalid
        public double cn0;          // S1C (dB-Hz), NaN if invalid
        public int lli;         // Loss-of-Lock Indicator (0-7)

        public SatObservation(String satPrn, double pseudorange, double carrierPhase, double doppler, double cn0, int lli) {
            this.satPrn = satPrn;
            this.pseudorange = pseudorange;
            this.carrierPhase = carrierPhase;
            this.doppler = doppler;
            this.cn0 = cn0;
            this.lli = lli;
        }
    }

    /**
     * Writes a standard RINEX 3.03 Header.
     */
    public void writeHeader(BufferedWriter writer) throws IOException {
        writeHeaderLine(writer, "     3.03           OBSERVATION DATA    M: Mixed            ", "RINEX VERSION / TYPE");
        writeHeaderLine(writer, padRight("AndroidLoggerEngine", 20) + padRight("User", 20) + "20260917 080000 UTC", "PGM / RUN BY / DATE");
        writeHeaderLine(writer, padRight("ANDROID_ROVER", 60), "MARKER NAME");
        writeHeaderLine(writer, padRight("0.0000", 20) + padRight("0.0000", 20) + padRight("0.0000", 20), "APPROX POSITION XYZ");

        // Define observation types for GPS (G) and Galileo (E): Pseudorange (C1C), Phase (L1C), Doppler (D1C), C/N0 (S1C)
        writeHeaderLine(writer, "G    4 C1C L1C D1C S1C                                      ", "SYS / # / OBS TYPES");
        writeHeaderLine(writer, "E    4 C1C L1C D1C S1C                                      ", "SYS / # / OBS TYPES");
        writeHeaderLine(writer, "R    4 C1C L1C D1C S1C                                      ", "SYS / # / OBS TYPES");

        writeHeaderLine(writer, "", "END OF HEADER");
        writer.flush();
    }

    /**
     * Writes a single measurement epoch into RINEX 3 format.
     *
     * @param writer Output writer stream
     * @param year, month, day, hour, minute, second Epoch timestamp (GPS Time)
     * @param obsList List of satellite observations tracked in this epoch
     */
    public void writeEpoch(BufferedWriter writer, int year, int month, int day,
                           int hour, int min, double sec, List<SatObservation> obsList) throws IOException {

        // 1. Write Epoch Header Line: > YYYY MM DD HH MM SS.SSSSSSS  0 N_SATS
        String epochHeader = String.format(Locale.US, "> %4d %02d %02d %02d %02d %10.7f  0 %2d",
                year, month, day, hour, min, sec, obsList.size());

        writer.write(epochHeader);
        writer.newLine();

        // 2. Write Observation Row for each satellite
        for (SatObservation obs : obsList) {
            StringBuilder sb = new StringBuilder();

            // Sat PRN (3 chars, e.g., "G01")
            sb.append(String.format(Locale.US, "%-3s", obs.satPrn));

            // Format C1C (Pseudorange)
            sb.append(formatObsValue(obs.pseudorange, 0));

            // Format L1C (Carrier Phase + LLI)
            sb.append(formatObsValue(obs.carrierPhase, obs.lli));

            // Format D1C (Doppler)
            sb.append(formatObsValue(obs.doppler, 0));

            // Format S1C (Signal Strength / C/N0)
            sb.append(formatObsValue(obs.cn0, 0));

            writer.write(sb.toString());
            writer.newLine();
        }
        writer.flush();
    }

    /**
     * Formats an individual observation field into the mandatory RINEX 16-character field:
     * %14.3f (14 chars right-aligned value) + 1 char LLI + 1 char SSI
     */
    private String formatObsValue(double value, int lli) {
        if (Double.isNaN(value) || Double.isInfinite(value)) {
            // If observation is missing, write 16 blank spaces
            return "                ";
        }

        String lliStr = (lli > 0) ? String.valueOf(lli) : " ";
        String ssiStr = " "; // Signal strength indicator space flag

        return String.format(Locale.US, "%14.3f%1s%1s", value, lliStr, ssiStr);
    }

    /**
     * Enforces strict 80-column line width required by standard RINEX specification.
     */
    private void writeHeaderLine(BufferedWriter writer, String dataPayload, String label) throws IOException {
        String paddedData = padRight(dataPayload, 60);
        String paddedLabel = padRight(label, 20);
        writer.write(paddedData + paddedLabel);
        writer.newLine();
    }

    private String padRight(String s, int n) {
        if (s == null) s = "";
        if (s.length() >= n) return s.substring(0, n);
        return String.format(Locale.US, "%-" + n + "s", s);
    }
}