package si.vajnartech.vajnarglobe;

import android.graphics.Canvas;
import android.graphics.Paint;

import si.vajnartech.vajnarglobe.math.LinearFunction;
import si.vajnartech.vajnarglobe.math.NumDouble2;

class Line
{
  protected NumDouble2 p1;
  protected NumDouble2 p2;

  protected LinearFunction function;

  Line(NumDouble2 p1, NumDouble2 p2)
  {
    this.p1 = p1;
    this.p2 = p2;
    defineFunction();
  }

  NumDouble2 intersection(Line l2)
  {
    double k = function.k - l2.function.k;
    double s = l2.function.n - function.n;

    // second line is vertical
    if (l2.function.isVertical)
      return new NumDouble2(l2.p1.get(0), function.k * l2.p1.get(0) + function.n);
    // second line is horizontal
    if (l2.function.isHorizontal)
      return new NumDouble2((l2.p1.get(1) - function.n) / function.k, l2.p1.get(1));

    if (k == 0 || s == 0)
      return null;

    double x1 = s / k;
    double x2 = function.k * x1 + function.n;
    return new NumDouble2(x1, x2);
  }

  boolean onMe(NumDouble2 p)
  {
    NumDouble2 a = p1;
    NumDouble2 b = p2;
    // horizontalna
    if (function.isHorizontal) {
      if (p1.get(0) > p2.get(0)) {
        a = p2;
        b = p1;
      }
      return p.get(0) <= b.get(0) && p.get(0) >= a.get(0);
    }
    // vertical
    if (function.isVertical) {
      if (p1.get(1) > p2.get(1)) {
        a = p2;
        b = p1;
      }
      return p.get(1) <= b.get(1) && p.get(1) >= a.get(1);
    }
    if (a.get(0) > b.get(0)) {
      a = p2;
      b = p1;
    }
    if (function.k > 0)
      return (!(p.get(0) > b.get(0)) || !(p.get(1) > b.get(1))) && (!(p.get(0) < a.get(0)) || !(p.get(1) < a.get(1)));
    else
      return (!(p.get(0) > b.get(0)) || !(p.get(1) < b.get(1))) && (!(p.get(0) < a.get(0)) || !(p.get(1) > a.get(1)));
  }

  private void defineFunction()
  {
    Double k;
    Double n;

    double dx = p2.get(0) - p1.get(0); // if 0 vertical
    double dy = p2.get(1) - p1.get(1); // if 0 horizontal

    if (dy == 0) {
      k = 0.0;
      n = p1.get(1);
    }
    else if (dx == 0) {
      k = null;
      n = null;
    }
    else {
      k = dy / dx;
      n = p1.get(1) - k * p1.get(0);
    }
    function = new LinearFunction(k, n);
  }

  GeoPoint getClosestPoint(GeoPoint p)
  {
    if (function.isHorizontal)
      return new GeoPoint(p.get(0), p1.get(1));
    if (function.isVertical)
      return new GeoPoint(p1.get(0), p.get(1));
    double a, b, c;
    a = function.k;
    b = -1;
    c = function.n;
    double k  = (a * a + 1); //a^2 + b^2
    double qx = b * (b * p.get(0) - a * p.get(1)) - a * c;
    double qy = a * (-b * p.get(0) + a * p.get(1)) - b * c;
    return new GeoPoint(qx / k, qy / k);
  }

  void draw(Canvas canvas, Paint paint, int color, Transform transform)
  {
    paint.setColor(color);
    NumDouble2 o1  = transform.transform(p1);
    NumDouble2 o2  = transform.transform(p2);

    double   x11 = o1.get(0);
    double   x12 = o1.get(1);
    double   x21 = o2.get(0);
    double   x22 = o2.get(1);
    canvas.drawLine((float) x11, (float) x12, (float) x21, (float) x22, paint);
  }

  @SuppressWarnings("NullableProblems")
  @Override
  public String toString()
  {
    return ("[" + p1 + "," + p2 + "]");
  }
}
