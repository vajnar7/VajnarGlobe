package com.vajnar.vajnargnss;

import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;

public class NtripClient extends AsyncTask<TcpClient, byte[], byte[]>
{
    String FILENAME = "base.rtcm3";
    private final ByteArrayOutputStream result = new ByteArrayOutputStream();
    private TcpClient client;
    protected NtripInterface ntripInterface;
    private final Object fileLock = new Object();

    public NtripClient(NtripInterface ntripInterface, OnFailInterface onFail)
    {
        super(onFail);
        this.ntripInterface = ntripInterface;
        new CasterLogin(this, onFail, ntripInterface).execute();
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
                startNewLog();
                client.run(data -> publishProgress(data.toByteArray()));
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
            fos.write(value);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void startNewLog()
    {
        synchronized (fileLock) {
            File baseDirectory = new File(mContext.getFilesDir(), FILE_PREFIX);
            File file = new File(baseDirectory, filename);
        }
    }

    private void writeBytes()
    {
        File file = new File()
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(bytes);
            fos.close();
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
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
