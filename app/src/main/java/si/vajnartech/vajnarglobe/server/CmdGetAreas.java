package si.vajnartech.vajnarglobe.server;

import android.util.Log;

import java.io.BufferedReader;

public class CmdGetAreas extends Controller<RObjAreas>
{
    protected Runnable runAfter;

    public CmdGetAreas(Runnable runAfter, String email)
    {
        super("get_areas", email);
        this.runAfter = runAfter;
    }

    @Override
    protected RObjAreas deserialize(BufferedReader br)
    {
        return gson.fromJson(br, RObjAreas.class);
    }

    @Override
    public RObjAreas backgroundFunc()
    {
        return callServer(null);
    }

    @Override
    protected void onFail()
    {
            Log.i("VajnarGlobe", "Error");
    }

    @Override
    protected void onPostExecute(RObjAreas areas)
    {
        Log.i("pepe", "Areas: " + areas);
        if (runAfter != null)
            runAfter.run();
    }
}
