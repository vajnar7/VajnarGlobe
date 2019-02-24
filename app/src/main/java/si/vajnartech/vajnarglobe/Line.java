package si.vajnartech.vajnarglobe;

import android.graphics.Canvas;
import android.graphics.Paint;

import si.vajnartech.vajnarglobe.math.Function;
import si.vajnartech.vajnarglobe.math.R2Double;

class Line
{
  R2Double p1;
  private R2Double p2;
  Fun f = null;

  Line(R2Double p1, R2Double p2)
  {
    this.p1 = p1;
    this.p2 = p2;
    _defineF();
  }

  R2Double intersection(Line l2)
  {
    double k = f.a - l2.f.a;
    double s = l2.f.c - f.c;

    // second line is vertical
    if (l2.f.isVertical)
      return new R2Double(l2.p1.get(0), f.a * l2.p1.get(0) + f.c);
    // second line is horizontal
    if (l2.f.isHorizontal)
      return new R2Double((l2.p1.get(1) - f.c) / f.a, l2.p1.get(1));

    if (k == 0 || s == 0)
      return null;

    double x1 = s / k;
    double x2 = f.a * x1 + f.c;
    return new R2Double(x1, x2);
  }

  boolean onMe(R2Double p)
  {
    R2Double a = p1;
    R2Double b = p2;
    // horizontalna
    if (f.isHorizontal) {
      if (p1.get(0) > p2.get(0)) {
        a = p2;
        b = p1;
      }
      if (p.get(0) > b.get(0) || p.get(0) < a.get(0))
        return false;
    }
    // vertical
    if (f.isVertical) {
      if (p1.get(1) > p2.get(1)) {
        a = p2;
        b = p1;
      }
      if (p.get(1) > b.get(1) || p.get(1) < a.get(1))
        return false;
    }
    if (a.get(0) > b.get(0)) {
      a = p2;
      b = p1;
    }
    if (f.a > 0)
      return (!(p.get(0) > b.get(0)) || !(p.get(1) > b.get(1))) && (!(p.get(0) < a.get(0)) || !(p.get(1) < a.get(1)));
    else
      return (!(p.get(0) > b.get(0)) || !(p.get(1) < b.get(1))) && (!(p.get(0) < a.get(0)) || !(p.get(1) > a.get(1)));
  }

  private void _defineF()
  {
    double a = p2.get(1) - p1.get(1); // if 0 horizontal
    double b = p2.get(0) - p1.get(0); // if 0 vertical
    double m;
    if (a == 0 && b != 0) {
      m = p2.get(0) > p1.get(0) ? 1 : -1;
      f = new Fun("horizontal", m);
    } else if (a != 0 && b == 0) {
      m = p2.get(1) > p1.get(1) ? 1 : -1;
      f = new Fun("vertical", m);
    } else if (a == 0)
      f = new Fun("invalid", 0);
    else {
      m = a / b;
      f = new Fun(m, -1, p1.get(1) - (m * p1.get(0)));
    }
  }

  class Fun
  {
    double  a;
    double  b            = 0;
    double  c            = 0;
    boolean isHorizontal = false;
    boolean isVertical   = false;
    boolean isInvalid    = false;

    Fun(String s, double m)
    {
      switch (s) {
      case "vertical":
        isVertical = true;
        break;
      case "horizontal":
        isHorizontal = true;
        break;
      case "invalid":
        isInvalid = true;
        break;
      }
      a = m;
    }

    Fun(double a, double b, double c)
    {
      this.a = a;
      this.b = b;
      this.c = c;
    }
  }

  void draw(Canvas c, Paint p, int color, Area a)
  {
    // render
    p.setColor(color);
    R2Double o1  = a.transform(new R2Double(p1.get(0), p1.get(1)), false);
    R2Double o2  = a.transform(new R2Double(p2.get(0), p2.get(1)), false);
    double   x11 = o1.get(0);
    double   x12 = o1.get(1);
    double   x21 = o2.get(0);
    double   x22 = o2.get(1);
    c.drawLine((float) x11, (float) x12, (float) x21, (float) x22, p);
  }

  @SuppressWarnings("NullableProblems")
  @Override
  public String toString()
  {
    return ("[" + p1 + "," + p2 + "]");
  }
}

@SuppressWarnings("WeakerAccess")
class LinearFun extends Function<Long, Double>
{
  boolean isHorizontal = false;
  boolean isVertical   = false;
  boolean isInvalid    = false;
  double  k            = 0;
  double  n            = 0;

  LinearFun(R2Double p1, R2Double p2)
  {
    double a = p2.get(1) - p1.get(1); // if 0 horizontal
    double b = p2.get(0) - p1.get(0); // if 0 vertical

    if (a == 0 && b != 0) {
      k = p2.get(0) > p1.get(0) ? 1 : -1;
      isHorizontal = true;
    } else if (a != 0 && b == 0) {
      k = p2.get(1) > p1.get(1) ? 1 : -1;
      isVertical = true;
    } else if (a == 0)
      isInvalid = true;
    else {
      k = a / b;
      n = p1.get(1) - (k * p1.get(0));
    }
  }

  @Override
  public Double f(Long x)
  {
    return k * x + n;
  }

  @Override public Double sum(Long x0, Long x1)
  {
    return null;
  }
}
