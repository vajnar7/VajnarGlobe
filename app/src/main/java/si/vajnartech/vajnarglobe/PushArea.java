package si.vajnartech.vajnarglobe;

import androidx.annotation.NonNull;

public class PushArea extends REST<PushArea.PushReturnObj>
{
  PushArea(String url, MainActivity act)
  {
    super(url, act);
  }

  @Override
  public PushReturnObj backgroundFunc()
  {
    return null;
  }

  @Override
  public void onFail()
  {

  }

  static class PushReturnObj
  {
    String return_code;

    @NonNull
    @Override
    public String toString()
    {
      return "LocationObj{" +
          "return_code='" + return_code + '\'' +
          "}";
    }
  }
}

