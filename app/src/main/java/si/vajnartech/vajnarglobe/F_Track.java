package si.vajnartech.vajnarglobe;

import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.Map;

import androidx.annotation.NonNull;
import si.vajnartech.vajnarglobe.rest.Areas;

import static si.vajnartech.vajnarglobe.C.areas;

public class F_Track extends MyFragment implements View.OnClickListener
{
  public TrackView myView;

  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
  {
    LinearLayout res = createView(inflater, container);

    printMessage(act.tx(R.string.mode_tracking));
    myView = new TrackView(act, this);
    myView.setOnTouchListener(myView);
    res.addView(myView);

    return res;
  }

//  void calibrate(boolean val)
//  {
//    myView.isCalibrated = val;
//  }

//  boolean isCalibrated()
//  {
//    return myView.isCalibrated;
//  }
//
//  void reset()
//  {
//    myView.aproximator.end();
//  }
//
//  void startAproximator()
//  {
//    myView.start();
//  }

  @Override
  protected void init(View layout)
  {
    layout.findViewById(R.id.b_mark).setVisibility(View.GONE);
    layout.findViewById(R.id.b_construct).setVisibility(View.GONE);
    layout.findViewById(R.id.b_clear).setVisibility(View.GONE);

    if (C.DEBUG_MODE) {
      layout.findViewById(R.id.test_buttons).setVisibility(View.VISIBLE);
      layout.findViewById(R.id.test_left).setOnClickListener(this);
      layout.findViewById(R.id.test_right).setOnClickListener(this);
      layout.findViewById(R.id.test_up).setOnClickListener(this);
      layout.findViewById(R.id.test_down).setOnClickListener(this);
    }
  }

  @Override
  public void onClick(View v)
  {
    if (C.DEBUG_MODE) {
      if (v.getId() == R.id.test_left) {
        myView.mvLeft();
      } else if (v.getId() == R.id.test_right) {
        myView.mvRight();
      } else if (v.getId() == R.id.test_up) {
        myView.mvUp();
      } else if (v.getId() == R.id.test_down) {
        myView.mvDown();
      }
    }
  }
}