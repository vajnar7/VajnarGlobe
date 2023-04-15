package si.vajnartech.vajnarglobe.math;

public class TrackFunction extends Function<Long, NumDouble2>
{
  private static final int POINTS_BACK = 1;

  protected LinearFunction function1, function2;

  @Override
  protected NumDouble2 function(Long val)
  {
    // null pomeni NaN
    if (size() == POINTS_BACK)
      return get(0);
    if (size() == 0)
      return null;

    // Potegni linearno funkcijo skozi zadnjo in zadnjo - POINTS_BACK tocko
    Long t1 = keys.get(size() - 1 - POINTS_BACK);
    Long t2 = keys.get(size() - 1);
    NumDouble2 p1 = get(t1);
    NumDouble2 p2 = get(t2);
    assert p1 != null;
    assert p2 != null;
    function1 = defineFunction(p2.get(0), p1.get(0), t1, t2);
    function2 = defineFunction(p2.get(1), p1.get(1), t1, t2);

    Double t = Double.valueOf(val);
    return new NumDouble2(function1.f(t), function2.f(t));
  }

  private LinearFunction defineFunction(Double x1, Double x0, Long t1, Long t2)
  {
    Double k = null;
    Double n = null;
    if ((t2 - t1) != 0) {
      k = (x1 - x0) / (t2 - t1);
      n = x0 - k * t1;
    }

    return new LinearFunction(k, n);
  }
}
