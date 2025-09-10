package si.vajnartech.vajnarglobe.rest;

import java.util.ArrayList;
import java.util.List;

import si.vajnartech.vajnarglobe.GeoPoint;

public class AreaObj extends RestBaseObject
{
  public String name;
  public String user;
  public List<PointObj> points;

  public AreaObj(List<GeoPoint> geoPoints, String name, String user)
  {
    this.name = name;
    this.user = user;
    this.points = new ArrayList<>();

    for (GeoPoint point: geoPoints)
     points.add(new PointObj(point.timestamp, point.lon, point.lat));
  }

  public static class PointObj
  {
    public long timestamp;
    public double lon;
    public double lat;

    public PointObj(long timestamp, double lon, double lat)
    {
      this.timestamp = timestamp;
      this.lon = lon;
      this.lat = lat;
    }
  }

}
