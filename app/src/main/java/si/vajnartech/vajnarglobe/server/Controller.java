package si.vajnartech.vajnarglobe.server;

public abstract class Controller<R> extends RestBase<ObjController, R>
{
    public static String URL = "http://192.168.7.73:5000/";
    public static String PWD = "password123";

    public Controller(String cmd, String email)
    {
        super(URL + cmd, email);
    }
}
