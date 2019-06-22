package si.vajnartech.vajnarglobe;

import android.location.Location;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import si.vajnartech.vajnarglobe.math.R2Double;

class C
{
  static R2Double O;

  static final String TAG           = "IZAA";
  static final double DEF_LONGITUDE = 13.826209;  //x
  static final double DEF_LATITUDE  = 46.487243;  //y

  // scale i power of 10
  static int                    scale   = 6;
  // offsets
  static Integer                xOffset = null;
  static Integer                yOffset = null;
  // DB of areas
  static HashMap<String, Area>  areas   = new HashMap<>();
  // screen dimensions
  static android.graphics.Point size    = new android.graphics.Point();

  static void startTestGPSService(final MainActivity act)
  {
    // test parameters
    final int min = 5;
    final int max = 13;
    WhereAmI  gps = null;
    if (act.getCurrentFragment() instanceof F_Track) {
      F_Track f = (F_Track) act.getCurrentFragment();
      if (f != null)
        gps = f.gps;
    } else
      return;
    final WhereAmI finalGps = gps;
    new Thread(new Runnable()
    {
      @Override public void run()
      {
        Random   r         = new Random();
        double   longitude = DEF_LONGITUDE;
        double   latitude  = DEF_LATITUDE;
        Location loc       = new Location("");

        while (true) {
          int    t    = Parameters.minTime;
          int    rx   = r.nextInt(max - min) + min;
          int    ry   = r.nextInt(max - min) + min;
          double offX = (double) rx / 1000000.0;
          double offY = -(double) ry / 1000000.0;
          assert finalGps != null;
          loc.setLatitude(latitude);
          loc.setLongitude(longitude);
          finalGps.onLocationChanged(loc);
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
    static final int           minTime = 500;    // ms
    static final float         minDist = 0f; // m
    static final int           ZZ      = 2;    // ~ points back from current, cant be les than 2
    static double scaleX = 3000;
    static double scaleY = 30000;
  }

  // for simulation
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
