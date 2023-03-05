package si.vajnartech.vajnarglobe;

import android.annotation.SuppressLint;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.location.Location;

@SuppressLint("ViewConstructor")
public class CaptureView extends GeoMap
{
  private final Paint paint = new Paint();

  GeoPoint currentPoint;

  CaptureView(MainActivity ctx)
  {
    super(ctx);
  }

  @Override
  protected void notifyMe(Location loc)
  {
    super.notifyMe(loc);
    geoMapInterface.printLocation(loc);
    currentArea = (CurrentArea) updateCurrentArea(currentPoint);
    if (currentArea != null)
      geoMapInterface.setAreaName(currentArea.areaName);
  }

  @Override
  public boolean performClick()
  {
    return super.performClick();
  }

  @Override
  protected void onDraw(Canvas canvas)
  {
    super.onDraw(canvas);
    if (currentPoint != null)
      currentPoint.draw(canvas, paint, Color.RED, 4, this);
    if (currentArea.isConstructed())
      currentArea.draw(canvas, paint, Color.BLACK, this);
    else {
      for (GeoPoint p : currentArea.geoPoints)
        p.draw(canvas, paint, Color.BLUE, 6, this);
    }
  }
}
