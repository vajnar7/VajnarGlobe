package si.vajnartech.vajnarglobe;

import android.graphics.Canvas;
import android.graphics.Paint;

import si.vajnartech.vajnarglobe.math.R2Double;

class Point extends R2Double
{
  double x, y;

  Point(double x1, double x2)
  {
    super(x1, x2);

    x = get(0);
    y = get(1);
  }

  @Override
  public String toString()
  {
    return ("P(" + x + "," + y + ")");
  }

  void draw(Canvas canvas, Paint paint, int color, Area a)
  {
    // render
    paint.setColor(color);
    Point o1 = a.transform(new Point(x, y), false);
    canvas.drawCircle((float) o1.x, (float) o1.y, 4, paint);
  }

  void draw(Canvas canvas, Paint paint, int color, int r, Area a)
  {
    // render
    paint.setColor(color);
    Point o1 = a.transform(new Point(x, y), false);
    canvas.drawCircle((float) o1.x, (float) o1.y, r, paint);
  }
}

