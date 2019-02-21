package si.vajnartech.vajnarglobe.math;

import java.util.ArrayList;

class RnFloat extends Rn<Float, RnFloat>
{
  private int n;

  RnFloat(int n)
  {
    super(new ArrayList<Float>());
    this.n = n;
  }

  @Override
  public RnFloat mul(RnFloat val)
  {
    RnFloat res = new RnFloat(n);
    for (int i = 0; i < n; n++)
      res.add(val.get(i) * get(i));
    return res;
  }

  @Override
  public RnFloat plus(RnFloat val)
  {
    RnFloat res = new RnFloat(n);
    for (int i = 0; i < n; n++)
      res.add(val.get(i) + get(i));
    return res;
  }

  @Override
  public void is(RnFloat val)
  {
    clear();
    for (int i = 0; i < n; n++)
      add(val.get(i));
  }

  @Override
  public RnFloat div(Float val)
  {
    RnFloat res = new RnFloat(n);
    for (int i = 0; i < n; n++)
      res.add(get(i) / val);
    return res;
  }

  @Override
  public RnFloat minus(RnFloat val)
  {
    RnFloat res = new RnFloat(n);
    for (int i = 0; i < n; n++)
      res.add(val.get(i) - get(i));
    return res;
  }
}
