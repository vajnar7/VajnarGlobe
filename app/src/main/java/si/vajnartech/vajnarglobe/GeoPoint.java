package si.vajnartech.vajnarglobe;

import si.vajnartech.calculus.R2Double;

public class GeoPoint extends R2Double
{
  public long timestamp;
  public double lon;
  public double lat;

  public GeoPoint(double lon, double lat)
  {
    super(lon, lat);
    this.timestamp = System.currentTimeMillis();
    this.lon = lon;
    this.lat = lat;
  }
}
