package si.vajnartech.vajnarglobe;

import android.graphics.Canvas;
import android.graphics.Paint;

import java.util.ArrayList;

import si.vajnartech.calculus.R2Double;
import si.vajnartech.calculus.Transformator;
import si.vajnartech.vajnarglobe.rest.Areas;

public class Place extends Area
{
  public Place(String name, ArrayList<GeoPoint> points)
  {
    super(name, points);
  }

  Place(String name)
  {
    super(name);
  }

  @Override
  protected void mark(GeoPoint a)
  {
    geoPoints.add(a);
  }

  @Override
  public void push(MainActivity act)
  {
    new Areas(act, geoPoints, areaName);
  }

  @Override
  protected ArrayList<R2Double> process(R2Double p)
  {
    ArrayList<R2Double> closestPoints = new ArrayList<>();
    for (Line l : this)
      closestPoints.add(getClosestPoint(l, p));
    return closestPoints;
  }

  @Override
  public void draw(Canvas canvas, Paint paint, int color, Transformator tr)
  {
    for (Line l : this)
      l.draw(canvas, paint, color, tr);
  }
}
