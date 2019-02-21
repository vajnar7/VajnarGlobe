package si.vajnartech.vajnarglobe;

import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import si.vajnartech.vajnarglobe.math.R2Double;

public class F_Capture extends MyFragment implements View.OnClickListener
{
  GPS      gps;
  TextView latitudeT;
  TextView longitudeT;

  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
  {
    View res = inflater.inflate(R.layout.capture_layout, container, false);
    res.findViewById(R.id.send).setOnClickListener(this);
    res.findViewById(R.id.construct).setOnClickListener(this);
    latitudeT = res.findViewById(R.id.latitude);
    longitudeT = res.findViewById(R.id.longitude);

    init();
    return res;
  }

  @Override
  protected void init()
  {
    gps = new GPS(act)
    {
      @Override protected void notifyMe(R2Double point)
      {
      }

      @Override
      protected void notifyMe(Location loc)
      {
        _printLocation();
      }
    };

    act.currentArea = new CurrentArea("Test1");
  }

  private void _printLocation()
  {
    String lonS = "Longitude:";
    String latS = "Latitude:";
    latitudeT.setText(latS);
    longitudeT.setText(lonS);
    latS += gps.location.getLatitude();
    lonS += gps.location.getLongitude();
    latitudeT.setText(latS);
    longitudeT.setText(lonS);
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
    }
  }
}
