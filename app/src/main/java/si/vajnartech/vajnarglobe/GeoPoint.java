package si.vajnartech.vajnarglobe;


import si.vajnartech.vajnarglobe.math.NumDouble2;

public class GeoPoint extends NumDouble2
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
