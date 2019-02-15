package si.vajnartech.vajnarglobe;


class D extends Vector
{
  private double q_x;
  private double q_y;
  private long q_s;

  D()
  {
    q_x = 0;
    q_y = 0;
  }

  void _up(long scalar)
  {
    s = scalar - q_s;
    q_s = scalar;
  }

  void _up(Vector v)
  {
    x = v.x - q_x;
    y = v.y - q_y;
    q_x = v.x;
    q_y = v.y;
  }

  void _is(Vector v)
  {
    x = v.x;
    y = v.y;
  }

  void _is(int v)
  {
    s = v;
  }
}
