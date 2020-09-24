package com.example.acer.jinwifidirect.controller;

import android.util.Log;
import java.io.IOException;
import java.io.ObjectInputStream;

import com.example.acer.jinwifidirect.view.HostChatActivity;
import com.example.acer.jinwifidirect.repository.SocketUtil;

import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class HostSendProfileThread implements Runnable
{
    private HostChatActivity activity;
    private ServerSocket serverSocket;
    private ObjectInputStream ois;
    private int port;
    public enum Request {
        HPROFILE,;
    }
    public HostSendProfileThread (HostChatActivity acti,int p)
    {
        activity = acti;
        port=p;
    }
    @Override
    public void run()
    {
        try
        {
            serverSocket = new ServerSocket();
            serverSocket.setReuseAddress(true);
            serverSocket.bind(new InetSocketAddress(port));
            while(true)
            {
                Socket serverClient = serverSocket.accept();
                ois = new ObjectInputStream(serverClient.getInputStream());
                SocketUtil.getInstance().storeClientSocket2(serverClient);
                try {
                    final Request requestCode = (Request) ois.readObject();
                    switch (requestCode) {
                        case HPROFILE:
                            sendProfile();
                            break;
                    }
                }
                catch (ClassNotFoundException e) {
                    Log.v("error", e.toString());
                }
            }
        }
        catch (IOException e)
        {
            Log.v("error", e.toString());
        }
        finally {
            if(serverSocket != null  && !serverSocket.isClosed()) {
                try {
                    serverSocket.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    public void sendProfile() {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                activity.request();
            }
        });
    }

    public void interruptProfileSocket()
    {
        try {
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
