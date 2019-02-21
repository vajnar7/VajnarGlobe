package si.vajnartech.vajnarglobe;


import android.annotation.SuppressLint;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.location.Location;
import android.util.Log;

import java.util.ArrayList;

import si.vajnartech.vajnarglobe.math.Function;
import si.vajnartech.vajnarglobe.math.R2Double;
import si.vajnartech.vajnarglobe.math.R2Function;

import static si.vajnartech.vajnarglobe.C.Parameters.ZZ;
import static si.vajnartech.vajnarglobe.C.TAG;
import static si.vajnartech.vajnarglobe.C.scale;
import static si.vajnartech.vajnarglobe.C.xOffset;
import static si.vajnartech.vajnarglobe.C.yOffset;

@SuppressLint("ViewConstructor")
public class WhereAmI extends GPS
{
  MainActivity act;

  boolean  ready = false;
  Paint    paint = new Paint();
  MyFunction fv    = new MyFunction();
  MyFunction fs    = new MyFunction();
  D        ds    = new D();
  D        dt    = new D();
  D dv;
  R2Double currentPosition = null;
  R2Double aproxPosition   = null;
  long currentTime;

  VectorField H = new VectorField()
  {
    @Override
    void done(R2Double v)
    {
      _hector(v);
    }
  };

  Aproximator A = new Aproximator(1)
  {
    @Override
    void go()
    {
      if (fs != null) {
        R2Double r = fs.f(System.currentTimeMillis());
        if (r == null) return;
        aproxPosition = (Point) r;
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
  protected void notifyMe(R2Double point)
  {
    H.add(point);
  }

  private void _hector(R2Double point)
  {
    currentTime = System.currentTimeMillis();
    fs.put(currentTime, point);
    fv.put(currentTime, dv);
    currentPosition = (Point) point;
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
      new GetAreas(act, new Runnable()
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

    Point ttt  = new Point(predictor.x, predictor.y);
    R2Double ccc  = new R2Double(currentPosition.x, currentPosition.y);
    R2Double qqq  = ttt._minus(ccc);
    R2Double sume = fv.integral();
    R2Double time = new R2Double(Math.abs(qqq.x / sume.x), Math.abs(qqq.y / sume.y));
    Log.i(TAG, String.format("do meje %d bos prisel cez ", i) + (time.x + time.y) / 1000 + " sekund");
  }

  @Override
  public boolean performClick()
  {
    return super.performClick();
  }
}

class MyFunction extends Function<Long, R2Double>
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
  public R2Double integral(Long x0, Long x1)
  {
    R2Double sum = new R2Double(0.0, 0.0);
    for (int i = keys.indexOf(x0); i <= keys.indexOf(x1); i++)
      sum.is(sum.plus(get(keys.get(i))));
    return sum;
  }

  @SuppressWarnings("ConstantConditions")
  public R2Double fi(Long x)
  {
    R2Double df;
    if (size() < 2) return null;
    int j = keys.indexOf(x);
    if (j == size() - 1) return null;
    df = new R2Double(0.0, 0.0);
    df.is(get(keys.get(j+1)).minus(get(keys.get(j))));
    long dx = keys.get(j+1) - keys.get(j);
    df.is(df.div((double) dx));
    return df;
  }

  @SuppressWarnings("ConstantConditions") @Override
  public R2Double put(Long key, R2Double value)
  {
    R2Double res = super.put(key, value);
    if (size() > 2) {
      long k0 = keys.get(0);
      long kn = keys.get(size() - 1);
      Point p11 = new Point(k0, get(k0).get(0));
      Point p12 = new Point(kn, get(kn).get(0));
      Point p21 = new Point(k0, get(k0).get(1));
      Point p22 = new Point(kn, get(kn).get(1));
      fun = new R2Function<>(new LinearFun(p11, p12), new LinearFun(p21, p22));
    }
    return res;
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

  int value()
  {
    return res;
  }
}

