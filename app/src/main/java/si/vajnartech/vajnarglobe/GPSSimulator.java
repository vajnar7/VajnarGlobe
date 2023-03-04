package si.vajnartech.vajnarglobe;

import android.location.Location;

import androidx.annotation.NonNull;

abstract class GPSSimulator extends GPS
{
  GPSSimulator(MainActivity ctx)
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


  private void move(int xDir, int yDir)
  {
    Location tmpLocation = new Location(location);
    double lat = location.getLatitude() + (0.00001 * yDir);
    double lon = location.getLongitude() + (0.00001 * xDir);

    tmpLocation.setLongitude(lon);
    tmpLocation.setLatitude(lat);
    onLocationChanged(tmpLocation);
  }

  @SuppressWarnings({"BusyWait", "unused"})
  class MoveSimulator extends Thread
  {
    boolean running;
    MoveSimulator()
    {
      start();
      running = true;
    }

    @Override
    public void run()
    {
      while(running) {
        Location tmpLocation = new Location(location);
        double lat = location.getLatitude() + 0.00001;
        double lon = location.getLongitude() + 0.00001;

        tmpLocation.setLongitude(lon);
        tmpLocation.setLatitude(lat);
        onLocationChanged(tmpLocation);
        try {
          sleep(5000);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
    }
  }
}
