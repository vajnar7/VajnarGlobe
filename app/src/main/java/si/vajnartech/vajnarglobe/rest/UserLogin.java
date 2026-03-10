package si.vajnartech.vajnarglobe.rest;

import android.widget.Toast;

import com.vajnar.vajnargnss.OnFailInterface;

import java.io.BufferedReader;

import si.vajnartech.vajnarglobe.C;
import si.vajnartech.vajnarglobe.Json;
import si.vajnartech.vajnarglobe.MainActivity;
import si.vajnartech.vajnarglobe.R;

public class UserLogin extends RestBase<UserObjR>
{
    String user;
    private final Runnable runAfter;


    public UserLogin(String user, Runnable runAfter, OnFailInterface onFail)
    {
        super(C.USER_API, "POST", onFail);
        this.user = user;
        this.runAfter = runAfter;
    }

    @Override
    protected UserObjR backgroundFunc() {
        return callServer(new UserObj(user));
    }

    @Override
    protected UserObjR deserialize(BufferedReader br)
    {
        return Json.fromJson(br, UserObjR.class);
    }

    @Override
    protected void onPostExecute(UserObjR user)
    {
        if (user == null) return;

        if (runAfter != null)
            runAfter.run();
    }
}
