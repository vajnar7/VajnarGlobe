package com.vajnar.vajnargnss;

import android.annotation.SuppressLint;
import android.util.Base64;
import android.util.Log;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class CasterLogin extends AsyncTask<String, String, TcpClient>
{
    private static final String mountPoint = "FRELIH";
    private static final String userAgent = "Android #1 VajnarGlobe client";
    private static final String userNamePwd = "xxxx@xxxx.xxxx:none";
    private static final int ntripPort = 2101;
    private static final boolean ntripHost = false;
    private static final boolean ntripV2 = false;
    private static final String ntripCaster = "rtk2go.com";
    private static final double myLat = 46.486149638;  // Gozd Martuljek Zg. Rute 99
    private static final double myLon =  13.825585396;
    private static final double myAlt = 752;

    protected NtripClient connectTask;

    public CasterLogin(NtripClient connectTask) {
        this.connectTask = connectTask;
    }

    @Override
    protected void onPostExecute(TcpClient client)
    {
       if (client != null) {
           connectTask.execute(new HashMap<>() {{
               put("client", client);
           }});
       }
    }

    @Override
    protected TcpClient doInBackground(HashMap<String, String> params)
    {
        TcpClient client = new TcpClient(ntripCaster, ntripPort);

        try {
            client.connect();
            String error = onClientPrepared(client);
            if (!error.isEmpty())
                return null;
            client.send(makeGGA());
            return client;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected String onClientPrepared(@NonNull TcpClient client) throws IOException {
        client.send(preapareRequest());
        String[] res = client.receive(4096).toString().split("\r\n");
        boolean headerOk = false;

        for (String line : res) {
            line = line.toUpperCase();
            if (line.startsWith("SOURCETABLE")) {
               return "Mountpoint not found";
            } else{
                headerOk |= line.endsWith(" 200 OK");
            }
            if (!headerOk) {
                return "Connection error";
            }
        }

        return "";
    }

    private String preapareRequest()
    {
        String b64NamePwd = Base64.encodeToString(userNamePwd.getBytes(), Base64.DEFAULT).strip();

        String request = String.format("GET /%s HTTP/1.1\r\n", mountPoint);
        request += String.format("User-Agent: %s\r\n", userAgent);
        request += String.format("Authorization: Basic %s\r\n", b64NamePwd);

        if (ntripHost || ntripV2)
            request += String.format("Host: %s:%s\r\n", ntripCaster, ntripPort);
        if (ntripV2)
            request += "Ntrip-Version: Ntrip/2.0\r\n";

        return request;
    }

    /** @noinspection ConstantValue*/
    private String makeGGA()
    {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);
        // sanitise lat/lon
        String NS = "N";
        String EW = "E";
        double lon = myLon;
        double lat = myLat;
        if (lon > 180) {
            lon -= 360;
            lon *= -1;
            EW = "W";
        }
        else if ((lon < 0) && (lon >= -180)) {
            lon *= -1;
            EW = "W";
        }
        else if (lon <-180) {
            lon += 360;
        }
        if (lat < 0) {
            lat *= -1;
            NS = "S";
        }

        int lonDeg = (int) lon;
        int latDeg = (int) lat;
        double lonMin = (lon - lonDeg) * 60;
        double latMin = (lat - latDeg) * 60;

        // construct GGA sentence
        String timStr = String.format(Locale.GERMAN,
                "%02d%02d%02d.00", hour, minute, second);
        String latStr = String.format(Locale.GERMAN,
                "%02d%011.8f,%s", latDeg, latMin, NS);
        String lonStr = String.format(Locale.GERMAN,
                "%02d%011.8f,%s", lonDeg, lonMin, EW);
        String altStr = String.format(Locale.GERMAN,
                "%5.3f", myAlt);
        String ggaStr = String.format(Locale.GERMAN,
                "GPGGA,%s,%s,%s,1,05,0.19,+00400,M,%s,M,,", timStr, latStr, lonStr, altStr);
        // calc checksum
        int cksm = 0;
        for (int i = 0; i < ggaStr.length(); i++){
            char c = ggaStr.charAt(i);
            cksm ^= c;
        }
        String cksStr = String.format(Locale.GERMAN,
                "%02X",cksm);
        return String.format(Locale.GERMAN,
                "$%s*%s\r\n", ggaStr, cksStr);

    }
}
