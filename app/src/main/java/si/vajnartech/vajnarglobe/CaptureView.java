package si.vajnartech.vajnarglobe;

import android.annotation.SuppressLint;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.location.Location;

interface CaptureViewInterface
{
  void printLocation(Location loc);
  void printMessage(String msg);
}

@SuppressLint("ViewConstructor")
public class CaptureView extends Map
{
  private Paint paint = new Paint();
  public GeoPoint currentPoint;
  private MainActivity act;
  private CaptureViewInterface intf;

  CaptureView(MainActivity ctx, CaptureViewInterface intf)
  {
    super(ctx);
    act = ctx;
    this.intf = intf;
  }

  @Override
  protected void notifyMe(Location loc)
  {
    if (loc != null) {
      if (intf == null) return;
      currentPoint = new GeoPoint(loc.getLongitude(), loc.getLatitude());
      super.notifyMe(loc);
      intf.printLocation(loc);
    }
    invalidate();
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
    if (act.currentArea.isConstructed())
      act.currentArea.draw(canvas, paint, Color.BLACK, this);
    else {
      for (GeoPoint p : act.currentArea.geoPoints)
        p.draw(canvas, paint, Color.BLUE, 6, this);
      for (GeoPoint p : act.currentArea.currentPoints)
        p.draw(canvas, paint, Color.GREEN, 4, this);
    }
  }
}
