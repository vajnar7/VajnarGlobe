package si.vajnartech.vajnarglobe;

import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import static si.vajnartech.vajnarglobe.C.GPS_SIMULATE;

public class F_Track extends MyFragment implements View.OnClickListener, TrackViewInterface
{
  TrackView myView;
  View layout;

  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
  {
    LinearLayout res = new LinearLayout(act);
    res.setOrientation(LinearLayout.VERTICAL);
    layout = inflater.inflate(R.layout.track_layout, container, false);
    layout.findViewById(R.id.bt_test12).setOnClickListener(this);
    layout.findViewById(R.id.bt_test12).setVisibility(GPS_SIMULATE ? View.VISIBLE : View.GONE); // vajnar. poglej tule
    myView = new TrackView(act, this);
    myView.setOnTouchListener(myView);
    res.addView(layout);
    res.addView(myView);
    return res;
  }

  @Override
  public void onClick(View v)
  {
    if (v.getId() == R.id.bt_test12) {
      Location loc = new Location("");
      loc.setLongitude(C.fakeArea.get(0).lon);
      loc.setLatitude(C.fakeArea.get(0).lat);
      myView.onLocationChanged(loc);
      C.startTestGPSService(act);
    }
  }

  @Override
  public void printLocation(Location loc)
  {
  }
}