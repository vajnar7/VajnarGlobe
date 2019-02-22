package si.vajnartech.vajnarglobe;

import android.graphics.Canvas;
import android.graphics.Paint;

import si.vajnartech.vajnarglobe.math.R2Double;

@SuppressWarnings("SuspiciousNameCombination")
class Point extends R2Double
{
  Point(R2Double a)
  {
    super(a.get(0), a.get(1));
  }

  void draw(Canvas canvas, Paint paint, int color, int r, Area a)
  {
    // render
    paint.setColor(color);
    R2Double o1 = a.transform(new R2Double(get(0), get(1)), false);
    double q1 = o1.get(0);
    double q2 = o1.get(1);
    canvas.drawCircle((float)q1, (float)q2, r, paint);
  }
}
