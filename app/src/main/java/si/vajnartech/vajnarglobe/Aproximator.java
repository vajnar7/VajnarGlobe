package si.vajnartech.vajnarglobe;

public abstract class Aproximator extends Thread
{
  private int     to;
  private boolean running = false;
  Aproximator(int timeout)
  {
    to = timeout;
  }

  @Override public void run()
  {
    while (running) {
      go();
      try {
        Thread.sleep(1000 * to);
      } catch (InterruptedException e) {
        e.printStackTrace();
        break;
      }
    }
  }

  void end()
  {
    running = false;
  }

  abstract void go();
}
