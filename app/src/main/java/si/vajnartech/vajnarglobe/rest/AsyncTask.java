package si.vajnartech.vajnarglobe.rest;

import android.os.Handler;
import android.os.Looper;

import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class AsyncTask<Result, Params>
{
  private final ExecutorService executor = Executors.newSingleThreadExecutor();

  private final Handler handler = new Handler(Looper.getMainLooper());

  private volatile Result result;

  public void execute(HashMap<String, Params> params)
  {
    executor.execute(() -> {
      result = background(params);

      handler.post(() -> onPostExecute(result));
    });
  }

  protected abstract Result background(HashMap<String, Params> params);

  protected abstract void onPostExecute(Result result);
}
