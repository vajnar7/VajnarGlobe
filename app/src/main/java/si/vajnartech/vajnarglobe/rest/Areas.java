package si.vajnartech.vajnarglobe.rest;

import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.Objects;

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
  ArrayList<GeoPoint> geoPoints;
  String name;

  public Areas(String requestMethod, Runnable runAfter, MainActivity act)
  {
    super(C.AREAS_API, requestMethod, act);
    this.runAfter = runAfter;
  }

  public Areas(MainActivity act, ArrayList<GeoPoint> geoPoints, String name)
  {
    super(C.AREAS_API, "POST", act);
    this.runAfter = null;
    this.geoPoints = geoPoints;
    this.name = name;
  }

  @Override
  protected void onPostExecute(AreasObj areasObj)
  {
    if (Objects.equals(requestMethod, "DELETE")) {
      Log.i(C.TAG, "TODO DELETE");
    } else if (Objects.equals(requestMethod, "POST")) {
      if (areasObj.return_code != 0)
        onFail();
    } else if (areasObj != null) {
      if (areasObj.response.isEmpty())
        Toast.makeText(act.get(), R.string.no_areas, Toast.LENGTH_LONG).show();
      C.areas.clear();
      for (AreaObjR a : areasObj.response) {
        if (a.points.size() < 3) continue;
        ArrayList<GeoPoint> points = new ArrayList<>();
        for (AreaObjR.PointObj p : a.points)
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
    if (Objects.equals(requestMethod, "POST")) {
      return callServer(new AreaObj(geoPoints, name));
    }
    return callServer(null);
  }

  @Override
  protected void onFail()
  {
    act.get().runOnUiThread(() -> Toast.makeText(act.get(), R.string.server_conn_error, Toast.LENGTH_LONG).show());
  }

  @Override
  protected AreasObj deserialize(BufferedReader br)
  {
    return Json.fromJson(br, AreasObj.class);
  }
}


