package si.vajnartech.calculus;

import java.util.ArrayList;

public abstract class
Derivative<K,V> extends Function<K, V>
{
  private Function<K, V> fun;

  protected Derivative(Function<K, V> fun)
  {
    this.fun = fun;
  }

  @Override
  protected ArrayList<K> getKeys()
  {
    return fun.getKeys();
  }

  @Override
  public V get(Object key)
  {
    return fun.get(key);
  }

  @Override
  public int size()
  {
    return fun.size();
  }

  @Override
  protected K getKeyAt(int i)
  {
    return fun.getKeyAt(i);
  }
}
