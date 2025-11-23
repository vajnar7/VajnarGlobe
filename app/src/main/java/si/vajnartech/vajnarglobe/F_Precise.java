package si.vajnartech.vajnarglobe;

import static si.vajnartech.vajnarglobe.C.DEBUG_MODE;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.vajnar.vajnargnss.NtripClient;

import java.util.concurrent.atomic.AtomicBoolean;

import si.vajnartech.vajnarglobe.rest.PrecisePosition;

interface UpdatePrecisePoint
{
    void setPrecisePoint(GeoPoint point);
}

public class F_Precise extends MyFragment<PreciseView> implements View.OnClickListener, UpdatePrecisePoint
{
    protected AtomicBoolean hasStarted = new AtomicBoolean(false);
    private NtripClient client;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        LinearLayout res = createView(inflater, container);

        printMessage(act.tx(R.string.mode_precise));
        myView = new PreciseView(act, this);
        myView.setOnTouchListener(myView);

        res.addView(myView);

        return res;
    }

    @Override
    public void onClick(View v)
    {
        if (v.getId() == R.id.zoom_in) {
            C.Parameters.zoomIn();
            myView.invalidate();
        } else if (v.getId() == R.id.zoom_out) {
            C.Parameters.zoomOut();
            myView.invalidate();
        } else if (v.getId() == START_STOP_BUTTON) {
            Button b = layout.findViewById(v.getId());
            if (!hasStarted.get()) {
                if (DEBUG_MODE) {
                    myView.mvUp();
                    myView.mvDown();
                }
                if (myView.isInit()) {
                    b.setText(R.string.stop);
                    hasStarted.set(true);
                    client = new NtripClient();
//                    new PrecisePosition(act,"START", myView.currentPoint.get(0), myView.currentPoint.get(1), 755.0);
                }
                else
                    act.runOnUiThread(() -> Toast.makeText(act, R.string.no_crnt_position, Toast.LENGTH_LONG).show());
            } else {
                b.setText(R.string.start);
                hasStarted.set(false);
//                new PrecisePosition(act, "STOP", this);
                client.stop();
            }
        }

        // ko sklopis debug mode tole zakomentiraj
        if (DEBUG_MODE) {
            if (v.getId() == R.id.test_left) {
                myView.mvLeft();
            } else if (v.getId() == R.id.test_right) {
                myView.mvRight();
            } else if (v.getId() == R.id.test_up) {
                myView.mvUp();
            } else if (v.getId() == R.id.test_down) {
                myView.mvDown();
            }
        }
    }

    @Override
    protected void init()
    {
        layout.findViewById(R.id.b_mark).setVisibility(View.GONE);
        layout.findViewById(R.id.b_construct).setVisibility(View.GONE);
        layout.findViewById(R.id.zoom_in).setOnClickListener(this);
        layout.findViewById(R.id.zoom_out).setOnClickListener(this);
        layout.findViewById(CANCEL_BUTTON).setVisibility(View.GONE);
        layout.findViewById(NEW_AREA_BUTTON).setVisibility(View.GONE);
        layout.findViewById(DELETE_AREA_BUTTON).setVisibility(View.GONE);

        layout.findViewById(PRECISE_NAVIGATION_BUTTONS).setVisibility(View.VISIBLE);
        layout.findViewById(START_STOP_BUTTON).setOnClickListener(this);


        if (DEBUG_MODE) {
            layout.findViewById(R.id.test_buttons).setVisibility(View.VISIBLE);
            layout.findViewById(R.id.test_left).setOnClickListener(this);
            layout.findViewById(R.id.test_right).setOnClickListener(this);
            layout.findViewById(R.id.test_up).setOnClickListener(this);
            layout.findViewById(R.id.test_down).setOnClickListener(this);
        }
    }

    @Override
    public void setPrecisePoint(GeoPoint point)
    {
        myView.setPrecisePoint(point);
    }
}
