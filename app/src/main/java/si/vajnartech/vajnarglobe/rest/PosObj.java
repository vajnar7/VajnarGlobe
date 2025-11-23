package si.vajnartech.vajnarglobe.rest;

public class PosObj
{
    public PosObjR position = new PosObjR();
    public String action;

    public PosObj(Double lon, Double lat, Double h)
    {
        position.lon = lon;
        position.lat = lat;
        position.h = h;
        action = "START";
    }

    public PosObj()
    {
        this(0.0, 0.0, 0.0);
        action = "STOP";
    }
}
