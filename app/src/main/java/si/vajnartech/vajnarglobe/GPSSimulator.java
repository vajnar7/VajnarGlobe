package si.vajnartech.vajnarglobe;

import android.location.Location;
import android.util.Log;

abstract class GPSSimulator extends GPS
{
  GPSSimulator(MainActivity ctx)
  {
    super(ctx);
  }

  @Override
  protected void initGPSService(MainActivity ctx)
  {
    location.setLatitude(C.DEF_LATITUDE);
    location.setLongitude(C.DEF_LONGITUDE);
    new MoveSimulator();
  }



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

        Log.i("pepe", "dzzzzk" + " " + lon + "  " + lat);


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
