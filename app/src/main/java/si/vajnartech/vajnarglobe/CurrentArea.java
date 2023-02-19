package si.vajnartech.vajnarglobe;

class CurrentArea extends Place
{
  private boolean isConstructed = false;
  CurrentArea()
  {
    super("");
  }

  @Override
  public boolean constructArea()
  {
    isConstructed = super.constructArea();
    return isConstructed;
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
