package si.vajnartech.vajnarglobe;

import java.util.ArrayList;

import si.vajnartech.vajnarglobe.math.NumDouble2;

class AveragerOfPosition extends ArrayList<GeoPoint>
{
  private final AveragerRunnable    averagerRunnable;
  private final double              pointsNum;

  AveragerOfPosition(AveragerRunnable averagerRunnable, Double pointsNum)
  {
    this.averagerRunnable = averagerRunnable;
    this.pointsNum = pointsNum;
  }

  @Override
  public boolean add(GeoPoint point)
  {
    boolean res = super.add(point);
    if (size() == pointsNum) {
      averagerRunnable.averagerRun(average());
      clear();
    }

    return res;
  }

  private GeoPoint average()
  {
    GeoPoint tmp = new GeoPoint(0.0, 0.0);
    for (GeoPoint p: this)
      tmp.plus(p);

    tmp.div(new NumDouble2(pointsNum, pointsNum));
    return tmp;
  }

  public interface AveragerRunnable
  {
    void averagerRun(GeoPoint point);
  }
}
