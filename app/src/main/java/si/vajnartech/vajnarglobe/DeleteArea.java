package si.vajnartech.vajnarglobe;

import static si.vajnartech.vajnarglobe.C.DELETE_AREA;

public class DeleteArea extends REST<String>
{
  private final String areaName;

  DeleteArea(String areaName, MainActivity act)
  {
    super(DELETE_AREA, act, "DELETE");
    this.areaName = areaName;
  }

  @Override
  public String backgroundFunc()
  {
    return callServer(areaName, OUTPUT_TYPE_JSON);
  }

  @Override public void onFail()
  {

  }
}
