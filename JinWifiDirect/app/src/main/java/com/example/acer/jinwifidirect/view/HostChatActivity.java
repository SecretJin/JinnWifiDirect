package com.example.acer.jinwifidirect.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.DataSetObserver;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.example.acer.jinwifidirect.R;
import com.example.acer.jinwifidirect.controller.ClientReceiveProfileThread;
import com.example.acer.jinwifidirect.controller.HostStartReceiveProfileThread;
import com.example.acer.jinwifidirect.controller.HostSendProfileThread;
import com.example.acer.jinwifidirect.controller.HostSendNameThread;
import com.example.acer.jinwifidirect.controller.RequestProfile;
import com.example.acer.jinwifidirect.controller.ServerReceiveMessageThread;
import com.example.acer.jinwifidirect.controller.ServerSendMessageThread;
import com.example.acer.jinwifidirect.repository.ChatMessage;
import com.example.acer.jinwifidirect.repository.ProfileUtil;
import com.example.acer.jinwifidirect.repository.SocketUtil;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class HostChatActivity extends Activity {

    private ChatArrayAdapter chatArrayAdapter;
    private WifiP2pManager mManager;
    private WifiP2pManager.Channel mChannel;
    private final IntentFilter intentFilter = new IntentFilter();
    private BroadcastReceiver mReceiver = null;
    private ServerReceiveMessageThread serverReceiveMessageThread;
    private ServerSendMessageThread serverSendMessageThread;
    private HostSendNameThread hostSendNameThread;
    private HostSendProfileThread sendProfileThread;
    private HostStartReceiveProfileThread receiveProfileThread;
    private RequestProfile requestProfile;
    private boolean left;
    private ObjectOutputStream oos;
    private Socket s;
    private AlertDialog alert;
    private boolean added;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host_chat);
        final ListView listView = (ListView)findViewById(R.id.listView3);
        chatArrayAdapter = new ChatArrayAdapter(getApplicationContext(), R.layout.right);
        listView.setAdapter(chatArrayAdapter);
        listView.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        listView.setAdapter(chatArrayAdapter);
        mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mManager.initialize(this, getMainLooper(), null);
        deletePersistentGroups(mManager,mChannel);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        chatArrayAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                listView.setSelection(chatArrayAdapter.getCount() - 1);
            }
        });


    }

    public void setAdded(boolean b)
    {
        added=b;
        invalidateOptionsMenu();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.host_chat, menu);
        if(!added){
            menu.findItem(R.id.addBtn).setEnabled(true);
        }
        else
        {
            menu.findItem(R.id.addBtn).setEnabled(false);
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.addBtn:
                saveProfile();
                break;
        }
        return true;
    }

    public void hostForceDisconnect()
    {
        mManager.removeGroup(mChannel, new WifiP2pManager.ActionListener() {

            @Override
            public void onSuccess() {
                chatArrayAdapter.clear();
            }

            @Override
            public void onFailure(int reasonCode) {
                chatArrayAdapter.clear();
            }
        });

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                serverSendMessageThread.interruptSocket();
                serverReceiveMessageThread.interruptSocket();
                sendProfileThread.interruptProfileSocket();
                receiveProfileThread.interruptProfileSocket();
                hostSendNameThread.interruptSocket();
                quitBtn(true);
            }

        }, 3000);

    }

    public void updateReceiveChat(String message)
    {
        left=false;//left side
        chatArrayAdapter.add(new ChatMessage(message,left));
    }

    public void updateSendChat(String message)
    {
        left=true;//right side
        EditText messageET = (EditText)findViewById(R.id.editText3);
        chatArrayAdapter.add(new ChatMessage(message,left));
        messageET.setText("");

    }

    public void request()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Profile Request");
        builder.setMessage("User want to add your profile")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        try{
                            s = SocketUtil.getInstance().getClientSocket2();
                            oos = new ObjectOutputStream(s.getOutputStream());
                            oos.writeObject(ClientReceiveProfileThread.Result.HRECEIVE);
                            oos.flush();
                            oos = new ObjectOutputStream(s.getOutputStream());
                            oos.writeObject(ProfileUtil.getInstance());
                            oos.flush();
                        } catch (IOException e) {
                            Log.v("error", e.toString());
                        }
                    }
                })
                .setNegativeButton("Reject", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        try{
                            s = SocketUtil.getInstance().getClientSocket2();
                            oos = new ObjectOutputStream(s.getOutputStream());
                            oos.writeObject(ClientReceiveProfileThread.Result.HREJECT);
                            oos.flush();
                        } catch (IOException e) {
                            Log.v("error", e.toString());
                        }
                    }
                });
        alert = builder.create();
        alert.show();
    }

    public void setTime()
    {
        TextView messageTV = (TextView)findViewById(R.id.textView23);
        Calendar cal = Calendar.getInstance();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String date_str = df.format(cal.getTime());
        messageTV.setText(date_str);
    }

    public void reject()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Request Rejected");
        builder.setMessage("User rejected your request")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });
        alert = builder.create();
        alert.show();
        setAdded(false);
    }

    public void accept()
    {
        setAdded(true);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Request Accepted");
        builder.setMessage("User accepted your request")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });
        alert = builder.create();
        alert.show();
        setAdded(true);
    }

    public void checking()
    {
        if(SocketUtil.getInstance().getSocket()==null)
        {
            Button btn = (Button)findViewById(R.id.sendBtn);
            btn.setEnabled(false);
            added=true;
            invalidateOptionsMenu();
        }

        else
        {
            Button btn = (Button)findViewById(R.id.sendBtn);
            btn.setEnabled(true);
            added=false;
            invalidateOptionsMenu();
        }
    }

    public void disconnectChat (View v)
    {
        mManager.removeGroup(mChannel, new WifiP2pManager.ActionListener() {

            @Override
            public void onSuccess() {
                chatArrayAdapter.clear();
            }

            @Override
            public void onFailure(int reasonCode) {
                chatArrayAdapter.clear();
            }
        });
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                serverSendMessageThread.interruptSocket();
                serverReceiveMessageThread.interruptSocket();
                sendProfileThread.interruptProfileSocket();
                receiveProfileThread.interruptProfileSocket();
                hostSendNameThread.interruptSocket();
                quitBtn(true);
            }

        }, 3000);
    }

    public void quitChat (View v)
    {
        Intent a = new Intent(HostChatActivity.this, MenuSelectionActivity.class);
        startActivity(a);
    }

    public void quitBtn(boolean b)
    {
        Button btn = (Button)findViewById(R.id.button6);
        btn.setEnabled(b);
    }

    public void receiveProfileThread()
    {
        sendProfileThread = new HostSendProfileThread(this,4444);
        Thread t = new Thread(sendProfileThread);
        t.start();
    }

    public void profileThread()
    {
        requestProfile = new RequestProfile(this);
        Thread t = new Thread(requestProfile);
        t.start();
    }
    public void saveProfile()
    {
        profileThread();
        setAdded(true);
    }

    public void saveHProfile()
    {
        receiveProfileThread = new HostStartReceiveProfileThread(this,7777);
        Thread t = new Thread(receiveProfileThread);
        t.start();
    }
    @Override
    protected void onResume() {
        super.onResume();
        mReceiver = new HostChatBroadcastReceiver(mManager, mChannel, this);
        registerReceiver(mReceiver, intentFilter);//new object broadcast
    }
    /* unregister the broadcast receiver */
    @Override
    protected void onPause() {
        super.onPause();
        try {
            unregisterReceiver(mReceiver);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    public void serverSendMessage(View v)
    {
        EditText messageET = (EditText)findViewById(R.id.editText3);
        String message = messageET.getText().toString();
        PrintWriter pw = null;
        if (messageET.getText().toString().trim().length() != 0) {
            try {
                pw = new PrintWriter(SocketUtil.getInstance().getSocket().getOutputStream(), true);//null object
                pw.println(message);
                updateSendChat(message);
                pw.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void setHostName(String s)
    {
        TextView messageTV = (TextView)findViewById(R.id.textView11);
        messageTV.setText(s);
    }

    public void serverSendingMessage()
    {
        serverSendMessageThread = new ServerSendMessageThread(this,8888);
        Thread t = new Thread(serverSendMessageThread);
        t.start();
    }

    public void serverReceivingMessage()
    {
        serverReceiveMessageThread = new ServerReceiveMessageThread(this,8867);
        Thread t = new Thread(serverReceiveMessageThread);
        t.start();
    }

    public void serverProfileName()
    {
        hostSendNameThread = new HostSendNameThread(this,5555);
        Thread t = new Thread(hostSendNameThread);
        t.start();
    }

    public void hostSend (boolean b)
    {
        Button btn = (Button)findViewById(R.id.sendBtn);
        btn.setEnabled(b);
    }

    @Override
    public void onStart()
    {
        super.onStart();
        final Handler handler = new Handler();
    }

    @Override
    public void onBackPressed() {}

    public void deletePersistentGroups(WifiP2pManager manager, WifiP2pManager.Channel channel) {
        try {
            Method method = WifiP2pManager.class.getMethod("deletePersistentGroup",
                    WifiP2pManager.Channel.class, int.class, WifiP2pManager.ActionListener.class);

            for (int netId = 0; netId < 32; netId++) {
                method.invoke(manager, channel, netId, null);
            }
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

}
