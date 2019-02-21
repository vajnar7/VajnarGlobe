package si.vajnartech.vajnarglobe.math;

import java.util.ArrayList;

abstract class Rn<V, O> extends ArrayList<V>
{
  Rn(ArrayList<V> set)
  {
    this.addAll(set);
  }

  abstract O mul(O val);

  abstract O plus(O val);

  abstract void is(O val);

  abstract O div(V val);

  abstract O minus(O val);
}