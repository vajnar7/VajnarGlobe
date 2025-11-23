package com.vajnar.vajnargnss;

import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;

public class NtripClient extends AsyncTask<TcpClient, byte[], byte[]>
{
    private final ByteArrayOutputStream result = new ByteArrayOutputStream();

    public NtripClient()
    {
        new CasterLogin(this).execute();
    }

    @Override
    protected byte[] doInBackground(HashMap<String, TcpClient> params)
    {
        TcpClient client = params.get("client");
        ByteArrayOutputStream result = new ByteArrayOutputStream();

        if (client != null) {
            try {
            client.run(data -> {
                publishProgress(data.toByteArray());
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }}


        Log.i("PEPE", "Isteku se je task!");
        return result.toByteArray();
    }

    @Override
    protected void onProgressUpdate(byte[] value)
    {
        try {
            result.write(value);
            Log.i("PEPE", "Publishalo je tole v result " + Arrays.toString(value));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Log.i("PEPE", "Drekula");
    }

    @Override
    protected void onPostExecute(byte[] result)
    {
        Log.i("PEPE", "Done: " + Arrays.toString(result));
    }
}
