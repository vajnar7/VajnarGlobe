package si.vajnartech.vajnarglobe.rest;

import android.widget.Toast;

import java.io.BufferedReader;
import java.util.ArrayList;

import si.vajnartech.vajnarglobe.Area;
import si.vajnartech.vajnarglobe.C;
import si.vajnartech.vajnarglobe.GeoPoint;
import si.vajnartech.vajnarglobe.Json;
import si.vajnartech.vajnarglobe.MainActivity;
import si.vajnartech.vajnarglobe.Place;
import si.vajnartech.vajnarglobe.R;

public class Areas extends RestBase<AreasObj>
{
  private final Runnable runAfter;

  public Areas(String requestMethod, Runnable runAfter, MainActivity act)
  {
    super(C.AREAS_API, requestMethod, act);
    this.runAfter = runAfter;
  }

  @Override
  protected void onPostExecute(AreasObj areasObj)
  {
    if (areasObj != null) {
      if (areasObj.response.isEmpty())
        Toast.makeText(act.get(), R.string.no_areas, Toast.LENGTH_LONG).show();
      C.areas.clear();
      for (AreasObj.AreaObj a : areasObj.response) {
        if (a.points.size() < 3) continue;
        ArrayList<GeoPoint> points = new ArrayList<>();
        for (AreasObj.PointObj p : a.points)
          points.add(new GeoPoint(p.lon, p.lat));
        Area newArea = new Place(a.name, points);
        C.areas.put(a.name, newArea);
        newArea.constructArea();
      }
      if (runAfter != null)
        runAfter.run();
    } else
      onFail();
  }

  @Override
  protected AreasObj backgroundFunc()
  {
    return callServer(null);
  }

  @Override
  protected void onFail()
  {

  }

  @Override
  protected AreasObj deserialize(BufferedReader br)
  {
    return Json.fromJson(br, AreasObj.class);
  }
}


