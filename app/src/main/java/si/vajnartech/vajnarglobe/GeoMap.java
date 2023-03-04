package si.vajnartech.vajnarglobe;

import android.annotation.SuppressLint;
import android.graphics.Canvas;
import android.graphics.Color;
import android.location.Location;
import android.view.MotionEvent;
import android.view.View;

import si.vajnartech.calculus.D;
import si.vajnartech.calculus.R2Double;
import si.vajnartech.calculus.RnDouble;
import si.vajnartech.calculus.Transformator;

@SuppressLint("ViewConstructor")
class GeoMap extends GPSSimulator implements Transformator
{
  protected R2Double firstPoint;
  protected R2Double currentPoint;

  private final D dK = new D();

  GeoMap(MainActivity ctx)
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
    currentPoint = new GeoPoint(loc.getLongitude(), loc.getLatitude());
    invalidate();
  }

  @Override
  protected void onDraw(Canvas canvas)
  {
    super.onDraw(canvas);
    for (Area a : C.areas.values()) {
      if (firstPoint == null) firstPoint = a.getFirstPoint();
      a.draw(canvas, paint, Color.BLACK, this);
    }
  }

  @Override
  public boolean onTouch(View view, MotionEvent event)
  {
    if (origin == null) return true;
    double rx, ry;
    switch (event.getAction()) {
    case MotionEvent.ACTION_DOWN:
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

  @Override
  public R2Double transform(R2Double p)
  {
    RnDouble d = p.minus(firstPoint);
    R2Double scale = new R2Double(-C.Parameters.getScale(), C.Parameters.getScale());
    RnDouble tmp = d.mul(scale);
    RnDouble b = origin.plus(tmp);
    return new R2Double(b.get(0), b.get(1));
  }
}
