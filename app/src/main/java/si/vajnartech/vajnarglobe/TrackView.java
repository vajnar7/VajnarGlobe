package si.vajnartech.vajnarglobe;

import android.annotation.SuppressLint;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.location.Location;

import si.vajnartech.vajnarglobe.math.R2Double;
import si.vajnartech.vajnarglobe.math.RnDouble;

interface TrackViewInterface
{
  void printLocation(Location loc);
}

@SuppressLint("ViewConstructor")
public class TrackView extends GPS implements Transformator
{
  private Paint paint = new Paint();
  public GeoPoint currentPoint;
  private MainActivity act;
  private TrackViewInterface intf;
  private R2Double firstPoint;

  TrackView(MainActivity ctx, TrackViewInterface intf)
  {
    super(ctx);
    act = ctx;
    this.intf = intf;
  }

  @Override
  protected void notifyMe(Location loc)
  {
    if (intf == null) return;
    currentPoint = new GeoPoint(loc.getLongitude(), loc.getLatitude());
    if (firstPoint == null)
      firstPoint = new GeoPoint(loc.getLongitude(), loc.getLatitude());
    intf.printLocation(loc);
    invalidate();
  }

  @Override
  public R2Double transform(R2Double p)
  {
    RnDouble a = origin.mul(p);
    RnDouble c = a.div(firstPoint);
    R2Double scale = new R2Double(-C.Parameters.scaleX, C.Parameters.scaleY);
    RnDouble dX = c.minus(origin);
    dX = dX.mul(scale);
    RnDouble b = origin.plus(dX);
    return new R2Double(b.get(0), b.get(1));
  }

  @Override
  protected void onDraw(Canvas canvas)
  {
    super.onDraw(canvas);
    if (currentPoint != null)
      currentPoint.draw(canvas, paint, Color.RED, 4, this);
    if (act.currentArea.isConstructed())
      act.currentArea.draw(canvas, paint, Color.BLACK, this);
    else
      for (GeoPoint p : act.currentArea.geoPoints)
        p.draw(canvas, paint, Color.GREEN, 4, this);
  }

  @Override
  protected R2Double setOrigin()
  {
    double x1 = getWidth();
    double x2 = getHeight();
    R2Double o = new R2Double(x1, x2);
    RnDouble a = o.divS(2.0);
    return new R2Double(a.get(0), a.get(1));
  }
}
