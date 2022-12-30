package si.vajnartech.vajnarglobe;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import static si.vajnartech.vajnarglobe.C.SERVER_ADDRESS;
import static si.vajnartech.vajnarglobe.C.WATCHDOG_PWD;
import static si.vajnartech.vajnarglobe.C.WATCHDOG_USR;

public class Login extends AsyncTask<String, Void, Integer>
{
  private final REST<Integer> task;

  private static String token = "";

  private final String user;
  private final String pwd;

  private final Gson gson;

  Login(REST<Integer> task)
  {
    super();
    this.task = task;
    user = WATCHDOG_USR;
    pwd = WATCHDOG_PWD;
    gson = new Gson();
  }

  private String getToken()
  {
    return token;
  }

  private void setToken(String token)
  {
    Login.token = token;
  }

  @Override
  protected Integer doInBackground(String... params)
  {
    if (getToken().length() > 0)
      return HttpURLConnection.HTTP_OK;
    try {
      String login = SERVER_ADDRESS + "token/";
      URL    url      = new URL(login);
      Log.i("REST", "Login dib: " + login);
      HttpURLConnection conn = (HttpURLConnection) url.openConnection();
      conn.setConnectTimeout(30000);
      conn.setReadTimeout(30000);
      conn.setRequestMethod("POST");
      conn.setDoOutput(true);

      Log.i("REST", "usr=" + user + "   pwd=" + pwd);
      String post = new Uri.Builder()
          .appendQueryParameter("username", user)
          .appendQueryParameter("password", pwd)
          .build().getEncodedQuery();

      OutputStream   os  = conn.getOutputStream();
      BufferedWriter out = new BufferedWriter(new OutputStreamWriter(os, StandardCharsets.UTF_8));
      out.write(post);
      out.flush();
      out.close();
      os.close();

      conn.connect();

      if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
        BufferedInputStream is = new BufferedInputStream(conn.getInputStream());
        BufferedReader      br = new BufferedReader(new InputStreamReader(is));
        setToken(gson.fromJson(br, RegistrationToken.class).token);
        Log.i("REST", "Login token: " + token);
        br.close();
        is.close();
      } else
        Log.i("REST", "Login response " + conn.getResponseCode() + conn.getResponseMessage());
      return conn.getResponseCode();
    } catch (SocketTimeoutException e) {
      Log.i("REST Login", "Timeout connecting to settings server");
      task.onFail();
    } catch (IOException e) {
      Log.i("REST Login", "Timeout connecting to settings server");
      task.onFail();
      e.printStackTrace();
    }
    return null;
  }

  protected void onPostExecute(Integer responseCode)
  {
    try {
      if (responseCode != null && responseCode == HttpURLConnection.HTTP_OK && task != null) {
        task.executeOnExecutor(THREAD_POOL_EXECUTOR, getToken());
      } else if (task != null && responseCode != null)
        task.onFail();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}

@SuppressWarnings("WeakerAccess")
class RegistrationToken
{
  public String token;
}

