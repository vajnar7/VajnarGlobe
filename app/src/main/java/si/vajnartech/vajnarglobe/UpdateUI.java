package si.vajnartech.vajnarglobe;

import android.location.Location;

interface UpdateUI
{
  void printLocation(Location loc);
  void setAreaName(Area name);

  void setMessage(String msg);
}
