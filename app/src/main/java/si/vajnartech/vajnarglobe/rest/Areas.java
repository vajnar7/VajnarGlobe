package si.vajnartech.vajnarglobe.rest;

import android.widget.Toast;

import com.vajnar.vajnargnss.OnFailInterface;

import java.io.BufferedReader;
import java.util.ArrayList;

import si.vajnartech.vajnarglobe.Area;
import si.vajnartech.vajnarglobe.C;
import si.vajnartech.vajnarglobe.GeoPoint;
import si.vajnartech.vajnarglobe.Json;
import si.vajnartech.vajnarglobe.Place;
import si.vajnartech.vajnarglobe.R;

public class Areas extends RestBase<AreasObj>
{
  private final Runnable runAfter;
  ArrayList<GeoPoint> geoPoints;
  String user;
  String name;

  public Areas(String requestMethod, String name, String user, Runnable runAfter,
               OnFailInterface onFail)
  {
    super(C.AREAS_API, requestMethod, onFail);
    this.runAfter = runAfter;
    this.user = user;
    this.name = name;
  }

  public Areas(ArrayList<GeoPoint> geoPoints, String name, String user, Runnable runAfter,
               OnFailInterface onFail)
  {
    super(C.AREAS_API, "POST", onFail);
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
      onFail.onFail();
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
  protected AreasObj deserialize(BufferedReader br)
  {
    return Json.fromJson(br, AreasObj.class);
  }
}


