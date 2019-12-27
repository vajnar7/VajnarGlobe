package si.vajnartech.vajnarglobe;

import android.annotation.SuppressLint;
import android.graphics.Canvas;
import android.graphics.Color;
import android.location.Location;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import si.vajnartech.vajnarglobe.math.R2Double;
import si.vajnartech.vajnarglobe.math.RnDouble;

@SuppressLint("ViewConstructor")
public class Map extends GPS
{
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
}
