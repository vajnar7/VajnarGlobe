package si.vajnartech.vajnarglobe.math;

import android.graphics.Canvas;
import android.graphics.Paint;

import si.vajnartech.vajnarglobe.Transformator;

public class R2Double extends RnDouble
{
  public R2Double()
  {
    super(2);
    add(0.0);
    add(0.0);
  }

  public R2Double(R2Double a)
  {
    super(2);
    add(a.get(0));
    add(a.get(1));
  }
  public R2Double(Double x1, Double x2)
  {
    super(2);
    add(x1);
    add(x2);
  }

  public Double x1()
  {
    return get(0);
  }

  public Double x2()
  {
    return get(1);
  }

  public void draw(Canvas canvas, Paint paint, int color, int r, Transformator tr)
  {
    R2Double p = tr.transform(this);
    paint.setColor(color);
    double x = p.x1();
    double y = p.x2();
    canvas.drawCircle((float)x, (float)y, r, paint);
  }
}
