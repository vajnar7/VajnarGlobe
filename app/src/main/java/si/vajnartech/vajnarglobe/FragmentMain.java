package si.vajnartech.vajnarglobe;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class FragmentMain extends MyFragment implements View.OnClickListener
{
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
  {
    init();
    View res = inflater.inflate(R.layout.activity_main, container, false);
    res.findViewById(R.id.id_capture).setOnClickListener(this);
    res.findViewById(R.id.id_track).setOnClickListener(this);
    return res;
  }

  @Override
  public void onClick(View v)
  {
    switch (v.getId()) {
    case R.id.id_capture:
      act.setFragment("capture", F_Capture.class, new Bundle());
      break;
    case R.id.id_track:
      act.setFragment("track", F_Track.class, new Bundle());
      break;
    }
  }

  @Override
  protected void init()
  {
  }
}
