package si.vajnartech.vajnarglobe;

import android.widget.Toast;

import java.util.ArrayList;
import static si.vajnartech.vajnarglobe.Login.GET_ALL;

public class GetAreas extends REST<AreaQ>
{
  private Runnable r = null;
  private Runnable onFail;

  private GetAreas(final MainActivity act)
  {
    super(GET_ALL);
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
    else
    {
      onFail.run();
    }
  }
}