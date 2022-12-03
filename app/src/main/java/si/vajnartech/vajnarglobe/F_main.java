package si.vajnartech.vajnarglobe;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;

public abstract class F_main extends MyFragment
{
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
  {
    LinearLayout res = createView(inflater, container);
    res.addView(new GeoMap(act));
    return res;
  }
}
