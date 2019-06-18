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
    Area a = super.constructArea();
    if (a != null)
      isConstructed = true;
    return a;
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
