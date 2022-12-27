package si.vajnartech.vajnarglobe;

class CurrentArea extends Place
{
  private boolean isConstructed = false;
  CurrentArea(String name)
  {
    super(name);
  }

  @Override
  protected Area constructArea()
  {
    Area area = super.constructArea();
    if (area != null)
      isConstructed = true;
    return area;
  }

  public void setName(String name)
  {
    areaName = name;
  }

  boolean isConstructed()
  {
    return isConstructed;
  }
}
