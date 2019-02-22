package si.vajnartech.vajnarglobe.math;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

interface FunctionBase<K, V>
{
  V f(K x);
  V sum(K x0, K x1);
}

@SuppressWarnings("NullableProblems")
public abstract class Function<K, V> extends HashMap<K, V> implements FunctionBase<K, V>
{
  /*
    A = {x1->y1, x2-y2, x3->y3, x4->y4}, f(xi) = yi: xi e A, f(xi) = NaN: f(xi) !e A
   */
  // for discrete function we must have array of indeces
  private ArrayList<K>   keys = new ArrayList<>();

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

  @Override
  public V put(K key, V value)
  {
    keys.add(key);
    return super.put(key, value);
  }

  protected ArrayList<K> getKeys()
  {
    return keys;
  }
}
