package si.vajnartech.vajnarglobe;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

public class F_Register extends MyFragmentFlat
{
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return createView(inflater, container, R.layout.register_screen);
    }

    @Override
    protected void init() {

    }

    @Override
    public void onClick(View view) {

    }
}
