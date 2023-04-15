package si.vajnartech.vajnarglobe;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Aproximator
{
  private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

  private final int timeout;

  private final AproximatorRunnable runnable;

  Aproximator(int timeout, AproximatorRunnable runnable)
  {
    this.timeout = timeout;
    this.runnable = runnable;
    start();
  }

  private void start()
  {
    executor.scheduleAtFixedRate(runnable::aproximatorRun, 0, timeout, TimeUnit.MILLISECONDS);
  }

  public void end()
  {
    executor.shutdownNow();
  }

  public interface AproximatorRunnable
  {
    void aproximatorRun();
  }
}
