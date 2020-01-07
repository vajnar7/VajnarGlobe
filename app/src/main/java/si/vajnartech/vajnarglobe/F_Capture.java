package si.vajnartech.vajnarglobe;

import android.annotation.SuppressLint;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Map;

import static si.vajnartech.vajnarglobe.C.GPS_SIMULATE;
import static si.vajnartech.vajnarglobe.C.areas;

@SuppressLint("SetTextI18n")
public class F_Capture extends MyFragment implements View.OnClickListener, CaptureViewInterface
{
  CaptureView myView;
  View layout;

  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
  {
    LinearLayout res = new LinearLayout(act);
    res.setOrientation(LinearLayout.VERTICAL);
    layout = inflater.inflate(R.layout.bidr, container, false);
    init();
    printMessage(act.tx(R.string.mode_capture));
    myView = new CaptureView(act, this);
    act.currentArea = new CurrentArea(act.tx(R.string.new_area));

    res.addView(layout);
    myView.setOnTouchListener(myView);
    res.addView(myView);

    if (GPS_SIMULATE) _fakeLocation();
    else _defaultLocation();
    return res;
  }

  public void push()
  {
    EditText et = layout.findViewById(R.id.ed_area_name);
    if (et.getText().toString().equals(act.tx(R.string.new_area)) ||
        C.areas.get(et.getText().toString()) != null) {
      et.setError(act.tx(R.string.error_wrong_name));
      return;
    }
    if (et.isEnabled()) {
      act.currentArea.setName(et.getText().toString());
      et.setEnabled(false);
    }
    act.currentArea.mark(myView.currentPoint);
    Toast.makeText(act, "Sent", Toast.LENGTH_SHORT).show();
    myView.invalidate();
  }

  // TODO urejanje obmocij da izbrises obmocje/select ...
  @Override
  public void onClick(View v)
  {
    switch (v.getId()) {
    case R.id.b_clear:
      new DeleteArea(act.currentArea.areaName);
      act.currentArea.currentPoints.clear();
      act.currentArea.geoPoints.clear();
      myView.invalidate();
      layout.findViewById(R.id.ed_area_name).setEnabled(true);
      break;
    case R.id.b_mark:
      act.currentArea.add(myView.currentPoint);
      myView.invalidate();
      break;
    case R.id.b_construct:
      if (act.currentArea != null) {
        act.currentArea.constructArea();
        myView.invalidate();
      }
      break;
    case R.id.b_test:
      _fakeLocation();
      break;
    }
  }

  @Override
  public void printLocation(Location loc)
  {
    ((TextView) layout.findViewById(R.id.longitude)).setText(Double.toString(loc.getLongitude()));
    ((TextView) layout.findViewById(R.id.latitude)).setText(Double.toString(loc.getLatitude()));
  }

  @Override
  protected void init()
  {
    layout.findViewById(R.id.b_mark).setOnClickListener(this);
    layout.findViewById(R.id.b_construct).setOnClickListener(this);
    layout.findViewById(R.id.b_test).setOnClickListener(this);
    layout.findViewById(R.id.b_test).setVisibility(GPS_SIMULATE ? View.VISIBLE : View.GONE);
  }

  @Override
  public void printMessage(String msg)
  {
    if (terminal == null)
      terminal = new TerminalWindow(layout);
    terminal.setText(msg);
  }

  private void _defaultLocation()
  {
    Location loc = new Location("");
    if (areas.size() > 0) {
      Map.Entry<String, Area> entry = C.areas.entrySet().iterator().next();
      Area a = entry.getValue();
      C.DEF_LONGITUDE = a.geoPoints.get(1).lon;
      C.DEF_LATITUDE = a.geoPoints.get(1).lat;
    }
    loc.setLongitude(C.DEF_LONGITUDE);
    loc.setLatitude(C.DEF_LATITUDE);
    myView.onLocationChanged(loc);
  }

  private void _fakeLocation()
  {
    Location loc = new Location("");
    C.c ++;
    loc.setLongitude(C.fakeArea.get(C.c).lon);
    loc.setLatitude(C.fakeArea.get(C.c).lat);
    myView.onLocationChanged(loc);
  }
}
