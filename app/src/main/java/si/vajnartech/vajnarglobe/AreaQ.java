package si.vajnartech.vajnarglobe;

import java.util.List;

@SuppressWarnings("WeakerAccess")
public class AreaQ
{
  public List<AreaP> areas;

  @SuppressWarnings("NullableProblems")
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

  @SuppressWarnings("NullableProblems")
  @Override
  public String toString()
  {
    return "AreaP{" +
           "name=" + name +
           ", points=" + points +
           '}';
  }
}
