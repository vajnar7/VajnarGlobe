//http://rkg.gov.si/GERK/WebViewer/ NTRIP protocol, for GPS corrections
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

import com.vajnar.vajnargnss.logger.GPSProvider;
import com.vajnar.vajnargnss.logger.GnssLogger;

public abstract class GPS extends GPSProvider
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
    getDimensions(this);

    paint.setStrokeWidth(3.0f);
    paint.setStyle(Paint.Style.STROKE);
    paint.setAntiAlias(true);
  }

  @Override
  public void onLocationChanged(@NonNull Location loc)
  {
    location.setLongitude(loc.getLongitude());
    location.setLatitude(loc.getLatitude());
    notifyMe(location);
  }


  @Override
  protected void onDraw(@NonNull Canvas canvas)
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