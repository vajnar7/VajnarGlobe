package si.vajnartech.vajnarglobe.server;

import android.util.Log;

import java.io.BufferedReader;
import java.util.ArrayList;

import si.vajnartech.vajnarglobe.Area;
import si.vajnartech.vajnarglobe.C;
import si.vajnartech.vajnarglobe.GeoPoint;
import si.vajnartech.vajnarglobe.Place;

public class CmdGetAreas extends Controller<RObjAreas>
{
    protected Runnable runAfter;

    public CmdGetAreas(Runnable runAfter, String email)
    {
        super("get_areas", email);
        this.runAfter = runAfter;
    }

    @Override
    protected RObjAreas deserialize(BufferedReader br)
    {
        return gson.fromJson(br, RObjAreas.class);
    }

    @Override
    public RObjAreas backgroundFunc()
    {
        return callServer(null);
    }

    @Override
    protected void onFail()
    {
            Log.i("VajnarGlobe", "Error");
    }

    @Override
    protected void onPostExecute(RObjAreas response)
    {
        if (response == null)
        {
            onFail();
            return;
        }

        if (!response.areas.isEmpty()) {
            C.areas.clear();
            for (RObjArea a : response.areas) {
                if (a.points.size() < 3) continue;
                ArrayList<GeoPoint> points = new ArrayList<>();
                for (RObjPoint p : a.points)
                    points.add(new GeoPoint(Double.parseDouble(p.longitude), Double.parseDouble(p.latitude)));
                Area newArea = new Place(a.name, points);
                C.areas.put(a.name, newArea);
                newArea.constructArea();
            }
        }

        if (runAfter != null)
            runAfter.run();
    }
}
