package si.vajnartech.vajnarglobe;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;

public class CaptureView extends GeoMap
{
  private final Paint paint = new Paint();

  CaptureView(Context ctx)
  {
    super(ctx);
  }

  CaptureView(MainActivity ctx, UpdateUI updateUI)
  {
    super(ctx, updateUI);
    if (C.DEBUG_MODE) {
      new Handler().postDelayed(this::initLocation, 4700);
    }
  }

  @Override
  public boolean performClick()
  {
    return super.performClick();
  }

  @Override
  protected void onDraw(Canvas canvas)
  {
    super.onDraw(canvas);

    if (currentPoint != null) {
      if (!isMoving)
        currentPoint.draw(canvas, paint, Color.GREEN, 8, this);
      else
        currentPoint.draw(canvas, paint, Color.RED, 3, this);
    }

    if (currentArea == null)
      return;

    if (currentArea.isConstructed())
      currentArea.draw(canvas, paint, Color.BLACK, this);

    for (GeoPoint p : currentArea.geoPoints)
      p.draw(canvas, paint, Color.BLUE, 6, this);
  }
}
