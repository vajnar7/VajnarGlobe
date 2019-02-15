package si.vajnartech.vajnarglobe;


import java.util.ArrayList;

public abstract class VectorField extends ArrayList<Vector>
{
  @Override
  public boolean add(Vector v)
  {
    if (C.Parameters.lim.getAndDecrement() > 0)
      return true;
    super.add(v);
    if (size() == C.Parameters.n) {
      done(average());
      clear();
    }
    return true;
  }

  private Vector average()
  {
    Vector a = new Vector();
    for (Vector v: this)
      a._plus_je(v);
    a._deljeno_je(new Vector(C.Parameters.n, C.Parameters.n));
    return (a);
  }

  abstract void done(Vector point);
}
