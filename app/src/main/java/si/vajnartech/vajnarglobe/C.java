package si.vajnartech.vajnarglobe;

import android.location.Location;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

class C
{
  // ce dostopamo od zunaj
  static final String SERVER_ADDRESS = "http://89.142.196.96:8007/";
//   static final String SERVER_ADDRESS = "http://192.168.1.10:8007/";

  static final String AREAS          = SERVER_ADDRESS + "rest/geopoint/%s";
  static final String DELETE_AREA    = SERVER_ADDRESS + "rest/delete/%s";
  static final String GET_ALL        = SERVER_ADDRESS + "rest/area/";
  static final String WATCHDOG_USR   = "vajnar";
  static final String WATCHDOG_PWD   = "AldebaraN7";

  static final boolean GPS_SIMULATE = true;

  static final String TAG           = "IZAA";
  static final double DEF_LONGITUDE = 13.826209;  //x
  static final double DEF_LATITUDE  = 46.487243;  //y

  // DB of areas
  static HashMap<String, Area>  areas   = new HashMap<>();
  // screen dimensions
  static android.graphics.Point size    = new android.graphics.Point();

  static boolean isTesterRunning = false;
  static void startTestGPSService(final MainActivity act)
  {
    // test parameters
    final int min = 5;
    final int max = 13;
    TrackView tv = null;
    if (act.getCurrentFragment() instanceof F_Track) {
      F_Track f = (F_Track) act.getCurrentFragment();
      if (f != null)
        tv = f.myView;
    } else
      return;
    final TrackView finalTv = tv;
    new Thread(new Runnable()
    {
      @Override public void run()
      {
        Random   r         = new Random();
        double   longitude = 13.82526551327198;
        double   latitude  = 46.48596391150142;
        Location loc       = new Location("");

        while (isTesterRunning) {
          int    t    = Parameters.minTime;
          int    rx   = r.nextInt(max - min) + min;
          int    ry   = r.nextInt(max - min) + min;
          double offX = (double) rx / 2000000.0;
          double offY = (double) ry / 2000000.0;
          assert finalTv != null;
          loc.setLatitude(latitude);
          loc.setLongitude(longitude);
          finalTv.onLocationChanged(loc);
          try {
            Thread.sleep(t);
          } catch (InterruptedException e) {
            e.printStackTrace();
            break;
          }
          longitude += offX;
          latitude += offY;
        }
      }
    }).start();
  }

  static class Parameters
  {
    // approximation
    static final int           n       = 1;    // get ~ points to determine current position
    // 35 is this value if min Time and minDist are zero.
    static final AtomicInteger lim     = new AtomicInteger(1);
    static final int           minTime = 2000;    // ms
    static final float         minDist = 0f; // m
    static final int           ZZ      = 3;    // ~ points back from current, cant be les than 2

    private static double scale = 2000000;
    static double getScale()
    {
      return scale;
    }

    static void setScale(double val)
    {
      scale = val;
    }
  }

  static ArrayList<GeoPoint> fakeArea = new ArrayList<>();
  static int                 c        = -1;

  static void createArea()
  {
    fakeArea.add(new GeoPoint(DEF_LONGITUDE, DEF_LATITUDE));
    fakeArea.add(new GeoPoint(DEF_LONGITUDE + 0.0001, DEF_LATITUDE));
    fakeArea.add(new GeoPoint(DEF_LONGITUDE + 0.0001, DEF_LATITUDE + 0.0001));
    fakeArea.add(new GeoPoint(DEF_LONGITUDE, DEF_LATITUDE + 0.0001));
  }
}
