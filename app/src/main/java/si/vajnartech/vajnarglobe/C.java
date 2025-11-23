package si.vajnartech.vajnarglobe;

import android.graphics.Point;

import java.util.HashMap;

public class C
{
  // ce dostopamo od zunaj
  public static final String SERVER_ADDRESS = "http://89.142.196.96:13005/";
//  public static final String SERVER_ADDRESS = "http://192.168.1.10:13005/";
//  public static final String SERVER_ADDRESS = "http://192.168.1.10:8007/";

  public static final boolean DEBUG_MODE = true;

  public static final String AREAS_API    = SERVER_ADDRESS + "areas/";

  public static final String USER_API = SERVER_ADDRESS + "userlogin/";

  public static final String POS_API = SERVER_ADDRESS + "position/";

  public static final String TAG = "pepe";

  static double DEF_LONGITUDE = 13.825696327;  //x
  static double DEF_LATITUDE  = 46.486163422;  //y

  // DB of areas
  public static HashMap<String, Area> areas = new HashMap<>();

  // screen dimensions
  static Point size = new android.graphics.Point();


  static class Parameters
  {
    static private final int MAX_SCALE = 2000000;
    static private final int MIN_SCALE = 100000;
    static private final int SCALE_STEP = 100000;

    // 35 is this value if min Time and minDist are zero.
    static final int   minTime = 1000;   // ms
    static final float minDist   = 0f;   // m

    private static int scale = MIN_SCALE;
    static int getScale()
    {
      return scale;
    }

    static void zoomIn()
    {
      int tmp = scale + SCALE_STEP;
      if (tmp <= MAX_SCALE)
        scale = tmp;
    }

    static void zoomOut()
    {
      int tmp = scale - SCALE_STEP;
      if (tmp >= MIN_SCALE)
        scale = tmp;
    }
  }
}
