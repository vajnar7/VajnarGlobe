package si.vajnartech.vajnarglobe;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;

import java.util.ArrayList;

import static si.vajnartech.vajnarglobe.C.scale;
import static si.vajnartech.vajnarglobe.C.xOffset;
import static si.vajnartech.vajnarglobe.C.yOffset;

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
  protected Point transform(Point p, boolean norm)
  {
    Point res = new Point(p.x, p.y);
    res.x *= Math.pow(10, scale);
    res.x -= xOffset;
    res.y *= Math.pow(10, scale);
    res.y -= yOffset;
    if (norm) {
      res.x -= min.x;
      res.y -= min.y;
    }
    res.x += C.O.x;
    res.y += C.O.y;
    res.y = C.size.y - res.y;
    return res;
  }

  @Override
  protected Area mark(GeoPoint a)
  {
    geoPoints.add(a);
    new SendLocation(getName(), a.timestamp, a.lon, a.lat);
    return this;
  }

  @Override
  protected ArrayList<Point> process(Point p)
  {
    ArrayList<Point> closestPoints = new ArrayList<>();
    for (Line l : this)
      closestPoints.add(getClosestPoint(l, p));
    return closestPoints;
  }

  private double _distance(Point p)
  {
    double d = 0;
    for (Line l : this) {
      if (l.f.isHorizontal)
        d = Math.abs(l.p1.y - p.y);
      else if (l.f.isVertical)
        d = Math.abs(l.p1.x - p.x);
      else
        // |ax0 + by0 + c| / sqr(a^2 + b^2)
        d = Math.abs(l.f.a * p.x + l.f.b * p.y + l.f.c);
      double k = Math.sqrt(l.f.a * l.f.a + l.f.b * l.f.b);
      d /= k;
    }
    Log.i("IZAA", "d(f(x), x0)=" + d);
    return d;
  }

  @Override
  public void draw(Canvas canvas, Paint paint, int color)
  {
    for (Line l: this) {
      l.draw(canvas, paint, color, this);
    }
  }

  @Override
  public void save()
  {
    for (GeoPoint p : geoPoints) {
      new SendLocation(getName(), p.timestamp, p.lon, p.lat);
      try {
        Thread.sleep(1000);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }
}
