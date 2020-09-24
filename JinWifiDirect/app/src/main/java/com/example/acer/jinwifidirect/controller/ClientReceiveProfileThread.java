
package com.example.acer.jinwifidirect.controller;

import android.util.Log;

import com.example.acer.jinwifidirect.view.ClientChatActivity;
import com.example.acer.jinwifidirect.repository.ProfileUtil;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

public class ClientReceiveProfileThread implements Runnable
{
    public enum Result {
        HRECEIVE,HREJECT;
    }
    private ClientChatActivity activity;
    private String hostAddress;
    private ObjectInputStream ois;
    private ObjectOutputStream oos;
    private Socket socket = null;
    private int port;
    public ClientReceiveProfileThread (ClientChatActivity acti,String host,int p)
    {
        activity = acti;
        hostAddress = host;
        port = p;
    }
    @Override
    public void run()
    {
        InetSocketAddress socketAddress = new InetSocketAddress(hostAddress, port);
        try {
            while (true)
            {
                socket = new Socket();
                if (socket != null)
                {
                    break;
                }
            }
            socket.bind(null);
            socket.connect(socketAddress);
            oos = new ObjectOutputStream(socket.getOutputStream());
            oos.writeObject(HostSendProfileThread.Request.HPROFILE);
            oos.flush();
            ois = new ObjectInputStream(socket.getInputStream());
            try {
                final Result requestCode = (Result) ois.readObject();
                switch (requestCode) {
                    case HRECEIVE:
                        accept();
                        break;
                    case HREJECT:
                        reject();
                        break;
                }
            }
            catch (ClassNotFoundException e) {
                Log.v("error", e.toString());
            }

        } catch (IOException e) {
            Log.v("error", e.toString());
        } finally {
            if (socket != null) {
                if (socket.isConnected()) {
                    try {
                        socket.close();
                    } catch (IOException e) {

                    }
                }
            }
        }
    }

    public void interruptProfileSocket()
    {
        if (socket != null) {
            try {
                socket.close();
            } catch (IOException e) {

            }
        }
    }

    public void accept()
    {
        try
        {
            ois = new ObjectInputStream(socket.getInputStream());
            final ProfileUtil profile = (ProfileUtil) ois.readObject();
            ProfileUtil.getInstance().storeProfile(profile);
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    activity.accept();
                }
            });
        }catch(IOException e){
            Log.v("error", e.toString());
        }catch(ClassNotFoundException e){
            Log.v("error", e.toString());
        }
    }

    public void reject()
    {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                activity.reject();
            }
        });
    }

}
