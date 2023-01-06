package si.vajnartech.vajnarglobe.rest;

import java.util.List;

public class AreasObj extends RestBaseObject
{
   public List<AreaObj> response;

   @Override
   protected int getNumParams()
   {
      return 1;
   }

   static class AreaObj
   {
      public String name;
      public List<PointObj> points;
   }

   static class PointObj
   {
      public long   timestamp;
      public double lon;
      public double lat;
   }
}
