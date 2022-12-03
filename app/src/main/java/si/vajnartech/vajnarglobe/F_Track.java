package si.vajnartech.vajnarglobe;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;

public class F_Track extends MyFragment
{
  public TrackView myView;

  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
  {
    LinearLayout res = createView(inflater, container);

    setAreaName("");
    printMessage(act.tx(R.string.mode_tracking));
    myView = new TrackView(act, this);
    myView.setOnTouchListener(myView);
    res.addView(myView);
    return res;
  }

  void calibrate(boolean val)
  {
    myView.isCalibrated = val;
  }

  boolean isCalibrated()
  {
    return myView.isCalibrated;
  }

  void reset()
  {
    myView.aproximator.end();
  }

  void startAproximator()
  {
    myView.start();
  }

  @Override
  protected void init(View layout)
  {
    layout.findViewById(R.id.b_mark).setVisibility(View.GONE);
    layout.findViewById(R.id.b_construct).setVisibility(View.GONE);
  }
}