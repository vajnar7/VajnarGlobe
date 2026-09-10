package si.vajnartech.vajnarglobe.server;

public class ObjLogin
{
    public String email;
    public String password;
    public Boolean registered;

    public ObjLogin (String email, String password)
    {
        this.email = email;
        this.password = password;
        this.registered = true;
    }
}
