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

public class Login extends AsyncTask<String, Void, Integer>
{
  // ce dostopamo od zunaj
  private static final String SERVER_ADDRESS = "http://89.142.196.96:8008/";
  //  private static final String SERVER_ADDRESS = "http://192.168.1.3:8008/";
  static final         String AREAS          = SERVER_ADDRESS + "rest/geopoint/%s";
  static final         String DELETE_AREA    = SERVER_ADDRESS + "rest/delete/%s";
  static final         String GET_ALL        = SERVER_ADDRESS + "rest/area/";
  private static final String WATCHDOG_USR   = "vajnar";
  private static final String WATCHDOG_PWD   = "AldebaraN7";

  private        REST<Integer> task;
  private static String        token = "";
  private        String        user, pwd;
  private Gson gson;

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
      String WATCHDOG = SERVER_ADDRESS + "rest/";
      URL    url      = new URL(WATCHDOG + "token/");
      Log.i("REST", "Login dib: " + WATCHDOG + "token/");
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
      BufferedWriter out = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
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
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }

  protected void onPostExecute(Integer responseCode)
  {
    try {
      if (responseCode != null && responseCode == HttpURLConnection.HTTP_OK && task != null) {
        task.executeOnExecutor(THREAD_POOL_EXECUTOR, (String[]) new String[]{getToken()});
      } else if (task != null) {
        task.fail(responseCode);
      }
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

