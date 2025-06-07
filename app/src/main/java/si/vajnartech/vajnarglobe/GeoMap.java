package si.vajnartech.vajnarglobe;

import static si.vajnartech.vajnarglobe.C.areas;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.location.Location;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import si.vajnartech.vajnarglobe.math.D;
import si.vajnartech.vajnarglobe.math.DxDtDouble2;
import si.vajnartech.vajnarglobe.math.NumDouble2;

// DEBUG_MODE
class GeoMap extends GPSSimulator implements Transform
//class GeoMap extends GPS implements Transform
{
  public static final int NONE = 0;
  public static final int CONSTRUCTING_AREA = 1;
  public static final int TRACKING = 2;

  protected GeoPoint firstPoint;
  protected GeoPoint currentPoint;

  protected UpdateUI updateUI;

  protected CurrentArea currentArea;

  protected Boolean isMoving;

  protected int mode = NONE;

  protected  NumDouble2 touchPoint;

  protected D dK = new D();

  // odvod funkcije poti po casu
  volatile DxDtDouble2 dsDt = new DxDtDouble2();

  GeoMap(Context ctx)
  {
    super(ctx);
  }

  GeoMap(Context ctx, UpdateUI updateUI)
  {
    super(ctx);
    this.updateUI = updateUI;
    updateUI.setMessage(((MainActivity)ctx).tx(R.string.no_location));
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
    dsDt.add(new NumDouble2(loc.getLongitude(), loc.getLatitude()));
    isMoving = isObjectMoving();
    if (isMoving != null) {
      if (firstPoint == null)
        firstPoint = new GeoPoint(loc.getLongitude(), loc.getLatitude());
      currentPoint = new GeoPoint(loc.getLongitude(), loc.getLatitude());
      refresh(loc);
    }
  }

  //ko real GPS vcasih posilja nenatancne koordinate tudi ko mirujem
  private Boolean isObjectMoving()
  {
    NumDouble2 val = dsDt.value(0);
    if (val == null)
      return null;
    return val.get(0) != 0.0 || val.get(1) != 0.0;
  }

  private void refresh(Location loc)
  {
    updateCurrentArea();
    updateUI.printLocation(loc);
    invalidate();
  }

  @Override
  protected void onDraw(Canvas canvas)
  {
    super.onDraw(canvas);
    if (isInit())
      for (Area a : C.areas.values())
        a.draw(canvas, paint, Color.BLACK, this);
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
    touchPoint = new NumDouble2(rx, ry);
    return true;
  }

  // na zacetku je treba poinicializirati lokacijo sploh ce ni GPS se up za DEBUG
  protected void initLocation()
  {
    Location loc = new Location("");
    loc.setLongitude(C.DEF_LONGITUDE);
    loc.setLatitude(C.DEF_LATITUDE);
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
    NumDouble2 scale = new NumDouble2((double) C.Parameters.getScale (),
        (double) -C.Parameters.getScale());
    res.mul(scale);
    res.plus(origin);
    return res;
  }

  // convert point from screen space to geo space
  protected NumDouble2 toGeoSpace(NumDouble2 p)
  {
    NumDouble2 res = new NumDouble2(p);
    NumDouble2 scale = new NumDouble2((double) C.Parameters.getScale (),
            (double) -C.Parameters.getScale());

    res.minus(origin);
    res.div(scale);
    res.plus(firstPoint);
    return res;
  }

  protected boolean isInit()
  {
    return firstPoint != null;
  }
}
