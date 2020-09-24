package com.example.acer.jinwifidirect.controller;

import android.util.Log;

import com.example.acer.jinwifidirect.view.HostChatActivity;
import com.example.acer.jinwifidirect.repository.ProfileUtil;
import com.example.acer.jinwifidirect.repository.SocketUtil;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Created by Acer on 15/4/2017.
 */
public class RequestProfile implements Runnable
{
    public enum Result
    {
        RECEIVE,REJECTED;
    }
    private ObjectInputStream ois=null;
    private ObjectOutputStream oos=null;
    private HostChatActivity activity;

    public RequestProfile(HostChatActivity acti)
    {
        activity = acti;
    }

    @Override
    public void run()
    {
        try
        {
            oos = new ObjectOutputStream(SocketUtil.getInstance().getProfileSocket().getOutputStream());
            oos.writeObject(ClientSendProfileThread.Request.PROFILE);
            oos.flush();
            ois = new ObjectInputStream(SocketUtil.getInstance().getProfileSocket().getInputStream());
            try {
                final Result requestCode = (Result) ois.readObject();
                switch (requestCode) {
                    case RECEIVE:
                        receiveProfile();
                        break;
                    case REJECTED:
                        rejected();
                        break;
                }
            }
            catch (ClassNotFoundException e) {
                Log.v("error", e.toString());
            }


        }
        catch (IOException e)
        {
            Log.v("error", e.toString());
        }
    }

    public void receiveProfile()
    {
        try
        {
            ois = new ObjectInputStream(SocketUtil.getInstance().getProfileSocket().getInputStream());
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

    public void rejected()
    {
        activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    activity.reject();
                }
            });
    }

}
