package si.vajnartech.vajnarglobe;

import android.annotation.SuppressLint;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

@SuppressLint("SetTextI18n")
public class F_Track extends MyFragment implements View.OnClickListener, TrackViewInterface
{
  TrackView myView;
  View layout;

  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
  {
    LinearLayout res = new LinearLayout(act);
    res.setOrientation(LinearLayout.VERTICAL);
    layout = inflater.inflate(R.layout.bidr, container, false);
    init();

    setAreaName("");
    printMessage(act.tx(R.string.mode_tracking));
    myView = new TrackView(act, this);
    myView.setOnTouchListener(myView);
    res.addView(layout);
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
    myView.A.end();
  }

  void startAproximator()
  {
    myView.start();
  }

  @Override
  public void onClick(View v)
  {
  }

  @Override
  public void printLocation(Location loc)
  {
    ((TextView) layout.findViewById(R.id.longitude)).setText(Double.toString(loc.getLongitude()));
    ((TextView) layout.findViewById(R.id.latitude)).setText(Double.toString(loc.getLatitude()));
  }

  @Override
  public void setAreaName(String name)
  {
    layout.findViewById(R.id.ed_area_name).setEnabled(false);
    ((TextView) layout.findViewById(R.id.ed_area_name)).setText(name);
  }

  @Override
  public void printMessage(String msg)
  {
    if (terminal == null)
      terminal = new TerminalWindow(layout);
    terminal.setText(msg);
  }

  @Override
  protected void init()
  {
    layout.findViewById(R.id.b_mark).setVisibility(View.GONE);
    layout.findViewById(R.id.b_construct).setVisibility(View.GONE);
    layout.findViewById(R.id.b_test).setVisibility(View.GONE);
  }
}