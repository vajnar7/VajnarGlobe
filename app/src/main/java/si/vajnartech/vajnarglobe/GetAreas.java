package si.vajnartech.vajnarglobe;

import java.util.ArrayList;
import static si.vajnartech.vajnarglobe.Login.GET_ALL;

public class GetAreas extends REST<AreaQ>
{
  private Runnable r;
  GetAreas()
  {
    super(GET_ALL);
  }

  GetAreas(Runnable run)
  {
    super(GET_ALL);
    r = run;
  }

  @Override
  public AreaQ backgroundFunc()
  {
    return callServer(null, REST.OUTPUT_TYPE_JSON);
  }

  @Override
  protected void onPostExecute(AreaQ j)
  {
    super.onPostExecute(j);
    if (j != null) {
      C.areas.clear();
      for (AreaP a : j.areas) {
        C.areas.put(a.name, new Place(a.name, (ArrayList<GeoPoint>) a.points).constructArea());
      }
      if (r != null )
        r.run();
    }
  }
}