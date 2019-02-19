package si.vajnartech.vajnarglobe.math;

import java.util.HashMap;

interface FunctionBase<K, V>
{
  V f(K x);
  V integral();
}

@SuppressWarnings("NullableProblems")
public abstract class Function<K, V> extends HashMap<K, V> implements FunctionBase<K, V>
{
  /*
    A = {x1->y1, x2-y2, x3->y3, x4->y4}, f(xi) = yi: xi e A, f(xi) = NaN: f(xi) !e A
   */

  private String exp = null;

  public Function()
  {}

  protected void set(String val)
  {
    exp = val;
    try {
      int i = exp.indexOf('x');
    } catch (Exception ignored) {
    }
  }

  @Override
  public String toString()
  {
    if (exp != null)
      return ("f(x) = " + exp);
    return ("Discrete");
  }
}
