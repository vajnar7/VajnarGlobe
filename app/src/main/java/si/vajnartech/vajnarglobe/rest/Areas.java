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
  ArrayList<GeoPoint> geoPoints;
  String user;
  String name;

  public Areas(String requestMethod, MainActivity act, String name, String user, Runnable runAfter)
  {
    super(C.AREAS_API, requestMethod, act);
    this.runAfter = runAfter;
    this.user = user;
    this.name = name;
  }

  public Areas(MainActivity act, ArrayList<GeoPoint> geoPoints, String name, String user, Runnable runAfter)
  {
    super(C.AREAS_API, "POST", act);
    this.geoPoints = geoPoints;
    this.user = user;
    this.name = name;
    this.runAfter = runAfter;
  }

  @Override
  protected void onPostExecute(AreasObj areasObj)
  {
    if (areasObj == null)
    {
      onFail();
      return;
    }

    if (!areasObj.response.isEmpty()) {
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
    }

    if (runAfter != null)
      runAfter.run();
  }

  @Override
  protected AreasObj backgroundFunc()
  {
      switch (requestMethod) {
          case "POST":
              return callServer(new AreaObj(geoPoints, name, user));
          case "DELETE":
              return callServer(new AreaObj(new ArrayList<>(), name, user));
          case "GET":
              requestMethod = "POST";
              break;
      }

    return callServer(new AreaObj(new ArrayList<>(), "", user));
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


