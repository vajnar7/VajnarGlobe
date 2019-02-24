package si.vajnartech.vajnarglobe;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import si.vajnartech.vajnarglobe.math.R2Double;

public class F_Track extends MyFragment implements View.OnClickListener, View.OnTouchListener
{
  WhereAmI gps;
  float dX, dY;
  D dK = new D();

  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
  {
    init();
    View res = inflater.inflate(R.layout.track_base, container, false);

    LinearLayout l1 = res.findViewById(R.id.kkk0_top);
    gps.setOnTouchListener(this);
    l1.addView(gps);
    res.findViewById(R.id.b_settings).setOnClickListener(this);
    res.findViewById(R.id.b_exit).setOnClickListener(this);
    return res;
  }

  @Override
  protected void init()
  {
    gps = new WhereAmI(act);
    C.startTestGPSService(act);
  }

  @Override
  public void onClick(View v)
  {
    switch (v.getId()) {
    case R.id.b_exit:
      act.finish();
    }
  }

  @Override
  public boolean onTouch(View view, MotionEvent event)
  {
    double rx, ry;

    switch (event.getAction()) {
    case MotionEvent.ACTION_DOWN:
      dX = view.getX() - event.getRawX();
      dY = view.getY() - event.getRawY();
      rx = event.getRawX();
      ry = event.getRawY();
      dK.up(new R2Double(rx, ry));
      view.performClick();
      break;
    case MotionEvent.ACTION_UP:
      rx = event.getRawX();
      ry = event.getRawY();
      dK.up(new R2Double(rx, ry));
      dK.is(dK.mul(new R2Double(1.0, -1.0))); // negate y part of point
      C.O.is(C.O.plus(dK));
      view.invalidate();
    default:
      return false;
    }
    return true;
  }
}