package si.vajnartech.vajnarglobe.math;

import android.graphics.Canvas;
import android.graphics.Paint;

import si.vajnartech.vajnarglobe.Transform;

public class NumDouble2 extends NumDouble
{

  public NumDouble2(NumDouble2 val)
  {
    super(2, val);
  }

  public NumDouble2(Double... values)
  {
    super(2);
    add(values[0]);
    add(values[1]);
  }

  public void draw(Canvas canvas, Paint paint, int color, int r, Transform tr)
  {
    NumDouble2 p = tr.transform(this);
    paint.setColor(color);
    double x = p.get(0);
    double y = p.get(1);
    canvas.drawCircle((float)x, (float)y, r, paint);
  }
}
