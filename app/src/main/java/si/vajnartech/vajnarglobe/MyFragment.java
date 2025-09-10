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

public abstract class MyFragment<V extends View> extends DialogFragment implements UpdateUI
{
  V myView;

  protected static int MARK_BUTTON = R.id.b_mark;
  protected static int CONSTRUCT_BUTTON = R.id.b_construct;
  protected static int NEW_AREA_BUTTON = R.id.b_new;
  protected static int DELETE_AREA_BUTTON = R.id.b_delete;
  protected static int CANCEL_BUTTON = R.id.b_cancel;

  MainActivity   act;
  TerminalWindow terminal = null;

  protected View layout;

  public LinearLayout createView(@NonNull LayoutInflater inflater, ViewGroup container)
  {
    LinearLayout res = new LinearLayout(act);
    res.setOrientation(LinearLayout.VERTICAL);

    layout = inflater.inflate(R.layout.bidr, container, false);
    res.addView(layout);
    init();
    return res;
  }

  public static <T extends MyFragment<?>> T instantiate(@NonNull Class<T> cls, MainActivity act)
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

  @Override
  public void setMessage(String msg)
  {
    printMessage(msg);
  }

  protected abstract void init();

  @Override
  public void buttonShow(int id, boolean visibility)
  {
    layout.findViewById(id).setVisibility(visibility ? View.VISIBLE : View.GONE);
  }
}
