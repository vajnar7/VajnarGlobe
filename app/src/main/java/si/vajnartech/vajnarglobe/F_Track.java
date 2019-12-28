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
    layout.findViewById(R.id.bt_test12).setVisibility(GPS_SIMULATE ? View.VISIBLE : View.GONE);
    myView = new TrackView(act, this);
    myView.setOnTouchListener(myView);
    res.addView(layout);
    res.addView(myView);
    return res;
  }

  @Override
  public void onClick(View v)
  {
    if (v.getId() == R.id.bt_test12)
      C.startTestGPSService(act, 13.82526551327198, 46.48596391150142);
  }

  @Override
  public void printLocation(Location loc)
  {
  }
}