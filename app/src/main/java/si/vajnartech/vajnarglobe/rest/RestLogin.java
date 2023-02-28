package si.vajnartech.vajnarglobe.rest;

import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

import si.vajnartech.vajnarglobe.Json;
import si.vajnartech.vajnarglobe.MainActivity;
import si.vajnartech.vajnarglobe.R;

class RestLogin<T extends RestBaseObject> extends AsyncTask<String, Integer>
{
  protected final WeakReference<MainActivity> act;

  protected String url;
  protected String user;
  protected String password;

  protected String token = null;

  protected RestBase<T> task;

  RestLogin(RestBase<T> task, String url, String user, String password, MainActivity act)
  {
    this.url = url;
    this.user = user;
    this.password = password;
    this.task = task;
    this.act = new WeakReference<>(act);
  }

  @Override
  protected String background(HashMap<String, Integer> params)
  {
    try {
      HttpURLConnection conn = null;
      try {
        conn = new GetHttpConnection(url + "token/")
        {
          @Override public void setConnParams(HttpURLConnection conn) throws IOException
          {
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);

            String post = new Uri.Builder()
                .appendQueryParameter("username", user)
                .appendQueryParameter("password", password)
                .build().getEncodedQuery();

            OutputStream   os  = conn.getOutputStream();
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(os, StandardCharsets.UTF_8));
            out.write(post);
            out.flush();
            out.close();
            os.close();
          }
        }.get();

        if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
          BufferedInputStream is = new BufferedInputStream(conn.getInputStream());
          BufferedReader      br = new BufferedReader(new InputStreamReader(is));
          try {
            RegistrationToken object = Json.fromJson(br, RegistrationToken.class);
            if (object != null)
              token = object.token;
          } catch (Exception e) {
            throw new IOException("Can't get registration token");
          }
          br.close();
          is.close();
        } else {
          onFail();
        }
        return token;
      } finally {
        if (conn != null)
          conn.disconnect();
      }
    } catch (SocketTimeoutException ignored) {
      onFail();
    } catch (IOException e) {
      onFail();
      e.printStackTrace();
    }
    return null;
  }

  @Override
  protected void onPostExecute(String token)
  {
    if (token != null)
    {
      task.execute(new HashMap<String, String>() {{put("token", token);}});
    }
  }

  static class RegistrationToken
  {
    public String token;
  }

  protected void onFail()
  {
    act.get().runOnUiThread(() -> Toast.makeText(act.get(), R.string.server_conn_error, Toast.LENGTH_LONG).show());
  }
}
