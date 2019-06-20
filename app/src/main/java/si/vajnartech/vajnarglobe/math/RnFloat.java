package si.vajnartech.vajnarglobe.math;

import java.util.ArrayList;

class RnFloat extends Rn<Float, RnFloat>
{
  private final int n;

  RnFloat(int n)
  {
    super(new ArrayList<Float>());
    this.n = n;
  }

  @Override
  public RnFloat mul(RnFloat val)
  {
    RnFloat res = new RnFloat(n);
    for (int i = 0; i < n; i++)
      res.add(val.get(i) * get(i));
    return res;
  }

  @Override RnFloat mulS(Float val)
  {
    return null;
  }

  @Override
  public RnFloat plus(RnFloat val)
  {
    RnFloat res = new RnFloat(n);
    for (int i = 0; i < n; i++)
      res.add(val.get(i) + get(i));
    return res;
  }

  @Override
  public void is(RnFloat val)
  {
    clear();
    for (int i = 0; i < n; i++)
      add(val.get(i));
  }

  @Override
  public RnFloat divS(Float val)
  {
    RnFloat res = new RnFloat(n);
    for (int i = 0; i < n; i++)
      res.add(get(i) / val);
    return res;
  }

  @Override
  public RnFloat minus(RnFloat val)
  {
    RnFloat res = new RnFloat(n);
    for (int i = 0; i < n; i++)
      res.add(val.get(i) - get(i));
    return res;
  }

  @Override RnFloat div(RnFloat val)
  {
    return null;
  }
}
