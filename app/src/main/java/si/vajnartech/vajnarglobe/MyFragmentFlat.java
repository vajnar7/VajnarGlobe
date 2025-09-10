package si.vajnartech.vajnarglobe;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

public abstract class MyFragmentFlat extends DialogFragment implements View.OnClickListener
{

    MainActivity   act;
    protected View layout;

    public LinearLayout createView(@NonNull LayoutInflater inflater, ViewGroup container, int layoutId)
    {
        layout = inflater.inflate(layoutId, container, false);
        init();

        return (LinearLayout) layout;
    }

    public static <T extends MyFragmentFlat> T instantiate(@NonNull Class<T> cls, MainActivity act)
    {
        T res = null;
        try {
            res = cls.newInstance();
            res.act = act;
        } catch (java.lang.InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return res;
    }

    protected abstract void init();
}
