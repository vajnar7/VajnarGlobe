package si.vajnartech.vajnarglobe.math;

public class LinearFunction extends Function<Double, Double>
{
  public Double k;
  public Double n;

  public boolean isHorizontal;
  public boolean isVertical;


  public LinearFunction(Double k, Double  n)
  {
    this.k = k;
    this.n = n;
    if (k == null)
      isVertical = true;
    else if (k == 0)
      isHorizontal = true;
  }

  @Override
  protected Double function(Double val)
  {
    if (isVertical)
      return null;
    return k * val + n;
  }
}
