package si.vajnartech.vajnarglobe;

import android.graphics.Canvas;
import android.graphics.Paint;

import java.util.ArrayList;
import java.util.Comparator;

import si.vajnartech.vajnarglobe.math.NumDouble2;

abstract public class Area extends ArrayList<Line>
{
  protected String areaName;

  protected ArrayList<GeoPoint> geoPoints = new ArrayList<>();

  Area(String name, ArrayList<GeoPoint> p)
  {
    areaName = name;
    geoPoints.addAll(p);
    sortPoints(geoPoints);
  }

  Area(String name)
  {
    areaName = name;
  }

  protected ArrayList<GeoPoint> getGeoPoints()
  {
    return geoPoints;
  }
  public boolean constructArea()
  {
    if (geoPoints.size() < 3)
      return false;
    sortPoints(geoPoints);
    for (int i = 0; i < geoPoints.size() - 1; i++)
      add(new Line(geoPoints.get(i), geoPoints.get(i + 1)));
    add(new Line(geoPoints.get(geoPoints.size() - 1), geoPoints.get(0)));
    return true;
  }

  GeoPoint getFirstPoint()
  {
    return geoPoints.get(0);
  }

  private void sortPoints(ArrayList<GeoPoint> p)
  {
    p.sort(Comparator.comparingLong(o -> o.timestamp));
  }

  abstract protected void mark(GeoPoint a);

  abstract public void push(MainActivity act);

  abstract protected ArrayList<GeoPoint> process(GeoPoint point);

  abstract public void draw(Canvas canvas, Paint paint, int color, Transform tr);

  boolean isInside(NumDouble2 p)
  {
    boolean oddNodes = false;

    int j = size() - 1;
    int i;

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

  private NumDouble2 min(ArrayList<GeoPoint> points)
  {
    double x1 = points.get(0).get(0);
    double x2 = points.get(0).get(1);
    for (NumDouble2 o : points) {
      if (o.get(0) < x1)
        x1 = o.get(0);
      if (o.get(1) < x2)
        x2 = o.get(1);
    }
    return new NumDouble2(x1, x2);
  }
}