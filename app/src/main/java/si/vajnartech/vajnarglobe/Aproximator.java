package si.vajnartech.vajnarglobe;

public abstract class Aproximator extends Thread
{
  private int to;
  Aproximator(int timeout)
  {
    to = timeout;
  }

  @Override public void run()
  {
    while (true) {
      go();
      try {
        Thread.sleep(1000 * to);
      } catch (InterruptedException e) {
        e.printStackTrace();
        break;
      }
    }
  }

  abstract void go();
}
