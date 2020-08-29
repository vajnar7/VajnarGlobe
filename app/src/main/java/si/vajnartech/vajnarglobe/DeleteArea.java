package si.vajnartech.vajnarglobe;

import static si.vajnartech.vajnarglobe.C.DELETE_AREA;

public class DeleteArea extends REST<String>
{
  private String areaName;
  DeleteArea(String areaName, MainActivity act)
  {
    super(String.format(DELETE_AREA, areaName), act);
    this.areaName = areaName;
  }

  @Override
  public String backgroundFunc()
  {
    return callServer(areaName, OUTPUT_TYPE_JSON);
  }
}
