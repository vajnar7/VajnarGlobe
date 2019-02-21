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

  @Override
  public RnDouble plus(RnDouble val)
  {
    RnDouble res = new RnDouble(n);
    for (int i = 0; i < n; n++)
      res.add(val.get(i) + get(i));
    return res;
  }

  @Override
  public void is(RnDouble val)
  {
    clear();
    for (int i = 0; i < n; n++)
      add(val.get(i));
  }

  @Override
  public RnDouble div(Double val)
  {
    RnDouble res = new RnDouble(n);
    for (int i = 0; i < n; n++)
      res.add(get(i) / val);
    return res;
  }

  @Override
  public RnDouble minus(RnDouble val)
  {
    RnDouble res = new RnDouble(n);
    for (int i = 0; i < n; n++)
      res.add(val.get(i) - get(i));
    return res;
  }


}
