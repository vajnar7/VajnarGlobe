package si.vajnartech.vajnarglobe;

import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import static si.vajnartech.vajnarglobe.C.GET_ALL;

public class GetAreas extends REST<AreaObj>
{
  private Runnable r = null;
  private Runnable onFail;

  private GetAreas(final MainActivity act)
  {
    super(GET_ALL, act);
    onFail = new Runnable() {
      @Override public void run()
      {
        Toast.makeText(act, act.tx(R.string.server_conn_error), Toast.LENGTH_LONG).show();
      }
    };
  }

  GetAreas(MainActivity act, Runnable run)
  {
    this(act);
    r = run;
  }

  @Override
  public AreaObj backgroundFunc()
  {
    return callServer(null, REST.OUTPUT_TYPE_JSON);
  }

  @Override
  protected void onPostExecute(AreaObj j)
  {
    super.onPostExecute(j);
    if (j != null) {
      C.areas.clear();
      for (AreaObj.Area a : j.response) {
        if (a.points.size() < 3) continue;
        ArrayList<GeoPoint> points = new ArrayList<>();
        for (AreaObj.Point p : a.points)
          points.add(new GeoPoint(p.lon, p.lat));
        Area newArea = new Place(a.name, points);
        C.areas.put(a.name, newArea);
        newArea.constructArea();
      }
      if (r != null )
        r.run();
    }
    else
      onFail.run();
  }
}

@SuppressWarnings({"NullableProblems", "WeakerAccess"})
class AreaObj
{
  public List<Area> response;

  @Override
  public String toString()
  {
    return "AreaObj{" +
           "response=" + response +
           '}';
  }

  @SuppressWarnings("InnerClassMayBeStatic")
  class Area
  {
    public String name;
    public List<Point> points;

    @Override
    public String toString()
    {
      return "Point{" +
             "name=" + name +
             ", points=" + points +
             '}';
    }
  }

  static class Point
  {
    public long   timestamp;
    public double lon;
    public double lat;

    @Override
    public String toString()
    {
      return "Point{" +
             "timestamp=" + timestamp +
             ", lon=" + lon +
             ", lat=" + lat +
             '}';
    }
  }
}