package com.vajnar.vajnargnss;

import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;

public class NtripClient extends AsyncTask<TcpClient, byte[], byte[]>
{
    private final ByteArrayOutputStream result = new ByteArrayOutputStream();
    private TcpClient client;
    protected NtripInterface ntripInterface;

    public NtripClient(NtripInterface ntripInterface, OnFailInterface onFail)
    {
        super(onFail);
        this.ntripInterface = ntripInterface;
        new CasterLogin(this, onFail).execute();
    }

    public void stop()
    {
        try {
            client.stop();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected byte[] doInBackground(HashMap<String, TcpClient> params)
    {
        client = params.get("client");

        if (client != null) {
            try {
            client.run(data -> {
                publishProgress(data.toByteArray());
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }}


        return result.toByteArray();
    }

    @Override
    protected void onProgressUpdate(byte[] value)
    {
        try {
            Log.i("PEPE", Arrays.toString(value));
            result.write(value);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void onPostExecute(byte[] result)
    {
        if (result == null) return;
        Log.i("PEPE", "Done: " + Arrays.toString(result));
        ntripInterface.onRtcmDataPrepared(result);
    }
}
