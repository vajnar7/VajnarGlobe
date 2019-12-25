package si.vajnartech.vajnarglobe;

import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import com.google.gson.Gson;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.ParameterizedType;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;

interface OnFail
{
  void execute(int errCode);
}

@SuppressWarnings({"unchecked", "ConstantConditions", "SameParameterValue", "WeakerAccess"})
public abstract class REST<T> extends AsyncTask<String, Void, T>
{
  private static final String TAG = "IZAA-REST";

  static final         int OUTPUT_TYPE_JSON   = 0;
  private static final int OUTPUT_TYPE_STRING = 1;
  private static final int OUTPUT_TYPE_STREAM = 2;

  private String url;
  private String token = "";

  private final Class<T> resultClass;

  private Gson gson;
  OnFail onFail;

  REST(String url)
  {
    this(url, new OnFail() {
      @Override public void execute(int errCode)
      {
        Log.i(TAG, "Something went wrong: " + errCode);
      }
    });
  }

  REST(String url, OnFail onFail)
  {
    super();
    resultClass = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    this.url = url;
    Log.i(TAG, "URL=" + url);
    this.gson = new Gson();
    this.onFail = onFail;
    new Login((REST<Integer>) this).executeOnExecutor(Login.THREAD_POOL_EXECUTOR);
  }

  @Override
  protected T doInBackground(String... params)
  {
    try {
      if (params.length > 0)
        token = params[0];
      return backgroundFunc();
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  @SuppressWarnings("ResultOfMethodCallIgnored")
  private void writeToInputStream(InputStream in, OutputStream out)
  {
    byte[] buffer = new byte[4096];
    int    len;

    try {
      File file = new File(Environment.getExternalStorageDirectory().getPath() + "/text.json");
      file.createNewFile();
      FileOutputStream test = new FileOutputStream(file);

      while ((len = in.read(buffer)) != -1) {
        out.write(buffer, 0, len);
        test.write(buffer, 0, len);
      }
      test.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  T callServer(Object params, int objectType)
  {
    try {
      URL               url  = new URL(this.url);
      HttpURLConnection conn = (HttpURLConnection) url.openConnection();
      conn.setRequestMethod("POST");
      int readTimeout = 0;
      conn.setConnectTimeout(readTimeout);
      conn.setReadTimeout(readTimeout);
      conn.setDoOutput(true);

      Log.i(TAG, "TOKEN=" + token);
      conn.setRequestProperty("Authorization", "Token " + token);
      conn.setRequestProperty("Content-Type", "application/json");
      conn.setRequestProperty("Content-Encoding", "utf-8");

      if (params != null) {
        OutputStream os = conn.getOutputStream();
        switch (objectType) {
        case OUTPUT_TYPE_JSON:    // object to serialize
          os.write(gson.toJson(params).getBytes());
          break;
        case OUTPUT_TYPE_STRING:  // plain string
          os.write(((String) params).getBytes());
          break;
        case OUTPUT_TYPE_STREAM:  // stream
          InputStream is = (InputStream) params;
          writeToInputStream(is, os);
          is.close();
          break;
        }
        os.close();
      }
      conn.connect();
      T   result       = null;
      int responseCode = conn.getResponseCode();
      if (responseCode == HttpURLConnection.HTTP_OK) {
        if (resultClass != Void.class) {
          BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()), 512);
          result = gson.fromJson(br, resultClass);
          br.close();
        }
        Log.i(TAG, "Response content: " + " " + url + " " + result);
      } else {
        BufferedInputStream is   = new BufferedInputStream(conn.getErrorStream());
        BufferedReader      br   = new BufferedReader(new InputStreamReader(is));
        String              l;
        StringBuilder       resj = new StringBuilder();
        while ((l = br.readLine()) != null)
          resj.append(l);
        br.close();
        is.close();
        Log.w(TAG, "Function response: " + url + " " +
                   responseCode + " " +
                   conn.getResponseMessage() + " " +
                   resj.toString());
      }
      return result;
    } catch (SocketTimeoutException e) {
      Log.w(TAG, "Timeout connecting to " + url);
    } catch (ConnectException e) {
      Log.w(TAG, e.getMessage());
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }

  public abstract T backgroundFunc();
}
