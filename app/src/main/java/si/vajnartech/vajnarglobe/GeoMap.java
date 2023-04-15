package si.vajnartech.vajnarglobe;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.location.Location;
import android.view.MotionEvent;
import android.view.View;

import java.util.Map;

import si.vajnartech.calculus.RnDouble;
import si.vajnartech.vajnarglobe.math.D;
import si.vajnartech.vajnarglobe.math.NumDouble2;

import static si.vajnartech.vajnarglobe.C.areas;

class GeoMap extends GPSSimulator implements Transform
{
  public static final int NONE = 0;
  public static final int CONSTRUCTING_AREA = 1;
  public static final int TRACKING = 2;

  protected GeoPoint firstPoint;
  protected GeoPoint currentPoint;

  protected UpdateUI updateUI;

  protected CurrentArea currentArea;

  protected int mode = NONE;

  private final D dK = new D();

  GeoMap(Context ctx)
  {
    super(ctx);
  }

  GeoMap(Context ctx, UpdateUI updateUI)
  {
    super(ctx);
    this.updateUI = updateUI;
  }

  @Override
  protected NumDouble2 setOrigin()
  {
    double   x1 = getWidth();
    double   x2 = getHeight();
    NumDouble2 res  = new NumDouble2(x1, x2);
    res.div(new NumDouble2(2.0, 2.0));
    return res;
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
      dK.up(new NumDouble2(rx, ry));
      break;
    case MotionEvent.ACTION_UP:
      rx = event.getRawX();
      ry = event.getRawY();
      dK.up(new NumDouble2(rx, ry));
      origin.plus(dK);
      view.invalidate();
      break;
    default:
      return false;
    }
    return true;
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
    if (mode == CONSTRUCTING_AREA) {
      return;
    }

    boolean found = false;
    for (Area area : areas.values()) {
      if (found)
        break;
      if (area.isInside(currentPoint)) {
        currentArea = new CurrentArea(area);
        currentArea.constructArea();
        found = true;
      }
    }

    if (currentArea == null || !found)
      currentArea = new CurrentArea();

    updateUI.setAreaName(currentArea);
  }

  @Override
  public NumDouble2 transform(NumDouble2 p)
  {
    NumDouble2 res = new NumDouble2(p);
    res.minus(firstPoint);
    NumDouble2 scale = new NumDouble2(C.Parameters.getScale(), -C.Parameters.getScale());
    res.mul(scale);
    res.plus(origin);
    return res;
  }
}
