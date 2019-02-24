package si.vajnartech.vajnarglobe;

import si.vajnartech.vajnarglobe.math.R2Double;

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
    R2Double res = new R2Double(v.x1() - q_x, v.x2() - q_y);
    q_x = v.x1();
    q_y = v.x2();
    is(res);
  }
}
