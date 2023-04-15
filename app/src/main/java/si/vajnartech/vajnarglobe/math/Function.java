package si.vajnartech.vajnarglobe.math;

import java.util.ArrayList;
import java.util.HashMap;

abstract class Function<In, Out> extends HashMap<In, Out>
{
  protected ArrayList<In> keys = new ArrayList<>();

  public Out f(In val)
  {
    return function(val);
  }

  public Out f(int val)
  {
    return get(keys.get(val));
  }

  @Override
  public Out put(In key, Out value)
  {
    keys.add(key);
    return super.put(key, value);
  }

  abstract protected Out function(In val);



}
