package si.vajnartech.vajnarglobe;

import java.util.ArrayList;

import si.vajnartech.calculus.R2Double;

public abstract class VectorField extends ArrayList<GeoPoint>
{
  @Override
  public boolean add(GeoPoint v)
  {
    if (C.Parameters.lim.getAndDecrement() > 0)
      return true;
    super.add(v);
    if (size() == C.Parameters.avaragePoints) {
      done(average());
      clear();
    }
    return true;
  }

  private GeoPoint average()
  {
    GeoPoint point = new GeoPoint(0.0, 0.0);
    for (GeoPoint p: this)
      point.is(point.plus(p));
    point.is(point.divS((double) C.Parameters.avaragePoints));
    return point;
  }

  abstract void done(GeoPoint point);
}
