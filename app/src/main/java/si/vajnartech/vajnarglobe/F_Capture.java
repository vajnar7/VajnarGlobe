package si.vajnartech.vajnarglobe;

import android.annotation.SuppressLint;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.Map;

import androidx.annotation.NonNull;

import static si.vajnartech.vajnarglobe.C.areas;

public class F_Capture extends MyFragment implements View.OnClickListener
{
  CaptureView myView;

  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
  {
    LinearLayout res = createView(inflater, container);

    printMessage(act.tx(R.string.mode_capture));
    myView = new CaptureView(act, this);

    myView.setOnTouchListener(myView);
    res.addView(myView);

    _defaultLocation();
    return res;
  }

  public void push()
  {
    EditText areaName = getAreaNameCointainer();
    if (areaName.getText().toString().equals(act.tx(R.string.new_area)) ||
        C.areas.get(areaName.getText().toString()) != null) {
      areaName.setError(act.tx(R.string.error_wrong_name));
      return;
    }
    if (areaName.isEnabled()) {
      currentArea.setName(areaName.getText().toString());
      areaName.setEnabled(false);
    }
    Toast.makeText(act, "Sent", Toast.LENGTH_SHORT).show();
    myView.invalidate();
  }

  // TODO urejanje obmocij da izbrises obmocje/select ...
  @Override
  public void onClick(@NonNull View v)
  {
    if (v.getId() ==  R.id.b_clear) {
      new DeleteArea(currentArea.areaName, act);
      currentArea.geoPoints.clear();
      myView.invalidate();
      getAreaNameCointainer().setEnabled(true);
    } else if (v.getId() == R.id.b_mark) {
      currentArea.mark(myView.currentPoint);
      myView.invalidate();
    } else if (v.getId() == R.id.b_construct) {
      if (currentArea != null) {
        currentArea.constructArea();
        myView.invalidate();
      }
    } else if (v.getId() == R.id.test_left) {
      myView.mvLeft();
    } else if (v.getId() == R.id.test_right) {
      myView.mvRight();
    } else if (v.getId() == R.id.test_up) {
      myView.mvUp();
    } else if (v.getId() == R.id.test_down) {
      myView.mvDown();
    }
  }

  @Override
  @SuppressLint("SetTextI18n")
  public void printLocation(Location loc)
  {
    getLongitudeContainer().setText(Double.toString(loc.getLongitude()));
    getLatitudeContainer().setText(Double.toString(loc.getLatitude()));
  }

  @Override
  protected void init(View layout)
  {
    layout.findViewById(R.id.b_mark).setOnClickListener(this);
    layout.findViewById(R.id.b_construct).setOnClickListener(this);

    if (C.DEBUG_MODE) {
      layout.findViewById(R.id.test_buttons).setVisibility(View.VISIBLE);
      layout.findViewById(R.id.test_left).setOnClickListener(this);
      layout.findViewById(R.id.test_right).setOnClickListener(this);
      layout.findViewById(R.id.test_up).setOnClickListener(this);
      layout.findViewById(R.id.test_down).setOnClickListener(this);
    }
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
}
