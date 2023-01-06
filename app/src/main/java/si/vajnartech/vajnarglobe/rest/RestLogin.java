package si.vajnartech.vajnarglobe.rest;

import android.net.Uri;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

import si.vajnartech.vajnarglobe.Json;

class RestLogin<T extends RestBaseObject> extends AsyncTask<String, Integer>
{
  protected String url;
  protected String user;
  protected String password;

  protected String token = null;

  protected RestBase<T> task;

  RestLogin(RestBase<T> task, String url, String user, String password)
  {
    this.url = url;
    this.user = user;
    this.password = password;
    this.task = task;
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
        }
        return token;
      } finally {
        if (conn != null)
          conn.disconnect();
      }
    } catch (SocketTimeoutException ignored) {
    } catch (IOException e) {
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
}
