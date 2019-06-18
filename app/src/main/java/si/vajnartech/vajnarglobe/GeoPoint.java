package si.vajnartech.vajnarglobe;

import si.vajnartech.vajnarglobe.math.R2Double;

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

  @SuppressWarnings("NullableProblems")
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
