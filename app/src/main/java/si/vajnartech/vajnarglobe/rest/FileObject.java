package si.vajnartech.vajnarglobe.rest;

import java.nio.charset.StandardCharsets;

public class FileObject
{
    String data;

    public FileObject(byte[] result)
    {
        data = new String(result, StandardCharsets.UTF_8);
    }
}
