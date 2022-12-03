package si.vajnartech.vajnarglobe;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public abstract class Aproximator
{
  ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
  Aproximator(int timeout)
  {
    executor.scheduleAtFixedRate(this::go, 0, timeout, TimeUnit.MILLISECONDS);
  }

  void end()
  {
    executor.shutdownNow();
  }

  abstract void go();
}
