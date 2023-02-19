package si.vajnartech.vajnarglobe.rest;

import java.util.List;

class AreaObjR
{
  public String         name;
  public List<PointObj> points;

  @SuppressWarnings("unused")
  public static class PointObj
  {
    public long   timestamp;
    public double lon;
    public double lat;
  }
}
