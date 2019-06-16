package si.vajnartech.vajnarglobe;

import android.annotation.SuppressLint;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import si.vajnartech.vajnarglobe.math.R2Double;

public class F_Capture extends MyFragment implements View.OnClickListener
{
  GPS      gps;
  View layout;

  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
  {
    layout = inflater.inflate(R.layout.capture_layout, container, false);
    layout.findViewById(R.id.send).setOnClickListener(this);
    layout.findViewById(R.id.construct).setOnClickListener(this);
    layout.findViewById(R.id.test1).setOnClickListener(this);
    layout.findViewById(R.id.test1).setVisibility(GPS.GPS_SIMULATE ? View.VISIBLE : View.GONE);
    init();
    return layout;
  }

  @Override
  protected void init()
  {
    gps = new GPS(act)
    {
      @Override
      protected void notifyMe(R2Double point)
      {
      }

      @Override
      protected void notifyMe(Location loc)
      {
        _printLocation(loc);
      }
    };

    // Tu se vzame ime iz settings
    act.currentArea = new CurrentArea("Test1");
  }

  @SuppressLint("SetTextI18n")
  private void _printLocation(Location loc)
  {
    ((TextView) layout.findViewById(R.id.longitude)).setText(Double.toString(loc.getLongitude()));
    ((TextView) layout.findViewById(R.id.latitude)).setText(Double.toString(loc.getLatitude()));
  }

  @Override
  public void onClick(View v)
  {
    switch (v.getId()) {
    case R.id.send:
      act.currentArea.mark(new GeoPoint(gps.location.getLongitude(), gps.location.getLatitude()));
      Toast.makeText(act, "Sent", Toast.LENGTH_SHORT).show();
      break;
    case R.id.construct:
      if (act.currentArea != null)
        act.currentArea.constructArea();
      break;
    case R.id.test1:
      Location loc = new Location("");
      C.c ++;
      loc.setLongitude(C.fakeArea.get(C.c).lon);
      loc.setLatitude(C.fakeArea.get(C.c).lat);
      gps.onLocationChanged(loc);
    }
  }
}
