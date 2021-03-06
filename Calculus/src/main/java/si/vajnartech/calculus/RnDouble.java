package si.vajnartech.calculus;

import java.util.ArrayList;

public class RnDouble extends Rn<Double, RnDouble>
{
  private final int n;

  RnDouble(int n)
  {
    super(new ArrayList<Double>());
    this.n = n;
  }

  @Override
  public RnDouble mul(RnDouble val)
  {
    RnDouble res = new RnDouble(n);
    for (int i = 0; i < n; i++)
      res.add(val.get(i) * get(i));
    return res;
  }

  @Override
  public RnDouble mulS(Double val)
  {
    RnDouble res = new RnDouble(n);
    for (int i = 0; i < n; i++)
      res.add(get(i) * val);
    return res;
  }

  @Override
  public RnDouble plus(RnDouble val)
  {
    RnDouble res = new RnDouble(n);
    for (int i = 0; i < n; i++)
      res.add(val.get(i) + get(i));
    return res;
  }

  @Override
  public void is(RnDouble val)
  {
    clear();
    for (int i = 0; i < n; i++)
      add(val.get(i));
  }

  @Override
  public RnDouble divS(Double val)
  {
    RnDouble res = new RnDouble(n);
    for (int i = 0; i < n; i++)
      res.add(get(i) / val);
    return res;
  }

  @Override
  public RnDouble minus(RnDouble val)
  {
    RnDouble res = new RnDouble(n);
    for (int i = 0; i < n; i++)
      res.add(val.get(i) - get(i));
    return res;
  }

  @Override
  public RnDouble div(RnDouble val)
  {
    RnDouble res = new RnDouble(n);
    for (int i = 0; i < n; i++)
      res.add(get(i) / val.get(i));
    return res;
  }

}
