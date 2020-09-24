package com.example.acer.jinwifidirect.controller;

import android.util.Log;

import com.example.acer.jinwifidirect.view.ClientChatActivity;
import com.example.acer.jinwifidirect.repository.SocketUtil;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

public class ClientSendProfileThread implements Runnable
{
    private ClientChatActivity activity;
    private String hostAddress;
    private ObjectInputStream ois;
    private ObjectOutputStream oos;
    private int port;
    private Socket socket = null;
    public enum Request {
        PROFILE,;
    }
    public ClientSendProfileThread (ClientChatActivity acti,String host,int p)
    {
        activity = acti;
        hostAddress = host;
        port = p;
    }
    @Override
    public void run()
    {
        try {
            socket = new Socket();
            InetSocketAddress socketAddress = new InetSocketAddress(hostAddress, port);
            socket.bind(null);
            socket.connect(socketAddress);
            while(true)
            {
                ois = new ObjectInputStream(socket.getInputStream());
                SocketUtil.getInstance().storeClientSocket(socket);
                try {
                    final Request requestCode = (Request) ois.readObject();
                    switch (requestCode) {
                        case PROFILE:
                            sendProfile();
                            break;
                    }
                }
                catch (ClassNotFoundException e) {
                    Log.v("error", e.toString());
                }
            }
        } catch (IOException e) {
            Log.v("error", e.toString());
        } finally {
            if (socket != null) {
                if (socket.isConnected()) {
                    try {
                        socket.close();
                    } catch (IOException e) {
                        //catch logic
                    }
                }
            }
        }
    }

    public void sendProfile()
    {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                activity.select();
            }
        });
    }

    public void interruptProfileSocket()
    {
        try {
            socket.close();
        } catch (IOException e) {
            //catch logic
        }
    }

}