package si.vajnartech.vajnarglobe;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

public class F_main extends MyFragment
{
  View layout;
  Map myView;

  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
  {

    LinearLayout res = new LinearLayout(act);
    res.setOrientation(LinearLayout.VERTICAL);
    layout = inflater.inflate(R.layout.main_layout, container, false);
    myView = new Map(act);
    act.currentArea = new CurrentArea(act.tx(R.string.new_area));

    res.addView(layout);
    res.addView(myView);
    return res;
  }
}
