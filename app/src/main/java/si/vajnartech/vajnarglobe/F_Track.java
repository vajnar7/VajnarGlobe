package si.vajnartech.vajnarglobe;

import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

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
    myView = new TrackView(act, this);
    myView.setOnTouchListener(myView);
    res.addView(layout);
    res.addView(myView);
    return res;
  }

  @Override
  public void onClick(View v)
  {
  }

  @Override
  public void printLocation(Location loc)
  {
  }
}