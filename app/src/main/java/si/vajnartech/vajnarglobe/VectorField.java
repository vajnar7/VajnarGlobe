package si.vajnartech.vajnarglobe;

import java.util.ArrayList;

import si.vajnartech.vajnarglobe.math.R2Double;

public abstract class VectorField extends ArrayList<R2Double>
{
  @Override
  public boolean add(R2Double v)
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

  private R2Double average()
  {
    R2Double a = new R2Double(0.0, 0.0);
    for (R2Double v: this)
      a.is(a.plus(v));
    a.is(a.div((double) C.Parameters.n));
    return (a);
  }

  abstract void done(R2Double point);
}
