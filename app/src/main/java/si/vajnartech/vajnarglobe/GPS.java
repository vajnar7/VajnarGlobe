//http://rkg.gov.si/GERK/WebViewer/
package si.vajnartech.vajnarglobe;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import si.vajnartech.vajnarglobe.math.NumDouble2;

import static si.vajnartech.vajnarglobe.C.Parameters.minDist;
import static si.vajnartech.vajnarglobe.C.Parameters.minTime;

public abstract class GPS extends View implements LocationListener, View.OnTouchListener
{
  protected volatile Location location;

  protected NumDouble2 origin = null;

  protected MainActivity activity;

  protected Paint paint = new Paint();

  GPS(Context ctx)
  {
    super(ctx);

    activity = (MainActivity) ctx;
    location = new Location("");
    initGPSService(activity);
    getDimensions(this);
  }

  protected void initGPSService(@NonNull MainActivity ctx)
  {
    final String[] INITIAL_PERMS = {
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    };
    final int INITIAL_REQUEST = 1337;
    ctx.requestPermissions(INITIAL_PERMS, INITIAL_REQUEST);

    LocationManager locationManager = (LocationManager) ctx.getSystemService(Context.LOCATION_SERVICE);
    if (ActivityCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
        ActivityCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
      Toast.makeText(ctx, "GPS not granted", Toast.LENGTH_LONG).show();
    } else
      locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, minTime, minDist, this);
  }

  @Override
  public void onLocationChanged(@NonNull Location loc)
  {
    location.setLongitude(loc.getLongitude());
    location.setLatitude(loc.getLatitude());
    notifyMe(location);
  }

  @Override
  public void onStatusChanged(String s, int i, Bundle bundle)
  {}

  @Override
  public void onProviderEnabled(String s)
  {}

  @Override
  public void onProviderDisabled(String s)
  {}

  @Override
  protected void onDraw(Canvas canvas)
  {
    if (origin == null)
      origin = setOrigin();
  }

  protected void getDimensions(final View v)
  {
    v.post(this::invalidate);
  }

  protected abstract NumDouble2 setOrigin();
  protected abstract void notifyMe(Location loc);
}