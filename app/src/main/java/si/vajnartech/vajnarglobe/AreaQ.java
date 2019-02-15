package si.vajnartech.vajnarglobe;

import java.util.List;

@SuppressWarnings("WeakerAccess")
public class AreaQ
{
  public List<AreaP> areas;

  @Override
  public String toString()
  {
    return "AreaQ{" +
           "areas=" + areas +
           '}';
  }
}

@SuppressWarnings("WeakerAccess")
class AreaP
{
  public String         name;
  public List<GeoPoint> points;

  @Override
  public String toString()
  {
    return "AreaP{" +
           "name=" + name +
           ", points=" + points +
           '}';
  }
}

@SuppressWarnings("WeakerAccess")
class GeoPoint
{
  public int timestamp;
  public double lon;
  public double lat;

  GeoPoint(double lon, double lat)
  {
    this.timestamp = C.stamp.getAndIncrement();
    this.lon = lon;
    this.lat = lat;
  }

  @Override
  public String toString()
  {
    return "GeoPoint{" +
           "timestamp=" + timestamp +
           "lon=" + lon +
           ", lat=" + lat +
           '}';
  }
}
