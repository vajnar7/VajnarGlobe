package si.vajnartech.vajnarglobe;

import android.annotation.SuppressLint;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.location.Location;
import android.util.Log;

import si.vajnartech.vajnarglobe.math.R2Double;
import si.vajnartech.vajnarglobe.math.RnDouble;

@SuppressLint("ViewConstructor")
public class Map extends GPS implements Transformator
{
  private Paint paint = new Paint();
  private R2Double firstPoint;

  Map(MainActivity ctx)
  {
    super(ctx);
  }

  @Override
  protected R2Double setOrigin()
  {
    double   x1 = getWidth();
    double   x2 = getHeight();
    R2Double o  = new R2Double(x1, x2);
    RnDouble a  = o.divS(2.0);
    return new R2Double(a.get(0), a.get(1));
  }

  @Override
  protected void notifyMe(Location loc)
  {
    if (firstPoint == null)
      firstPoint = new GeoPoint(loc.getLongitude(), loc.getLatitude());
  }

  @Override
  protected void onDraw(Canvas canvas)
  {
    super.onDraw(canvas);
    for (Area a : C.areas.values())
      a.draw(canvas, paint, Color.BLACK, this);
  }

  @Override
  public R2Double transform(R2Double p)
  {
    RnDouble a     = origin.mul(p);
    Log.i("IZAA", "a=" + a);
    Log.i("IZAA", "first=" + firstPoint);
    RnDouble c     = a.div(firstPoint);
    R2Double scale = new R2Double(-C.Parameters.scaleX, C.Parameters.scaleY);
    RnDouble dX    = c.minus(origin);
    dX = dX.mul(scale);
    RnDouble b = origin.plus(dX);
    return new R2Double(b.get(0), b.get(1));//------> zakaj se skala spreminaj
  }
}
