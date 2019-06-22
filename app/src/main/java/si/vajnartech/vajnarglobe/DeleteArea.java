package si.vajnartech.vajnarglobe;

import static si.vajnartech.vajnarglobe.Login.DELETE_AREA;

public class DeleteArea extends REST<String>
{
  private String areaName;
  DeleteArea(String areaName)
  {
    super(String.format(DELETE_AREA, areaName));
    this.areaName = areaName;
  }

  @Override
  public String backgroundFunc()
  {
    return callServer(areaName, OUTPUT_TYPE_JSON);
  }
}
