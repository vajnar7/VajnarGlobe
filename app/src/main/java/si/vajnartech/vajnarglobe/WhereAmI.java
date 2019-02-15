package si.vajnartech.vajnarglobe;


import android.annotation.SuppressLint;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.location.Location;
import android.util.Log;

import java.util.ArrayList;

import static si.vajnartech.vajnarglobe.C.Parameters.ZZ;
import static si.vajnartech.vajnarglobe.C.TAG;
import static si.vajnartech.vajnarglobe.C.scale;
import static si.vajnartech.vajnarglobe.C.xOffset;
import static si.vajnartech.vajnarglobe.C.yOffset;

@SuppressWarnings("InfiniteLoopStatement")
@SuppressLint("ViewConstructor")
public class WhereAmI extends GPS
{
  MainActivity act;

  boolean  ready = false;
  Paint    paint = new Paint();
  Function fv    = new Function();
  Function fs    = new Function();
  D        ds    = new D();
  D        dt    = new D();
  D dv;
  Point currentPosition = null;
  Point aproxPosition   = null;
  long currentTime;

  VectorField H = new VectorField()
  {
    @Override
    void done(Vector v)
    {
      _hector(v);
    }
  };

  Aproximator A = new Aproximator(1)
  {
    @Override void go()
    {
      if (fs != null) {
        Vector r = fs.f(System.currentTimeMillis());
        if (r == null)
          return;
        aproxPosition = r.toPoint();
        ctx.runOnUiThread(new Runnable()
        {
          @Override public void run()
          {
            invalidate();
          }
        });
      }

    }
  };

  WhereAmI(MainActivity ctx)
  {
    super(ctx);
    act = ctx;
    A.start();
  }

  @Override
  protected void notifyMe(Vector point)
  {
    H.add(point);
  }

  private void _hector(Vector point)
  {
    currentTime = System.currentTimeMillis();
    dv = new D();
    fs.put(currentTime, point);
    ds._up(point);
    dt._up(currentTime);
    dv._is(ds._po(dt));
    fv.put(currentTime, dv);
    currentPosition = point.toPoint();
    ctx.runOnUiThread(new Runnable()
    {
      @Override public void run()
      {
        invalidate();
      }
    });
  }

  @Override
  protected void notifyMe(Location loc)
  {
    Location currentLocation = new Location(loc);
    xOffset = new Normal(currentLocation.getLongitude(), 3).value();
    yOffset = new Normal(currentLocation.getLatitude(), 2).value();
    if (!ready) {
      new GetAreas(new Runnable()
      {
        @Override
        public void run()
        {
          invalidate();
        }
      });
      ready = true;
    }
  }

  @Override
  protected void onDraw(Canvas canvas)
  {
    super.onDraw(canvas);
    if (!ready)
      return;
    for (Area a : C.areas.values())
      _drawArea(a, canvas);
  }

  private void _drawArea(Area area, Canvas canvas)
  {
    fs.draw(canvas, paint, Color.GRAY, area);
    area.draw(canvas, paint, Color.BLACK);
    if (aproxPosition != null)
      aproxPosition.draw(canvas, paint, Color.GREEN, area);
    if (currentPosition == null)
      return;
    currentPosition.draw(canvas, paint, Color.RED, area);
    if (!area.isInside(currentPosition))
      return;
    ArrayList<Point> closestPoints = area.process(currentPosition);
    int              i             = -1;
    for (Point p : closestPoints) {
      i++;
      if (area.get(i).onMe(p))
        p.draw(canvas, paint, Color.GREEN, area);

      Vector sp = fs.f("first");
      if (sp == null)
        continue;
      Point startPoint = sp.toPoint();
      startPoint.draw(canvas, paint, Color.BLUE, area);
      _predict(startPoint, canvas, area, i);
    }
  }

  private void _predict(Point startPoint, Canvas canvas, Area area, int i)
  {
    Line approx = new Line(startPoint, currentPosition);
    fs.f(currentTime + 2000).toPoint().draw(canvas, paint, Color.MAGENTA, 3, area);
    approx.draw(canvas, paint, Color.RED, area);

    Point predictor = approx.intersection(area.get(i));
    if (predictor != null && area.get(i).onMe(predictor))
      predictor.draw(canvas, paint, Color.MAGENTA, 5, area);
    else
      return;

    Vector ttt  = new Vector(predictor.x, predictor.y);
    Vector ccc  = new Vector(currentPosition.x, currentPosition.y);
    Vector qqq  = ttt._minus(ccc);
    Vector sume = fv.integral();
    Vector time = new Vector(Math.abs(qqq.x / sume.x), Math.abs(qqq.y / sume.y));
    Log.i(TAG, String.format("do meje %d bos prisel cez ", i) + (time.x + time.y) / 1000 + " sekund");
  }

  @Override
  public boolean performClick()
  {
    return super.performClick();
  }
}

@SuppressWarnings("SuspiciousNameCombination")
class Function extends F<Vector>
{
  private F_V<LinearFun> fun = null;

  @Override
  Vector f(long t)
  {
    if (size() < 2 || fun == null)
      return null;
    return new Vector(fun.f1.f(t), fun.f2.f(t));
  }

  @Override
  Vector f(String s)
  {
    switch (s) {
    case "first":
      if (size() > ZZ)
        return get(keyAt(size() - ZZ));
      return null;
    }
    return null;
  }

  @Override
  Vector integral()
  {
    Vector sum = new Vector();
    for (int i = 0; i < size(); i++) {
      sum._plus_je(get(keyAt(i)));
    }
    sum._deljeno_je(new Vector(size(), size()));
    return sum;
  }

  @Override
  public void put(long key, Vector value)
  {
    super.put(key, value);
    if (size() > 2) {
      long k0 = keyAt(0);
      long kn = keyAt(size() - 1);

      Point p11 = new Point(k0, get(k0).x);
      Point p12 = new Point(kn, get(kn).x);
      Point p21 = new Point(k0, get(k0).y);
      Point p22 = new Point(kn, get(kn).y);
      fun = new F_V<>(new LinearFun(p11, p12), new LinearFun(p21, p22));
    }
  }

  void draw(Canvas c, Paint paint, int color, Area area)
  {
    if (size() > 1)
      for (int i = 0; i < size() - 1; i++)
        new Line(get(keyAt(i)).toPoint(), get(keyAt(i + 1)).toPoint()).draw(c, paint, color, area);
  }
}

class Normal
{
  private int res;

  Normal(double a1, double a2)
  {
    int    diff = 0;
    double b1   = a1;
    double b2   = a2;
    for (int i = 0; i < 10 && diff == 0; i++) {
      diff = Math.abs((int) b1 - (int) b2);
      b1 *= 10.0;
      b2 *= 10.0;
    }
    res = (int) b1;
  }

  Normal(double a1, int spoo)
  {
    int q = scale - spoo;
    res = (int) (a1 * Math.pow(10, q));
    res *= Math.pow(10, spoo);
  }

  public int value()
  {
    return res;
  }
}

