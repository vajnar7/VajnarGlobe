package si.vajnartech.vajnarglobe.rest;

import java.io.BufferedReader;

import si.vajnartech.vajnarglobe.MainActivity;

public class SendFile extends RestBase<FileObjectR>
{
    SendFile(String url, String requestMethod, MainActivity act) {
        super(url, requestMethod, act);
    }

    @Override
    protected FileObjectR backgroundFunc() {
        return null;
    }

    @Override
    protected void onFail() {

    }

    @Override
    protected FileObjectR deserialize(BufferedReader br) {
        return null;
    }

    @Override
    protected void onPostExecute(FileObjectR fileObjectR) {

    }
}
