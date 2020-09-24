package com.example.acer.jinwifidirect.controller;

import android.util.Log;

import com.example.acer.jinwifidirect.view.ClientChatActivity;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;

public class ClientSendMessageThread implements Runnable
{
    private String message;
    private String hostAddress;
    private int port;
    private Socket socket = null;
    private ClientChatActivity activity;

    public ClientSendMessageThread(String m,String host,ClientChatActivity acti,int p) {
        activity = acti;
        message = m;
        hostAddress = host;
        port=p;
    }


    @Override
    public void run()
    {
        InetSocketAddress socketAddress = new InetSocketAddress(hostAddress, port);

        try {
            socket = new Socket();
            socket.bind(null);
            socket.connect(socketAddress);

            //send message
            PrintWriter pw = new PrintWriter(socket.getOutputStream(), true);
            pw.println(message);
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    activity.updateSendChat(message);
                }
            });
            //

        } catch (IOException e) {
            Log.v("error", e.toString());
        }
    }

    public void interruptSocket()
    {
        try {
            socket.close();
        } catch (IOException e) {
            //catch logic
        }

    }


}
