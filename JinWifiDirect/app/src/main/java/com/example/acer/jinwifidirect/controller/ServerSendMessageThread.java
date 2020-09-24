package com.example.acer.jinwifidirect.controller;

import android.util.Log;

import com.example.acer.jinwifidirect.view.HostChatActivity;
import com.example.acer.jinwifidirect.repository.SocketUtil;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerSendMessageThread implements Runnable
{
    private ServerSocket serverSocket;
    private HostChatActivity activity;
    private boolean isConnected = true;
    private int port;

    public ServerSendMessageThread (HostChatActivity acti, int p)
    {
        activity = acti;
        port = p;
    }

    @Override
    public void run()
    {
        try {
            serverSocket = new ServerSocket();
            serverSocket.setReuseAddress(true);
            serverSocket.bind(new InetSocketAddress(port));

            while(true) {
                try {

                    Socket serverClient = serverSocket.accept();
                    Log.d("AA","ABCD");
                    SocketUtil.getInstance().storeSocket(serverClient);
                    Log.d("AA","ABCDE");

                } catch (IOException e) {
                    break;
                }
            }

        } catch (IOException e) {
            Log.v("error", e.toString());
        } finally {
            if(serverSocket != null && !serverSocket.isClosed()) {
                try {
                    serverSocket.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    public void interruptSocket()
    {
        try {
            serverSocket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setIsConnected(Boolean b)
    {
        isConnected = b;
    }


}
