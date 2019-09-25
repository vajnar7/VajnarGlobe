//http://rkg.gov.si/GERK/WebViewer/
package si.vajnartech.vajnarglobe;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import si.vajnartech.vajnarglobe.math.R2Double;
import si.vajnartech.vajnarglobe.math.RnDouble;

import static si.vajnartech.vajnarglobe.C.DEF_LATITUDE;
import static si.vajnartech.vajnarglobe.C.DEF_LONGITUDE;
import static si.vajnartech.vajnarglobe.C.GPS_SIMULATE;
import static si.vajnartech.vajnarglobe.C.Parameters.minDist;
import static si.vajnartech.vajnarglobe.C.Parameters.minTime;
import static si.vajnartech.vajnarglobe.C.*;

public abstract class GPS extends View implements LocationListener, View.OnTouchListener
{
  protected MainActivity ctx;
  protected Location location;
  protected RnDouble origin = null;
  float dX, dY;
  D dK = new D();

  GPS(MainActivity ctx)
  {
    super(ctx);
    this.ctx = ctx;
    location = new Location("");
    enableGPSService();
  }

  private void enableGPSService()
  {
    final String[] INITIAL_PERMS = {
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    };
    final int INITIAL_REQUEST = 1337;
    ctx.requestPermissions(INITIAL_PERMS, INITIAL_REQUEST);
    LocationManager locationManager = (LocationManager) ctx.getSystemService(Context.LOCATION_SERVICE);
    if (locationManager == null) {
      Log.i(TAG, "Cannot get the LocationManager");
      return;
    } else
      Log.i(TAG, "The LocationManager successfully granted");

    if (ActivityCompat.checkSelfPermission(
        ctx,
        Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
        ActivityCompat.checkSelfPermission(
            ctx,
            Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
      (new AlertDialog.Builder(ctx)
           .setTitle("GPS not granted")
           .setMessage("Application will exit")
           .setPositiveButton("OK", new DialogInterface.OnClickListener()
           {
             @Override public void onClick(DialogInterface dialog, int which)
             {
               ctx.finish();
             }
           }).create()).show();
      return;
    }

    if (GPS_SIMULATE) {
      Location loc = new Location("");
      loc.setLatitude(DEF_LATITUDE);
      loc.setLongitude(DEF_LONGITUDE);
      C.createArea();
    }
    else {
      locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, minTime, minDist, this);
      Toast.makeText(ctx, "GPS granted", Toast.LENGTH_SHORT).show();
    }
  }

  @Override
  public void onLocationChanged(Location loc)
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

  @Override
  public boolean onTouch(View view, MotionEvent event)
  {
    if (origin == null) return true;
    double rx, ry;
    switch (event.getAction()) {
    case MotionEvent.ACTION_DOWN:
      dX = view.getX() - event.getRawX();
      dY = view.getY() - event.getRawY();
      rx = event.getRawX();
      ry = event.getRawY();
      dK.up(new R2Double(rx, ry));
      break;
    case MotionEvent.ACTION_UP:
      rx = event.getRawX();
      ry = event.getRawY();
      dK.up(new R2Double(rx, ry));
      origin.is(origin.plus(dK));
      view.invalidate();
      break;
    default:
      return false;
    }
    return true;
  }

  protected abstract RnDouble setOrigin();
  protected abstract void notifyMe(Location loc);
}