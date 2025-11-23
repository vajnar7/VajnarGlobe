package com.vajnar.vajnargnss;

import android.util.Log;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicBoolean;

public class TcpClient
{
    protected String serverIp;
    protected int serverPort;
    private Socket socket;
    OutputStream outStream;
    InputStream inStream;

    private final AtomicBoolean running = new AtomicBoolean(false);

    public TcpClient(String serverIp, int serverPort)
    {
        this.serverPort = serverPort;
        this.serverIp = serverIp;
    }

    public void send(String data) throws IOException
    {
        OutputStream os = socket.getOutputStream();
        DataOutputStream out = new DataOutputStream(os);
        out.write(data.getBytes());
    }

    public ByteArrayOutputStream receive(int size) throws IOException
    {
        byte[] data = new byte[size];
        InputStream is = socket.getInputStream();
        int n = is.read(data, 0, size);
        ByteArrayOutputStream res = new ByteArrayOutputStream();
        res.write(data, 0, n);
        return res;
    }

    public void run(OnDataReceived onDataReceived) throws IOException
    {
//      running.set(true);

      onDataReceived.dataReceived(receive(128));

//      while (running.get()) {
//          onDataReceived.dataReceived(receive(128));
//      }
    }

    public void stop()
    {
        running.set(false);
    }

//    public ByteArrayOutputStream receive() throws IOException {
//        int n;
//        byte[] data = new byte[1024 * 4];
//        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
//
//        InputStream is = socket.getInputStream();
//        DataInputStream input = new DataInputStream(is);
//
//        while ((n = input.read(data, 0, data.length)) != -1) {
//           buffer.write(data, 0, n);
//        }
//        input.close();
//
//        return buffer.toByteArray();
//    }

    public void connect() throws IOException {
        InetAddress serverAddr = InetAddress.getByName(serverIp);
        socket = new Socket(serverAddr, serverPort);
        outStream = socket.getOutputStream();
        inStream = socket.getInputStream();
    }

    public interface OnDataReceived
    {
        void dataReceived(ByteArrayOutputStream data);
    }

    public Socket getSocket()
    {
        return socket;
    }

}
