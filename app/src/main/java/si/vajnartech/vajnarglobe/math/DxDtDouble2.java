package si.vajnartech.vajnarglobe.math;

public class DxDtDouble2 extends DxDt<NumDouble2>
{
  @Override
  public NumDouble2 value(int t)
  {
    if (size() > 1) {
      NumDouble2 res = new NumDouble2(get(size() - 1));
      res.minus(get(size() - 2));
      return res;
    }
    return null;
  }
}
