package si.vajnartech.vajnarglobe;

import android.annotation.SuppressLint;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
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

import si.vajnartech.vajnarglobe.math.R2Double;

import static si.vajnartech.vajnarglobe.C.xOffset;
import static si.vajnartech.vajnarglobe.C.yOffset;

public class F_Capture extends MyFragment implements View.OnClickListener
{
  GPS      gps;
  View layout;

  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
  {
    LinearLayout res = new LinearLayout(act);
    res.setOrientation(LinearLayout.VERTICAL);
    layout = inflater.inflate(R.layout.capture_layout, container, false);
    layout.findViewById(R.id.send).setOnClickListener(this);
    layout.findViewById(R.id.construct).setOnClickListener(this);
    layout.findViewById(R.id.test1).setOnClickListener(this);
    layout.findViewById(R.id.test1).setVisibility(GPS.GPS_SIMULATE ? View.VISIBLE : View.GONE);
    init();

    res.addView(layout);
    res.addView(gps);
    return res;
  }

  @Override
  protected void init()
  {
    gps = new GPS(act)
    {
      Paint paint = new Paint();

      @Override
      protected void notifyMe(R2Double point)
      {
      }

      @Override
      protected void notifyMe(Location loc)
      {
        Location currentLocation = new Location(loc);
        xOffset = new Normal(currentLocation.getLongitude(), 3).value();
        yOffset = new Normal(currentLocation.getLatitude(), 2).value();
        _printLocation(loc);
      }

      @Override
      protected void onDraw(Canvas canvas)
      {
        if (act.currentArea.isConstructed())
          act.currentArea.draw(canvas, paint, Color.BLACK);
      }
    };

    act.currentArea = new CurrentArea(act.tx(R.string.new_area));
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
      act.currentArea.mark(new GeoPoint(gps.location.getLongitude(), gps.location.getLatitude()));
      Toast.makeText(act, "Sent", Toast.LENGTH_SHORT).show();
      break;
    case R.id.construct:
      if (act.currentArea != null) {
        act.currentArea.constructArea();
        gps.invalidate();
      }
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
