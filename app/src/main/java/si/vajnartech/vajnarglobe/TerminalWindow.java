package si.vajnartech.vajnarglobe;

import android.view.View;
import android.widget.TextView;

final class TerminalWindow
{
  private final TextView tv;

  TerminalWindow(View w)
  {
    tv = w.findViewById(R.id.msg_window);
  }

  void setBackgroundColor(int color)
  {
    tv.setBackgroundColor(color);
  }

  void setText(String msg)
  {
    tv.setText(msg);
  }

  void show(boolean sh)
  {
    if (sh)
      tv.setVisibility(View.VISIBLE);
    else
      tv.setVisibility(View.GONE);
  }
}