package si.vajnartech.vajnarglobe.rest;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.util.HashMap;

import si.vajnartech.vajnarglobe.C;
import si.vajnartech.vajnarglobe.Json;
import si.vajnartech.vajnarglobe.MainActivity;
import si.vajnartech.vajnarglobe.SharedPref;

public abstract class RestBase<T extends RestBaseObject> extends AsyncTask<T, String>
{
  protected final WeakReference<MainActivity> act;

  protected String url;
  protected String requestMethod;
  protected String token;

  protected int       responseCode    = 0;
  protected String    responseData    = "";
  protected String    responseMessage = "";
  protected Exception serverException = null;

  public static final int SOCKET_TIMEOUT    = -100;
  public static final int CONNECT_EXCEPTION = -101;
  public static final int IO_EXCEPTION      = -102;

  protected int READ_TIMEOUT = 0;

  RestBase(String url, String requestMethod, MainActivity act)
  {
    this.url = url;
    this.requestMethod = requestMethod;
    this.act = new WeakReference<>(act);
    new RestLogin<>(this, C.SERVER_ADDRESS, "vajnar", "AldebaraN7", act).execute(null);
  }

  @Override
  protected T background(HashMap<String, String> params)
  {
    token = params.get("token");
    if (token != null)
      return backgroundFunc();
    return null;
  }

  protected T callServer(Object params)
  {
    try {
      HttpURLConnection conn = null;
      try {
        conn = new GetHttpConnection(url)
        {
          @Override public void setConnParams(HttpURLConnection conn) throws IOException
          {
            conn.setRequestMethod(requestMethod);
            if ("POST".equals(requestMethod))
              conn.setDoOutput(true);

            conn.setConnectTimeout(READ_TIMEOUT);
            conn.setReadTimeout(READ_TIMEOUT);
            conn.setRequestProperty("Authorization", "Token " + token);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Content-Encoding", "utf-8");

            if (params != null) {
              OutputStream os = conn.getOutputStream();
              synchronized (params) {
                os.write(Json.toJson(params).getBytes());
              }
              os.close();
            }
          }
        }.get();

        T result = null;
        responseCode = conn.getResponseCode();
        responseMessage = conn.getResponseMessage();
        if (responseCode == HttpURLConnection.HTTP_OK) {
          InputStream    is = conn.getInputStream();
          BufferedReader br = new BufferedReader(new InputStreamReader(is), 512);
          result = deserialize(br);
          br.close();
        } else {
          BufferedInputStream is   = new BufferedInputStream(conn.getErrorStream());
          BufferedReader      br   = new BufferedReader(new InputStreamReader(is));
          String              l;
          StringBuilder       resj = new StringBuilder();
          while ((l = br.readLine()) != null)
            resj.append(l);
          responseData = resj.toString();
          br.close();
          is.close();
          onFail();
        }
        return result;
      } finally {
        if (conn != null)
          conn.disconnect();
      }
    } catch (SocketTimeoutException e) {
      responseCode = SOCKET_TIMEOUT;
      serverException = e;
      responseMessage = "Timeout connecting to " + url;
      onFail();
    } catch (ConnectException e) {
      responseCode = CONNECT_EXCEPTION;
      serverException = e;
      responseMessage = "Connect exception";
      onFail();
    } catch (IOException e) {
      responseCode = IO_EXCEPTION;
      serverException = e;
      responseMessage = "IO exception";
      e.printStackTrace();
      onFail();
    }

    return null;
  }

  protected abstract T backgroundFunc();

  protected abstract void onFail();

  protected abstract T deserialize(BufferedReader br);
}
