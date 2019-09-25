package si.vajnartech.vajnarglobe;


import android.annotation.SuppressLint;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.location.Location;
import android.util.Log;

import java.util.ArrayList;

import si.vajnartech.vajnarglobe.math.Derivative;
import si.vajnartech.vajnarglobe.math.Function;
import si.vajnartech.vajnarglobe.math.R2Double;
import si.vajnartech.vajnarglobe.math.R2Function;

import static si.vajnartech.vajnarglobe.C.Parameters.ZZ;
import static si.vajnartech.vajnarglobe.C.TAG;
import static si.vajnartech.vajnarglobe.C.scale;

@SuppressLint("ViewConstructor")
public class WhereAmI extends GPS implements Transformator
{
  MainActivity act;

  boolean    ready           = false;
  Paint      paint           = new Paint();
  MyFunction fs              = new MyFunction();
  R2Double   currentPosition = null;
  R2Double   aproxPosition   = null;
  long       currentTime;

  Derivative<Long, R2Double> fv =
  new Derivative<Long, R2Double>(fs) {
    @SuppressWarnings("ConstantConditions")
    @Override
    public R2Double f(Long x)
    {
      R2Double df = new R2Double();
      if (size() < 2) return null;
      int j = getKeys().indexOf(x);
      if (j == size() - 1) return null;
      df.is(get(getKeyAt(j + 1)).minus(get(getKeyAt(j))));
      long dx = getKeys().get(j + 1) - getKeys().get(j);
      R2Double res = new R2Double();
      res.is(df.divS((double) dx));
      return res;
    }

    @Override
    public R2Double sum(Long x0, Long x1)
    {
      int i0 = getKeys().indexOf(x0);
      int i1 = getKeys().indexOf(x1);
      if (x0 == null)
      {
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
  };

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
        aproxPosition = fs.f(System.currentTimeMillis());
        if (aproxPosition == null) return;
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

//  @Override
  // TODO what is this
//  protected void notifyMe(R2Double point)
//  {
//    H.add(point);
//  }

  private void _hector(R2Double point)
  {
    currentTime = System.currentTimeMillis();
    fs.put(currentTime, point);
    currentPosition = point;
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
//    Location currentLocation = new Location(loc);
//    xOffset = new Normal(currentLocation.getLongitude(), 3).value();
//    yOffset = new Normal(currentLocation.getLatitude(), 2).value();
//    if (!ready) {
//      new GetAreas(act, new Runnable()
//      {
//        @Override
//        public void run()
//        {
//          invalidate();
//        }
//      });
//      ready = true;
//    }
  }

  @Override
  public R2Double transform(R2Double p)
  {
    return null;
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

  @Override protected R2Double setOrigin()
  {
    return null;
  }

  private void _drawArea(Area area, Canvas canvas)
  {
    fs.draw(canvas, paint, Color.GRAY, this);
    area.draw(canvas, paint, Color.BLACK, this);
    if (aproxPosition != null)
      aproxPosition.draw(canvas, paint, Color.GREEN, 4, this);
    if (currentPosition == null)
      return;
    currentPosition.draw(canvas, paint, Color.RED, 4, this);
    if (area.isInside(currentPosition))
      return;
    ArrayList<R2Double> closestPoints = area.process(currentPosition);
    int i = -1;
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

  @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
  private void _predict(R2Double startPoint, Canvas canvas, Area area, int i)
  {
    Line approx = new Line(startPoint, currentPosition);
    fs.f(currentTime + 2000).draw(canvas, paint, Color.MAGENTA, 4, this);
    approx.draw(canvas, paint, Color.RED, this);

    R2Double predictor = approx.intersection(area.get(i));
    if (predictor != null && area.get(i).onMe(predictor))
      predictor.draw(canvas, paint, Color.MAGENTA, 4, this);
    else
      return;

    R2Double qqq = new R2Double();
    qqq.is(predictor.minus(currentPosition));
    R2Double sum = fv.sum(null, null);
    sum.is(sum.divS((double) fs.size()));
    R2Double time = new R2Double(Math.abs(qqq.x1() / sum.x1()), Math.abs(qqq.x2() / sum.x2()));
    Log.i(TAG, String.format("do meje %d bos prisel cez ", i) + (time.x1() + time.x2()) / 1000 + " sekund");
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

  @Override public R2Double sum(Long x0, Long x1)
  {
    return null;
  }

  @SuppressWarnings("SameParameterValue")
  R2Double f(String s)
  {
    if ("first".equals(s)) {
      if (size() > ZZ)
        return get(getKeys().get((size() - ZZ)));
      return null;
    }
    return null;
  }

  @SuppressWarnings("ConstantConditions")
  @Override
  public R2Double put(Long key, R2Double value)
  {
    R2Double res = super.put(key, value);
    if (size() > 2) {
      long  k0  = getKeys().get(0);
      long  kn  = getKeys().get(size() - 1);
      R2Double p11 = new R2Double((double) k0, get(k0).get(0));
      R2Double p12 = new R2Double((double) kn, get(kn).get(0));
      R2Double p21 = new R2Double((double) k0, get(k0).get(1));
      R2Double p22 = new R2Double((double) kn, get(kn).get(1));
      fun = new R2Function<>(new LinearFun(p11, p12), new LinearFun(p21, p22));
    }
    return res;
  }

  @SuppressWarnings("SameParameterValue")
  void draw(Canvas c, Paint paint, int color, Transformator tr)
  {
    if (size() > 1)
      for (int i = 0; i < size() - 1; i++)
        new Line(get(getKeyAt(i)), get(getKeyAt(i + 1))).draw(c, paint, color, tr);
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

