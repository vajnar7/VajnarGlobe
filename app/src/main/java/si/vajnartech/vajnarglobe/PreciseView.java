package si.vajnartech.vajnarglobe;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;


public class PreciseView extends TrackView
{
    protected GeoPoint precisePoint;

    PreciseView(Context ctx)
    {
        super(ctx);
    }

    PreciseView(MainActivity ctx, UpdateUI updateUI)
    {
        super(ctx, updateUI);
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
        if (precisePoint != null) {
            precisePoint.draw(canvas, paint, Color.BLUE, 6, this);
        }
    }

    public void setPrecisePoint(GeoPoint precisePoint)
    {
        this.precisePoint = precisePoint;
        invalidate();
    }
}
