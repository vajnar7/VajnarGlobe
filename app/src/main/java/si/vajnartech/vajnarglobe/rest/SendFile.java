package si.vajnartech.vajnarglobe.rest;

import android.widget.Toast;

import java.io.BufferedReader;

import si.vajnartech.vajnarglobe.C;
import si.vajnartech.vajnarglobe.Json;
import si.vajnartech.vajnarglobe.MainActivity;
import si.vajnartech.vajnarglobe.R;

public class SendFile extends RestBase<FileObjectR>
{
    private final byte[] result;

    public SendFile(MainActivity act, byte[] result)
    {
        super(C.RTCM_NTRIP_DATA_API, "POST", act);

        this.result = result;
    }

    @Override
    protected FileObjectR backgroundFunc()
    {
        return callServer(new FileObject(result));
    }

    @Override
    protected void onFail()
    {
        act.get().runOnUiThread(() -> Toast.makeText(act.get(), R.string.server_conn_error, Toast.LENGTH_LONG).show());
    }

    @Override
    protected FileObjectR deserialize(BufferedReader br)
    {
        return Json.fromJson(br, FileObjectR.class);
    }

    @Override
    protected void onPostExecute(FileObjectR result)
    {
        if (result != null) {
//            posl na server se raw data
        }
    }
}
