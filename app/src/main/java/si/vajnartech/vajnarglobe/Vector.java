package si.vajnartech.vajnarglobe;


public class Vector
{
  protected double x;
  protected double y;

  long s;

  Vector()
  {
    this.x = 0;
    this.y = 0;
    this.s = 0;
  }

  Vector(Vector b)
  {
    this.x = b.x;
    this.y = b.y;
  }

  Vector(double x, double y)
  {
    this.x = x;
    this.y = y;
  }

  Point toPoint()
  {
    return new Point((float)x, (float)y);
  }

  Vector _minus(Vector v)
  {
    return new Vector(x - v.x, y - v.y);
  }

  void _plus_je(Vector v)
  {
    x += v.x;
    y += v.y;
  }

  void _deljeno_je(Vector v)
  {
    x /= v.x;
    y /= v.y;
  }

  Vector _po(D dt)
  {
    return new Vector(x/dt.s, y/dt.s);
  }

  @Override
  public String toString()
  {
    return("[" + x + "," + y + "]");
  }
}
