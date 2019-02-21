package si.vajnartech.vajnarglobe;

import si.vajnartech.vajnarglobe.math.R2Double;

@SuppressWarnings("SuspiciousNameCombination")
class D extends R2Double
{
  private double q_x, q_y;

  D()
  {
    super(0.0, 0.0);
    q_x = 0;
    q_y = 0;
  }

  void up(R2Double v)
  {
    q_x = v.get(0);
    q_y = v.get(1);
    is(new R2Double(v.get(0) - q_x, v.get(1) - q_y));
  }
}
