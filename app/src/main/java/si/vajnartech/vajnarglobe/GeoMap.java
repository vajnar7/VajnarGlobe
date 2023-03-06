package si.vajnartech.vajnarglobe;

import static si.vajnartech.vajnarglobe.C.areas;

import android.annotation.SuppressLint;
import android.graphics.Canvas;
import android.graphics.Color;
import android.location.Location;
import android.view.MotionEvent;
import android.view.View;

import java.util.Map;

import si.vajnartech.calculus.D;
import si.vajnartech.calculus.R2Double;
import si.vajnartech.calculus.RnDouble;
import si.vajnartech.calculus.Transformator;

@SuppressLint("ViewConstructor")
class GeoMap extends GPSSimulator implements Transformator
{
  public static final int NONE = 0;
  public static final int CONSTRUCTING_AREA = 1;

  protected GeoPoint firstPoint;
  protected GeoPoint currentPoint;

  protected final UpdateUI updateUI;

  protected CurrentArea currentArea;

  protected int mode = NONE;

  private final D dK = new D();

  GeoMap(MainActivity ctx, UpdateUI updateUI)
  {
    super(ctx);
    this.updateUI = updateUI;
    initLocation();
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
    updateCurrentArea();
    updateUI.printLocation(loc);
    invalidate();
  }

  @Override
  protected void onDraw(Canvas canvas)
  {
    super.onDraw(canvas);
    for (Area a : C.areas.values()) {
      if (firstPoint == null)
        firstPoint = a.getFirstPoint();
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

  // na zacetku je treba poinicializirati lokacijo sploh ce ni GPS se up
  protected void initLocation()
  {
    Location loc = new Location("");
    if (areas.size() > 0) {
      Map.Entry<String, Area> entry = C.areas.entrySet().iterator().next();
      Area a = entry.getValue();
      C.DEF_LONGITUDE = a.geoPoints.get(1).lon;
      C.DEF_LATITUDE = a.geoPoints.get(1).lat;
    }
    loc.setLongitude(C.DEF_LONGITUDE);
    loc.setLatitude(C.DEF_LATITUDE);
    onLocationChanged(loc);
  }

  public void updateCurrentArea()
  {
    if (mode == CONSTRUCTING_AREA)
      return;

    boolean found = false;
    for (Area a: areas.values())
      if (a.isInside(currentPoint)) {
        currentArea = new CurrentArea(a);
        found = true;
      }

    if (currentArea == null || !found)
      currentArea = new CurrentArea();

    updateUI.setAreaName(currentArea);
  }
}
