package si.vajnartech.vajnarglobe;

import java.util.ArrayList;

import si.vajnartech.vajnarglobe.math.NumDouble2;

class AveragerOfPosition
{
  private final AveragerRunnable    averagerRunnable;
  private final ArrayList<GeoPoint> points = new ArrayList<>();
  private final double              pointsNum;

  AveragerOfPosition(AveragerRunnable averagerRunnable, Double pointsNum)
  {
    this.averagerRunnable = averagerRunnable;
    this.pointsNum = pointsNum == null ? 1 : pointsNum;
  }

  public boolean add(GeoPoint point)
  {
    points.add(point);
    if (points.size() == pointsNum) {
      averagerRunnable.averagerRun(average());
      points.clear();
    }
    return true;
  }

  private GeoPoint average()
  {
    GeoPoint tmp = new GeoPoint(0.0, 0.0);
    for (GeoPoint p: points)
      tmp.plus(p);

    tmp.div(new NumDouble2(pointsNum, pointsNum));
    return tmp;
  }

  public interface AveragerRunnable
  {
    void averagerRun(GeoPoint point);
  }
}
