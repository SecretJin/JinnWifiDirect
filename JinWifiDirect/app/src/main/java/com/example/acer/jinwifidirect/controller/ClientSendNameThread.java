package com.example.acer.jinwifidirect.controller;

import android.util.Log;

import com.example.acer.jinwifidirect.view.ClientChatActivity;
import com.example.acer.jinwifidirect.repository.ProfileUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;

public class ClientSendNameThread implements Runnable
{
    private ClientChatActivity activity;
    private String hostAddress;
    private int port;
    private Socket socket = null;
    public ClientSendNameThread(ClientChatActivity acti, String host, int p)
    {
        activity = acti;
        hostAddress = host;
        port= p;
    }

    @Override
    public void run() {
        InetSocketAddress socketAddress = new InetSocketAddress(hostAddress, port);

        try {

            socket = new Socket();
            socket.bind(null);
            socket.connect(socketAddress);
            try {
                // TODO Start Receiving Messages
                BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                final String message = br.readLine();
                //
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        activity.setClientName(message);
                    }
                });
                PrintWriter pw = new PrintWriter(socket.getOutputStream(), true);
                if(ProfileUtil.getInstance().getName()!=null) {
                    pw.println(ProfileUtil.getInstance().getName());
                    pw.flush();
                }
                else
                {
                    pw.println("Anonymous");
                    pw.flush();
                }

            } catch (IOException e) {
                Log.v("error", e.toString());
            }


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
