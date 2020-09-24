package com.example.acer.jinwifidirect.repository;

/**
 * Created by Acer on 27/3/2017.
 */

import java.net.Socket;

public class SocketUtil
{
    private static SocketUtil socketutil = null;
    private Socket socket;
    private Socket profileSocket;
    private Socket storeClient;
    private Socket storeClient2;
    private int connected;
    public void SocketsUtil() {};

    public static SocketUtil getInstance()
    {
        if(socketutil == null)
        {
            socketutil = new SocketUtil();
        }
        return socketutil;
    }

    public void initialize(Socket s,Socket so,int c)
    {
        socket = s;
        profileSocket=so;
        connected=c;
    }

    public void storeSocket(Socket s)
    {
        socket = s;
    }

    public Socket getSocket()
    {
        return socket ;
    }

    public void deleteSocket()
    {
        socket=null;
    }

    public void storeProfileSocket(Socket s)
    {
        profileSocket =s;
    }

    public Socket getProfileSocket()
    {
        return profileSocket;
    }

    public void storeClientSocket(Socket s)
    {
        storeClient =s;
    }

    public Socket getClientSocket()
    {
        return storeClient;
    }

    public void storeClientSocket2(Socket s)
    {
        storeClient2 =s;
    }

    public Socket getClientSocket2()
    {
        return storeClient2;
    }

    public void setConnect()
    {
        connected=1;
    }

    public int getConnect()
    {
        return connected;
    }

    public void setUnconnect()
    {
        connected=0;
    }




}
