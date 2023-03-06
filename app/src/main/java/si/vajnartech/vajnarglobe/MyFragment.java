package si.vajnartech.vajnarglobe;

import android.annotation.SuppressLint;
import android.location.Location;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

public abstract class MyFragment extends DialogFragment implements UpdateUI
{
  MainActivity   act;
  TerminalWindow terminal = null;

  private View layout;

  public LinearLayout createView(@NonNull LayoutInflater inflater, ViewGroup container)
  {
    LinearLayout res = new LinearLayout(act);
    res.setOrientation(LinearLayout.VERTICAL);

    layout = inflater.inflate(R.layout.bidr, container, false);
    res.addView(layout);
    init(layout);
    return res;
  }

  public static <T extends MyFragment> T instantiate(@NonNull Class<T> cls, MainActivity act)
  {
    T res = null;
    try {
      res = cls.newInstance();
      res.act = act;
    } catch (java.lang.InstantiationException | IllegalAccessException e) {
      e.printStackTrace();
    }
    return res;
  }

  protected EditText getAreaNameCointainer()
  {
    return layout.findViewById(R.id.ed_area_name);
  }

  protected TextView getLatitudeContainer()
  {
    return layout.findViewById(R.id.latitude);
  }

  protected TextView getLongitudeContainer()
  {
    return layout.findViewById(R.id.longitude);
  }

  protected void printMessage(String msg)
  {
    if (terminal == null)
      terminal = new TerminalWindow(layout);
    terminal.setText(msg);
  }

  @Override
  @SuppressLint("SetTextI18n")
  public void printLocation(@NonNull Location loc)
  {
    ((TextView) layout.findViewById(R.id.longitude)).setText(Double.toString(loc.getLongitude()));
    ((TextView) layout.findViewById(R.id.latitude)).setText(Double.toString(loc.getLatitude()));
  }

  @Override
  public void setAreaName(@NonNull Area area)
  {
    ((TextView) layout.findViewById(R.id.ed_area_name)).setText(area.areaName);
  }

  protected abstract void init(View layout);

  protected void markButton(boolean visibility) {
    layout.findViewById(R.id.b_mark).setVisibility(visibility ? View.VISIBLE : View.GONE);
  }

}
