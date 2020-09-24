package com.example.acer.jinwifidirect.controller;

import android.util.Log;

import com.example.acer.jinwifidirect.view.HostChatActivity;
import com.example.acer.jinwifidirect.repository.SocketUtil;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by Acer on 8/4/2017.
 */
public class HostStartReceiveProfileThread implements Runnable
{
    private HostChatActivity activity;
    private ServerSocket serverSocket;
    private int port;

    public HostStartReceiveProfileThread(HostChatActivity acti, int p)
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
                Socket client = serverSocket.accept();
                SocketUtil.getInstance().storeProfileSocket(client);
            }
        }
        catch (IOException e)
        {
            Log.v("error", e.toString());
        }finally {
            if(serverSocket != null  && !serverSocket.isClosed()) {
                try {
                    serverSocket.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
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
