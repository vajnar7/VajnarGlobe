package si.vajnartech.vajnarglobe.math;

import java.util.ArrayList;

abstract class Num<T> extends ArrayList<T>
{
  ArrayList<T> tmp;
  protected int dimension;

  Num(int dimension)
  {
    this.dimension = dimension;
  }

  Num(int dimension, ArrayList<T> values)
  {
    this.dimension = dimension;
    addAll(values);
  }

  public void is(Num<T> val)
  {
    addAll(val);
  }

  private void reset()
  {
    tmp = new ArrayList<>(this);
    clear();
  }

  public void plus(Num<T> value)
  {
    reset();
    for(int j = 0; j < dimension; j++)
      add(plusOp(tmp.get(j), value.get(j)));
  }

  public void mul(Num<T> value)
  {
    reset();
    for(int j = 0; j < dimension; j++)
      add(mulOp(tmp.get(j), value.get(j)));
  }

  public void minus(Num<T> value)
  {
    reset();
    for(int j = 0; j < dimension; j++)
      add(minusOp(tmp.get(j), value.get(j)));
  }

  public void div(Num<T> value)
  {
    reset();
    for(int j = 0; j < dimension; j++)
      add(divOp(tmp.get(j), value.get(j)));
  }

  protected abstract T plusOp(T a1, T a2);
  protected abstract T mulOp(T a1, T a2);
  protected abstract T minusOp(T a1, T a2);
  protected abstract T divOp(T a1, T a2);

//  public abstract Num<T> plus(Num<T> value);
//  public abstract Num<T> mul(Num<T> value);
//  public abstract Num<T> minus(Num<T> value);
//  public abstract Num<T> div(Num<T> valu);
}
