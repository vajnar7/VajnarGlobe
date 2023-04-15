package si.vajnartech.vajnarglobe.math;

import java.util.ArrayList;

public class NumDouble extends Num<Double>
{
  NumDouble(int dimension)
  {
    super(dimension);
  }

  NumDouble(int dimension, ArrayList<Double> values)
  {
    super(dimension, values);
  }

  @Override
  protected Double plusOp(Double a1, Double a2)
  {
    return a1 + a2;
  }

  @Override
  protected Double mulOp(Double a1, Double a2)
  {
    return a1 * a2;
  }

  @Override
  protected Double minusOp(Double a1, Double a2)
  {
    return a1 - a2;
  }

  @Override
  protected Double divOp(Double a1, Double a2)
  {
    return a1 / a2;
  }
}
