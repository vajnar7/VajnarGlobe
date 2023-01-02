package si.vajnartech.vajnarglobe.rest;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RestBase
{
  private ExecutorService executor = Executors.newSingleThreadExecutor();
  private Handler         handler  = new Handler(Looper.getMainLooper());

  public RestBase()
  {
    executor.execute(() -> {

    });
  }
}
