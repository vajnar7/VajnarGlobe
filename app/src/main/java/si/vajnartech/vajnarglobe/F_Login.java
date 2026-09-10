package si.vajnartech.vajnarglobe;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;

import si.vajnartech.vajnarglobe.server.CmdGetAreas;

public class F_Login extends MyFragmentFlat
{
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return createView(inflater, container, R.layout.login_screen);
    }

    @Override
    public void onClick(View view)
    {
        if (view.getId() == R.id.b_login) {
            SharedPref pref = new SharedPref(act);
            pref.put("username", getUserContainer().getText().toString());
            new CmdGetAreas(() -> {
                                pref.put("registered", true);
                                act.setFragment("precise", F_Precise.class, new Bundle());
                            },
                    pref.getString("username")
            );
        } else if (view.getId() == R.id.b_register) {
            act.setFragmentFlat("register", F_Register.class, new Bundle());
        }
    }

    @Override
    protected void init()
    {
        layout.findViewById(R.id.b_login).setOnClickListener(this);
        layout.findViewById(R.id.b_register).setOnClickListener(this);
    }

    protected EditText getUserContainer()
    {
        return layout.findViewById(R.id.e_user);
    }


}
