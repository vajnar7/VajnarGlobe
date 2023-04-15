package si.vajnartech.vajnarglobe;

import android.graphics.Canvas;
import android.graphics.Paint;

import si.vajnartech.vajnarglobe.math.TrackFunction;

class Path extends TrackFunction
{
   void draw(Canvas c, Paint paint, int color, Transform tr)
   {
      for (int i = 0; i < size() - 1; i++)
         new Line(get(keys.get(i)), get(keys.get(i + 1))).draw(c, paint, color, tr);
   }
}
