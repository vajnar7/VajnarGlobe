package si.vajnartech.vajnarglobe;

import si.vajnartech.calculus.R2Double;

@SuppressWarnings("NullableProblems")
public class GeoPoint extends R2Double
{
  long    timestamp;
  double lon;
  double lat;

  GeoPoint(double lon, double lat)
  {
    super(lon, lat);
    this.timestamp = System.currentTimeMillis();
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
