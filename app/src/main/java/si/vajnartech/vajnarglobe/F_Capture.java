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
import si.vajnartech.vajnarglobe.rest.Areas;

import static si.vajnartech.vajnarglobe.C.areas;

public class F_Capture extends MyFragment implements View.OnClickListener
{
  CaptureView myView;

  private CurrentArea currentArea;

  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
  {
    LinearLayout res = createView(inflater, container);

    printMessage(act.tx(R.string.mode_capture));
    myView = new CaptureView(act, this);

    myView.setOnTouchListener(myView);
    res.addView(myView);
    currentArea = myView.currentArea;

    return res;
  }

  private void push()
  {
    EditText areaName = getAreaNameCointainer();
    String name = areaName.getText().toString();
    if (name.isEmpty())
    {
      areaName.setError(act.tx(R.string.error_wrong_name));
      return;
    }
    currentArea.setName(name);
    if (currentArea.constructArea()) {
      currentArea.push(act);
      Toast.makeText(act, "Sent", Toast.LENGTH_SHORT).show();
      return;
    }
    Toast.makeText(act, "Error constructing area", Toast.LENGTH_SHORT).show();
  }

  @Override
  public void onClick(@NonNull View v)
  {
    if (v.getId() == R.id.b_mark) {
      currentArea.mark(myView.currentPoint);
      myView.invalidate();
    } else if (v.getId() == R.id.b_construct) {
      if (currentArea != null) {
        push();
        myView.invalidate();
      }
    }

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
    layout.findViewById(R.id.b_clear).setVisibility(View.GONE);

    if (C.DEBUG_MODE) {
      layout.findViewById(R.id.test_buttons).setVisibility(View.VISIBLE);
      layout.findViewById(R.id.test_left).setOnClickListener(this);
      layout.findViewById(R.id.test_right).setOnClickListener(this);
      layout.findViewById(R.id.test_up).setOnClickListener(this);
      layout.findViewById(R.id.test_down).setOnClickListener(this);
    }
  }
}
