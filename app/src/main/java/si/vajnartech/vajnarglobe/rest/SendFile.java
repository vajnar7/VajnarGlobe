package si.vajnartech.vajnarglobe.rest;

import android.widget.Toast;

import com.vajnar.vajnargnss.OnFailInterface;

import java.io.BufferedReader;

import si.vajnartech.vajnarglobe.C;
import si.vajnartech.vajnarglobe.Json;
import si.vajnartech.vajnarglobe.MainActivity;
import si.vajnartech.vajnarglobe.R;

public class SendFile extends RestBase<FileObjectR>
{
    private final byte[] result;

    public SendFile(byte[] result, OnFailInterface onFail)
    {
        super(C.RTCM_NTRIP_DATA_API, "POST", onFail);

        this.result = result;
    }

    @Override
    protected FileObjectR backgroundFunc()
    {
        return callServer(new FileObject(result));
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
