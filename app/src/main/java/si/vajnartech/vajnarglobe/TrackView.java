package si.vajnartech.vajnarglobe;

import android.annotation.SuppressLint;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.location.Location;
import android.util.Log;

import java.util.ArrayList;
import java.util.Map;

import si.vajnartech.calculus.Derivative;
import si.vajnartech.calculus.Function;
import si.vajnartech.calculus.R2Double;
import si.vajnartech.calculus.R2Function;
import si.vajnartech.calculus.Transformator;

import static si.vajnartech.vajnarglobe.C.Parameters.numPoints;
import static si.vajnartech.vajnarglobe.C.TAG;
import static si.vajnartech.vajnarglobe.C.areas;

interface TrackViewInterface
{
  void printLocation(Location loc);
  void setAreaName(String name);
}

@SuppressLint("ViewConstructor")
public class TrackView extends GeoMap
{
  private final TrackViewInterface intf;

  private R2Double     aproxPosition = null;
  long currentTime;

  private  Area currentArea;
  boolean isCalibrated = false;

  MyFunction   fs;
  MyDerivative fv;
  Aproximator  aproximator;

  TrackView(MainActivity ctx, TrackViewInterface intf)
  {
    super(ctx);
    this.intf = intf;
  }

  VectorField H = new VectorField()
  {
    @Override
    void done(R2Double v)
    {
      _hector(v);
    }
  };

  private void _hector(R2Double point)
  {
    currentTime = System.currentTimeMillis();
    fs.put(currentTime, point);
    currentPoint = point;
    activity.runOnUiThread(this::invalidate);
  }

  @Override
  protected void notifyMe(Location loc)
  {
    if (loc != null) {
      if (intf == null) return;
      GeoPoint geos = new GeoPoint(loc.getLongitude(), loc.getLatitude());
      if (isCalibrated)
        H.add(geos);
      else {
        currentPoint = geos;
        invalidate();
      }
      if (firstPoint == null) {
        Map.Entry<String, Area> entry = C.areas.entrySet().iterator().next();
        Area                    value = entry.getValue();
        firstPoint = value.getFirstPoint();
      }
      intf.printLocation(loc);
    }
    currentArea = _setCurrentArea(currentPoint);
  }

  private Area _setCurrentArea(R2Double p)
  {
    if (p == null)
      return null;
    for (Area a: areas.values())
      if (a.isInside(p)) {
        intf.setAreaName(a.areaName);
        return a;
      }
    return null;
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
    if (currentPoint == null) return;
    currentPoint.draw(canvas, paint, Color.RED, 5, this);
    if (currentArea != null && isCalibrated)
      _draw(currentArea, canvas);
  }

  private void _draw(Area area, Canvas canvas)
  {
    fs.draw(canvas, paint, Color.GRAY, this);
    if (aproxPosition != null)
      aproxPosition.draw(canvas, paint, Color.GREEN, 4, this);
    if (currentPoint == null)
      return;
    if (!area.isInside(currentPoint))
      return;
    ArrayList<R2Double> closestPoints = area.process(currentPoint);
    int                 i             = -1;
    for (R2Double p : closestPoints) {
      i++;
      if (area.get(i).onMe(p))
        p.draw(canvas, paint, Color.GREEN, 4, this);

      R2Double startPoint = fs.f("first");
      if (startPoint == null)
        continue;
      startPoint.draw(canvas, paint, Color.BLUE, 4, this);
      _predict(startPoint, canvas, area, i);
    }
  }

