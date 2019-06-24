package si.vajnartech.vajnarglobe;

import android.annotation.SuppressLint;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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
    layout = inflater.inflate(R.layout.capture_layout, container, false);
    layout.findViewById(R.id.mark).setOnClickListener(this);
    layout.findViewById(R.id.b_clear).setOnClickListener(this);
    layout.findViewById(R.id.b_push).setOnClickListener(this);
    layout.findViewById(R.id.construct).setOnClickListener(this);
    layout.findViewById(R.id.test1).setOnClickListener(this);
    layout.findViewById(R.id.test1).setVisibility(GPS.GPS_SIMULATE ? View.VISIBLE : View.GONE);

    myView = new CaptureView(act, this);
    act.currentArea = new CurrentArea(act.tx(R.string.new_area));

    res.addView(layout);
    res.addView(myView);
    return res;
  }

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
    case R.id.mark:
      act.currentArea.add(myView.currentPoint);
      myView.invalidate();
      break;
    case R.id.b_push:
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
      break;
    case R.id.construct:
      if (act.currentArea != null) {
        act.currentArea.constructArea();
        myView.invalidate();
      }
      break;
    case R.id.test1:
      Location loc = new Location("");
      C.c ++;
      loc.setLongitude(C.fakeArea.get(C.c).lon);
      loc.setLatitude(C.fakeArea.get(C.c).lat);
      myView.onLocationChanged(loc);
    }
  }

  @Override
  public void printLocation(Location loc)
  {
    ((TextView) layout.findViewById(R.id.longitude)).setText(Double.toString(loc.getLongitude()));
    ((TextView) layout.findViewById(R.id.latitude)).setText(Double.toString(loc.getLatitude()));
  }
}
