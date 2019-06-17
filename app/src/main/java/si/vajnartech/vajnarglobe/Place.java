package si.vajnartech.vajnarglobe;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;

import java.util.ArrayList;

import si.vajnartech.vajnarglobe.math.R2Double;

import static si.vajnartech.vajnarglobe.C.scale;
import static si.vajnartech.vajnarglobe.C.xOffset;
import static si.vajnartech.vajnarglobe.C.yOffset;

@SuppressWarnings("SuspiciousNameCombination")
public class Place extends Area
{
  Place(String name, ArrayList<GeoPoint> points)
  {
    super(name, points);
  }

  Place(String name)
  {
    super(name);
  }

  @Override
  protected R2Double transform(R2Double p, boolean norm)
  {
    double x1 = p.get(0);
    double x2 = p.get(1);
    x1 *= Math.pow(10, scale);
    x1 -= xOffset;
    x2 *= Math.pow(10, scale);
    x2 -= yOffset;
    if (norm) {
      x1 -= min.get(0);
      x2 -= min.get(1);
    }
    x1 += C.O.get(0);
    x2 += C.O.get(1);
    x2 = C.size.y - x2;
    return new R2Double(x1, x2);
  }

  @Override
  protected void mark(GeoPoint a)
  {
    geoPoints.add(a);
    new SendLocation(getName(), a.timestamp, a.lon, a.lat);
  }

  @Override
  protected ArrayList<R2Double> process(R2Double p)
  {
    ArrayList<R2Double> closestPoints = new ArrayList<>();
    for (Line l : this)
      closestPoints.add(getClosestPoint(l, p));
    return closestPoints;
  }

  private double _distance(R2Double p)
  {
    double d = 0;
    for (Line l : this) {
      if (l.f.isHorizontal)
        d = Math.abs(l.p1.get(1) - p.get(1));
      else if (l.f.isVertical)
        d = Math.abs(l.p1.get(0) - p.get(0));
      else
        // |ax0 + by0 + c| / sqr(a^2 + b^2)
        d = Math.abs(l.f.a * p.get(0) + l.f.b * p.get(1) + l.f.c);
      double k = Math.sqrt(l.f.a * l.f.a + l.f.b * l.f.b);
      d /= k;
    }
    return d;
  }

  @Override
  public void draw(Canvas canvas, Paint paint, int color)
  {
    for (Line l : this) {
      l.draw(canvas, paint, color, this);
    }
  }
}
