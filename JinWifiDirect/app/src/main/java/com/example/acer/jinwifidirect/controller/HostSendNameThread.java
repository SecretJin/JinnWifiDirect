package com.example.acer.jinwifidirect.controller;

import android.util.Log;

import com.example.acer.jinwifidirect.view.HostChatActivity;
import com.example.acer.jinwifidirect.repository.ProfileUtil;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by Acer on 10/4/2017.
 */
public class HostSendNameThread implements Runnable
{
    private ServerSocket serverSocket;
    private HostChatActivity activity;
    private int port;

    public HostSendNameThread(HostChatActivity acti, int p)
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
            Socket serverClient = serverSocket.accept();
            PrintWriter pw = new PrintWriter(serverClient.getOutputStream(), true);
            if(ProfileUtil.getInstance().getName()!=null) {
                pw.println(ProfileUtil.getInstance().getName());
                pw.flush();
            }
            else
            {
                pw.println("Anonymous");
                pw.flush();
            }

            //input stream blocks
            BufferedReader br = new BufferedReader(new InputStreamReader(serverClient.getInputStream()));
            final String message = br.readLine();
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    activity.setHostName(message);
                }
            });
            pw.flush();
        } catch (IOException e) {
            Log.v("error", e.toString());
        }finally {
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
}
