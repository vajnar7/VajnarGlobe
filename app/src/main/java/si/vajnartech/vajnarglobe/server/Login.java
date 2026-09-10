package si.vajnartech.vajnarglobe.server;

import static si.vajnartech.vajnarglobe.server.Controller.PWD;

import android.util.Log;

import java.io.BufferedReader;


public class Login extends RestBase<ObjLogin, RObjLogin>
{
    protected RestBase<?, ?> task;
    protected String email;

    public Login(RestBase<?, ?> task, String email)
    {
        super();
        this.task = task;
        this.email = email;
    }

    @Override
    protected RObjLogin deserialize(BufferedReader br)
    {
        return gson.fromJson(br, RObjLogin.class);
    }

    @Override
    public RObjLogin backgroundFunc()
    {
        return callServer(new ObjLogin(email, PWD));
    }

    @Override
    protected void onFail()
    {
        Log.i("vajnarglobe", "TODO");
    }

    @Override
    protected void onPostExecute(RObjLogin res)
    {
        if (res != null) {
            RestBase.setCachedToken(res.token);
            task.execute(res.token);
        }
    }
}
