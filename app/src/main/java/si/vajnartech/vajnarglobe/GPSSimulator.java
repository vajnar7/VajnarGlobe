package si.vajnartech.vajnarglobe;

import android.content.Context;
import android.location.Location;

import androidx.annotation.NonNull;

abstract class GPSSimulator extends GPS
{
  GPSSimulator(Context ctx)
  {
    super(ctx);
  }

  @Override
  protected void initGPSService(@NonNull MainActivity ctx)
  {
    location.setLatitude(C.DEF_LATITUDE);
    location.setLongitude(C.DEF_LONGITUDE);
  }

  public void mvLeft()
  {
    move(-1, 0);
  }

  public void mvRight()
  {
    move(1, 0);
  }

  public void mvDown()
  {
    move(0, -1);
  }

  public void mvUp()
  {
    move(0, 1);
  }

  public void startScheduler()
  {
    new MoveSimulator();
  }


  private void move(int xDir, int yDir)
  {
    Location tmpLocation = new Location(location);
    double lat = location.getLatitude() + (0.00001 * yDir);
    double lon = location.getLongitude() + (0.00001 * xDir);

    tmpLocation.setLongitude(lon);
    tmpLocation.setLatitude(lat);
    onLocationChanged(tmpLocation);
  }

  @SuppressWarnings("BusyWait")
  class MoveSimulator extends Thread
  {
    int fx = 1;
    int fy = 1;
    boolean running;
    int n = 1;
    MoveSimulator()
    {
      start();
      running = true;
    }

    // ++ SV
    // +- SZ
    // -+ JV
    // -- JZ
    @Override
    public void run()
    {
      while(running) {
        if (n % 5 != 0) {
          if (n == 19) {
            fy = 1;
            fx = -1;
          } else if (n == 38) {
            fy = -1;
            fx = -1;
          } else if (n == 57) {
            fy = -1;
            fx = 1;
          }
          double lat = location.getLatitude() + (0.00001 * fy);
          double lon = location.getLongitude() + (0.00001 * fx);
          Location tmpLoc = new Location("GPS");
          tmpLoc.setLongitude(lon);
          tmpLoc.setLatitude(lat);
          onLocationChanged(tmpLoc);
        } else
          onLocationChanged(location);

        try {
          sleep(1000);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
        n += 1;
      }
    }
  }
}
