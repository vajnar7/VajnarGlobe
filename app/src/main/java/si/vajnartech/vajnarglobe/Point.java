package si.vajnartech.vajnarglobe;

import android.graphics.Canvas;
import android.graphics.Paint;

@SuppressWarnings("SameParameterValue")
class Point
{
  double x, y;

  Point(double x, double y)
  {
    this.x = x;
    this.y = y;
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

  void is(Point a)
  {
    x = a.x;
    y = a.y;
  }
}

