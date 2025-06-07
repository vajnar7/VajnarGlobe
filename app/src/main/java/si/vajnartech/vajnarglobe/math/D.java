package si.vajnartech.vajnarglobe.math;

public class D extends NumDouble2
{
  private double q_x, q_y;

  public D()
  {
    super(0.0, 0.0);
    q_x = 0;
    q_y = 0;
  }

  public void up(NumDouble2 v)
  {
    NumDouble2 res = new NumDouble2(v.get(0) - q_x, v.get(1) - q_y);
    q_x = v.get(0);
    q_y = v.get(1);
    is(res);
  }

  public boolean isZero() {
    return get(0) == 0.0 && get(1) == 0.0;
  }
}
