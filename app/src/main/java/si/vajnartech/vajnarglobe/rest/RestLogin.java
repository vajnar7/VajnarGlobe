package si.vajnartech.vajnarglobe.rest;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class RestLogin
{
  private ExecutorService executor = Executors.newSingleThreadExecutor();
  Handler         handler  = new Handler(Looper.getMainLooper());

}
