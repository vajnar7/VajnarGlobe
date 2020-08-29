package si.vajnartech.vajnarglobe;

import android.support.v4.app.DialogFragment;

public abstract class MyFragment extends DialogFragment
{
  MainActivity act;
  TerminalWindow terminal = null;

  public static <T extends MyFragment> T instantiate(Class<T> cls, MainActivity act)
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

  abstract protected void init();
}
