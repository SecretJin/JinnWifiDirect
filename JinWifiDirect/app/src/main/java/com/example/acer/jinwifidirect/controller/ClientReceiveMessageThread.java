package com.example.acer.jinwifidirect.controller;

import android.util.Log;

import com.example.acer.jinwifidirect.view.ClientChatActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Socket;

public class ClientReceiveMessageThread implements Runnable
{
    private ClientChatActivity activity;
    private String hostAddress;
    private boolean isConnected = true;
    private int port;
    private Socket socket = null;
    public ClientReceiveMessageThread(ClientChatActivity acti, String host,int p)
    {
        activity = acti;
        hostAddress = host;
        port= p;
    }

    @Override
    public void run()
    {
        InetSocketAddress socketAddress = new InetSocketAddress(hostAddress, port);

        try {
            socket = new Socket();
            socket.bind(null);
            socket.connect(socketAddress);
            System.out.println("1233");
            while(isConnected) {
                try {
                    System.out.println("4567");
                    // TODO Start Receiving Messages
                    BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    final String message = br.readLine();
                    //
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            activity.updateReceiveChat(message);
                        }
                    });

                } catch (IOException e) {
                    Log.v("error", e.toString());
                    break;
                }
            }
        } catch (IOException e) {
            Log.v("error", e.toString());
        }
    }

    //get set
    public void setIsConnected(Boolean b)
    {
        isConnected = b;
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
