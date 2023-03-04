package si.vajnartech.vajnarglobe;

import android.annotation.SuppressLint;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.location.Location;

interface CaptureViewInterface
{
  void printLocation(Location loc);
  CurrentArea getCurrentArea();
}

@SuppressLint("ViewConstructor")
public class CaptureView extends GeoMap
{
  private final Paint paint = new Paint();

  GeoPoint currentPoint;
  private final CaptureViewInterface intf;

  CaptureView(MainActivity ctx, CaptureViewInterface intf)
  {
    super(ctx);
    this.intf = intf;
  }

  @Override
  protected void notifyMe(Location loc)
  {
    super.notifyMe(loc);
    intf.printLocation(loc);
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
    if (intf.getCurrentArea().isConstructed())
      intf.getCurrentArea().draw(canvas, paint, Color.BLACK, this);
    else {
      for (GeoPoint p : intf.getCurrentArea().geoPoints)
        p.draw(canvas, paint, Color.BLUE, 6, this);
    }
  }
}