  private void _predict(R2Double startPoint, Canvas canvas, Area area, int i)
  {
    Line approx = new Line(startPoint, currentPoint);
    fs.f(currentTime + 2000).draw(canvas, paint, Color.MAGENTA, 4, this);
    approx.draw(canvas, paint, Color.RED, this);

    R2Double predictor = approx.intersection(area.get(i));
    if (predictor != null && area.get(i).onMe(predictor))
      predictor.draw(canvas, paint, Color.MAGENTA, 4, this);
    else
      return;

    R2Double qqq = new R2Double();
    qqq.is(predictor.minus(currentPoint));
    R2Double sum = fv.sum(null, null);
    sum.is(sum.divS((double) fs.size()));
    R2Double time = new R2Double(Math.abs(qqq.x1() / sum.x1()), Math.abs(qqq.x2() / sum.x2()));
    Log.i(TAG, String.format("do meje %d bos prisel cez ", i) + (time.x1() + time.x2()) / 1000 + " sekund");
  }

  @SuppressWarnings("ConstantConditions")
  static class MyDerivative extends Derivative<Long, R2Double>
  {
    MyDerivative(Function<Long, R2Double> fun)
    {
      super(fun);
    }

    @Override
    public R2Double f(Long x)
    {
      R2Double df = new R2Double();
      if (size() < 2) return null;
      int j = getKeys().indexOf(x);
      if (j == size() - 1) return null;
      df.is(get(getKeyAt(j + 1)).minus(get(getKeyAt(j))));
      long     dx  = getKeys().get(j + 1) - getKeys().get(j);
      R2Double res = new R2Double();
      res.is(df.divS((double) dx));
      return res;
    }

    @Override
    public R2Double sum(Long x0, Long x1)
    {
      int i0 = getKeys().indexOf(x0);
      int i1 = getKeys().indexOf(x1);
      if (x0 == null) {
        i0 = 0;
        i1 = size() - 1;
      }
      R2Double res = new R2Double();
      for (int i = i0; i < i1; i++) {
        R2Double tmp = new R2Double();
        tmp.is(res.plus(f(getKeyAt(i))));
        res.is(res.plus(f(getKeyAt(i))));
      }
      return res;
    }
  }

  @SuppressWarnings("SameParameterValue")
  static class MyFunction extends Function<Long, R2Double>
  {
    private R2Function<LinearFun> fun = null;

    @Override
    public R2Double f(Long x)
    {
      if (size() < 2 || fun == null)
        return null;
      return new R2Double(fun.f1.f(x), fun.f2.f(x));
    }

    @Override
    public R2Double sum(Long x0, Long x1)
    {
      return null;
    }

    R2Double f(String s)
    {
      if ("first".equals(s)) {
        if (size() > numPoints)
          return get(getKeys().get((size() - numPoints)));
        return null;
      }
      return null;
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    public R2Double put(Long key, R2Double value)
    {
      R2Double res = super.put(key, value);
      if (size() > 2) {
        long     k0  = getKeys().get(0);
        long     kn  = getKeys().get(size() - 1);
        R2Double p11 = new R2Double((double) k0, get(k0).get(0));
        R2Double p12 = new R2Double((double) kn, get(kn).get(0));
        R2Double p21 = new R2Double((double) k0, get(k0).get(1));
        R2Double p22 = new R2Double((double) kn, get(kn).get(1));
        fun = new R2Function<>(new LinearFun(p11, p12), new LinearFun(p21, p22));
      }
      return res;
    }

    void draw(Canvas c, Paint paint, int color, Transformator tr)
    {
      if (size() > 1)
        for (int i = 0; i < size() - 1; i++)
          new Line(get(getKeyAt(i)), get(getKeyAt(i + 1))).draw(c, paint, color, tr);
    }
  }

  class MyAproximator extends Aproximator
  {
    MyAproximator(int timeout)
    {
      super(timeout);
    }

    @Override
    void go()
    {
      if (fs != null) {
        aproxPosition = fs.f(System.currentTimeMillis());
        if (aproxPosition == null) return;
        activity.runOnUiThread(TrackView.this::invalidate);
      }
    }
  }

  void start()
  {
    fs = new MyFunction();
    fv = new MyDerivative(fs);
    aproximator = new MyAproximator(1000);
  }
}

