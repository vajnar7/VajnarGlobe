package si.vajnartech.vajnarglobe.math;

import java.util.ArrayList;

class RnDouble extends Rn<Double, RnDouble>
{
  private int n;

  RnDouble(int n)
  {
    super(new ArrayList<Double>());
    this.n = n;
  }

  @Override RnDouble mul(RnDouble val)
  {
    RnDouble res = new RnDouble(n);
    for (int i = 0; i < n; n++)
      res.add(val.get(i) * get(i));
    return res;
  }

  @Override RnDouble plus(RnDouble val)
  {
    RnDouble res = new RnDouble(n);
    for (int i = 0; i < n; n++)
      res.add(val.get(i) + get(i));
    return res;
  }

  @Override void eq(RnDouble val)
  {
    clear();
    for (int i = 0; i < n; n++)
      add(val.get(i));
  }

  @Override RnDouble div(Double val)
  {
    RnDouble res = new RnDouble(n);
    for (int i = 0; i < n; n++)
      res.add(get(i) / val);
    return res;
  }
}
