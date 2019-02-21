package si.vajnartech.vajnarglobe;

import android.graphics.Canvas;
import android.graphics.Paint;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import si.vajnartech.vajnarglobe.math.R2Double;

abstract class Area extends ArrayList<Line>
{
  private String areaName;
  R2Double min;
  ArrayList<GeoPoint> geoPoints = new ArrayList<>();
  private ArrayList<R2Double> points = new ArrayList<>();

  Area(String name, ArrayList<GeoPoint> p)
  {
    areaName = name;
    geoPoints.addAll(p);
    _sortPoints(geoPoints);
    for (GeoPoint point : geoPoints)
      points.add(new R2Double(point.lon, point.lat));
    min = min(points);
    min = transform(min, false);
  }

  Area(String name)
  {
    areaName = name;
  }

  Area constructArea()
  {
    for (int i = 0; i < points.size() - 1; i++)
      add(new Line(points.get(i), points.get(i + 1)));
    add(new Line(points.get(points.size() - 1), points.get(0)));
    return this;
  }

  private void _sortPoints(ArrayList<GeoPoint> p)
  {
    Collections.sort(p, new Comparator<GeoPoint>()
    {
      @Override
      public int compare(GeoPoint o1, GeoPoint o2)
      {
        return o1.timestamp - o2.timestamp;
      }
    });
  }

  public String getName()
  {
    return areaName;
  }

  @SuppressWarnings("UnusedReturnValue")
  abstract protected Area mark(GeoPoint a);

  abstract protected ArrayList<R2Double> process(R2Double p);

  abstract public void draw(Canvas canvas, Paint paint, int color);

  protected boolean isInside(R2Double p)
  {
    boolean oddNodes = false;
    int     j        = size() - 1;
    int     i;

    for (i = 0; i < size(); i++) {
      double iy = get(i).p1.get(1);
      double ix = get(i).p1.get(0);
      double jy = get(j).p1.get(1);
      double jx = get(j).p1.get(0);
      if ((iy < p.get(1) && jy >= p.get(1) || jy < p.get(1) && iy >= p.get(1)) && (ix <= p.get(0) || jx <= p.get(0)))
        if (ix + (p.get(1) - iy) / (jy - iy) * (jx - ix) < p.get(0))
          oddNodes = !oddNodes;
      j = i;
    }
    return oddNodes;
  }

  R2Double getClosestPoint(Line l, R2Double p)
  {
    if (l.f.isHorizontal)
      return new R2Double(p.get(0), l.p1.get(1));
    if (l.f.isVertical)
      return new R2Double(l.p1.get(0), p.get(1));
    double a, b, c;
    a = l.f.a;
    b = l.f.b;
    c = l.f.c;
    double k  = (l.f.a * l.f.a + l.f.b * l.f.b); //a^2 + b^2
    double qx = b * (b * p.get(0) - a * p.get(1)) - a * c;
    double qy = a * (-b * p.get(0) + a * p.get(1)) - b * c;
    return new R2Double(qx / k, qy / k);
  }

  protected abstract void save();

  @SuppressWarnings("SameParameterValue")
  abstract R2Double transform(R2Double p, boolean norm);

  private R2Double min(ArrayList<R2Double> points)
  {
    double x1 = points.get(0).get(0);
    double x2 = points.get(0).get(1);
    for (R2Double o : points) {
      if (o.get(0) < x1)
        x1 = o.get(0);
      if (o.get(1) < x2)
        x2 = o.get(1);
    }
    return new R2Double(x1, x2);
  }
}