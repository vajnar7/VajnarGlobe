package si.vajnartech.vajnarglobe.math;

import android.annotation.SuppressLint;

@SuppressWarnings("WeakerAccess")
public class LinearFunction extends Function<Float, Float>
{
  public boolean isHorizontal = false;
  public boolean isVertical   = false;
  public boolean isInvalid    = false;
  public float k, n;

  @SuppressLint("DefaultLocale")
  public LinearFunction(RnFloat p1, RnFloat p2)
  {
    float a = p2.get(1) - p1.get(1); // if 0 horizontal
    float b = p2.get(0) - p1.get(0); // if 0 vertical

    if (a == 0 && b != 0) {
      isHorizontal = true;
    } else if (a != 0 && b == 0) {
      isVertical = true;
    } else if (a == 0)
      isInvalid = true;
    else {
      k = a / b;
      n = p1.get(1) - (k * p1.get(0));
      set(String.format("%fx + %f", k, n));
    }
  }
}
