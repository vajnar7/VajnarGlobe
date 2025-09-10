package si.vajnartech.vajnarglobe;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;

import java.util.ArrayList;

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
    String user = new SharedPref(act).getString("username");
    new Areas(act, geoPoints, areaName, user,
            () -> new Areas("GET", act, "", user, null)
    );
  }

  public void delete(MainActivity act) {
    String user = new SharedPref(act).getString("username");
    new Areas("DELETE", act, areaName, user,
            () -> new Areas("GET", act, "", user, null)
    );
  }

  @Override
  protected ArrayList<GeoPoint> process(GeoPoint point)
  {
    ArrayList<GeoPoint> closestPoints = new ArrayList<>();
    for (Line l : this)
      closestPoints.add(l.getClosestPoint(point));
    return closestPoints;
  }

  @Override
  public void draw(Canvas canvas, Paint paint, int color, Transform tr)
  {
    for (Line l : this)
      l.draw(canvas, paint, color, tr);
  }
}
