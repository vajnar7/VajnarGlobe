package si.vajnartech.vajnarglobe;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class F_Track extends MyFragment implements View.OnClickListener, View.OnTouchListener
{
  WhereAmI gps;
  float    dX, dY;
  D dK = new D();
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
  {
    init();

    View         res = inflater.inflate(R.layout.track_base, container, false);
    LinearLayout l1  = res.findViewById(R.id.kkk0_top);
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
    switch (event.getAction()) {
    case MotionEvent.ACTION_DOWN:
      dX = view.getX() - event.getRawX();
      dY = view.getY() - event.getRawY();
      dK._up(new Vector(event.getRawX(), event.getRawY()));
      view.performClick();
      break;
    case MotionEvent.ACTION_MOVE:
//      TODO:
//      view.animate()
//          .x(event.getRawX() + dX)
//          .y(event.getRawY() + dY)
//          .setDuration(0)
//          .start();
      break;
    case MotionEvent.ACTION_UP:
      Vector v = new Vector(event.getRawX(), event.getRawY());
      dK._up(v);
      dK.y = -dK.y;
      C.O._plus_je(dK);
      view.invalidate();
    default:
      return false;
    }
    return true;
  }
}