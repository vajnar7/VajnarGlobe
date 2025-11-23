package si.vajnartech.vajnarglobe.rest;

import android.widget.Toast;

import java.io.BufferedReader;
import java.net.HttpURLConnection;

import si.vajnartech.vajnarglobe.C;
import si.vajnartech.vajnarglobe.F_Precise;
import si.vajnartech.vajnarglobe.GeoPoint;
import si.vajnartech.vajnarglobe.Json;
import si.vajnartech.vajnarglobe.MainActivity;
import si.vajnartech.vajnarglobe.R;

public class PrecisePosition extends RestBase<PosObjR>
{
    protected PosObj position;
    protected String action;
    protected F_Precise frag;

    public PrecisePosition(MainActivity act, String action, double lon, double lat, double h)
    {
        super(C.POS_API, "POST", act);
        position = new PosObj(lon, lat, h);
        this.action = action;
    }

    public PrecisePosition(MainActivity act, String action, F_Precise f)
    {
        super(C.POS_API, "POST", act);
        position = new PosObj();
        this.action = action;
        frag = f;
    }

    @Override
    protected PosObjR backgroundFunc()
    {
        return callServer(position);
    }

    @Override
    protected void onFail()
    {
        if (responseCode == HttpURLConnection.HTTP_INTERNAL_ERROR)
            act.get().runOnUiThread(() -> Toast.makeText(act.get(), R.string.invalid_gnss_data, Toast.LENGTH_LONG).show());
        else
            act.get().runOnUiThread(() -> Toast.makeText(act.get(), R.string.server_conn_error, Toast.LENGTH_LONG).show());
    }

    @Override
    protected PosObjR deserialize(BufferedReader br)
    {
        return Json.fromJson(br, PosObjR.class);
    }

    @Override
    protected void onPostExecute(PosObjR posObjR)
    {
        if (posObjR == null) return;

        if (action.equals("STOP"))
        {
            frag.setPrecisePoint(new GeoPoint(posObjR.lon, posObjR.lat));
        }
    }
}
