package si.vajnartech.vajnarglobe;

import android.graphics.Point;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class C
{
  // ce dostopamo od zunaj
//  static final String SERVER_ADDRESS = "http://89.142.196.96:8007/";
  public static final String SERVER_ADDRESS = "http://192.168.1.101:8007/";

  public static final boolean DEBUG_MODE = true;

  public static final String AREAS_API    = SERVER_ADDRESS + "areas/";

  public static final String TAG = "pepe";

  static double DEF_LONGITUDE = 13.826209;  //x
  static double DEF_LATITUDE  = 46.487243;  //y

  // DB of areas
  public static HashMap<String, Area> areas = new HashMap<>();

  // screen dimensions
  static Point size = new android.graphics.Point();


  @SuppressWarnings("unused")
  static class Parameters
  {
    // 35 is this value if min Time and minDist are zero.
    static final AtomicInteger lim           = new AtomicInteger(1);
    static final int           minTime = 1000;    // ms
    static final float minDist   = 0f; // m
    static final int   numPoints = 3;    // ~ points back from current, cant be les than 2

    private static double scale = 1500000;
    static double getScale()
    {
      return scale;
    }

    static void setScale(double val)
    {
      scale = val;
    }
  }
}
