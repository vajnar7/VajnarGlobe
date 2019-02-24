package si.vajnartech.vajnarglobe;

public class GeoPoint
{
  int    timestamp;
  double lon;
  double lat;

  GeoPoint(double lon, double lat)
  {
    this.timestamp = C.stamp.getAndIncrement();
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
