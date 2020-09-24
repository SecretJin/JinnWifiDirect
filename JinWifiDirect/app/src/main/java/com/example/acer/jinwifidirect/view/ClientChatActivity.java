package com.example.acer.jinwifidirect.view;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.DataSetObserver;
import android.net.wifi.p2p.WifiP2pManager;
import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import java.io.IOException;
import java.io.ObjectOutputStream;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.example.acer.jinwifidirect.R;
import com.example.acer.jinwifidirect.controller.ClientReceiveMessageThread;
import com.example.acer.jinwifidirect.controller.ClientReceiveProfileThread;
import com.example.acer.jinwifidirect.controller.ClientSendMessageThread;
import com.example.acer.jinwifidirect.controller.ClientSendProfileThread;
import com.example.acer.jinwifidirect.controller.ClientSendNameThread;
import com.example.acer.jinwifidirect.controller.RequestProfile;
import com.example.acer.jinwifidirect.repository.ChatMessage;
import com.example.acer.jinwifidirect.repository.ProfileUtil;
import com.example.acer.jinwifidirect.repository.SocketUtil;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class ClientChatActivity extends Activity {

    private ChatArrayAdapter chatArrayAdapter;
    private WifiP2pManager mManager;
    private WifiP2pManager.Channel mChannel;
    private final IntentFilter intentFilter = new IntentFilter();
    private BroadcastReceiver mReceiver = null;
    private String hostAddress;
    private ClientReceiveMessageThread clientReceiveThread;
    private ClientSendMessageThread sendThread;
    private boolean left;
    private ClientSendNameThread clientSendNameThread;
    private ObjectOutputStream oos;
    private Socket s;
    private AlertDialog alert;
    private ClientReceiveProfileThread receiveProfileThread;
    private ClientSendProfileThread sendProfileThread;
    private boolean added;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_chat);
        final ListView listView = (ListView)findViewById(R.id.listView4);
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

    public void checking()
    {
        if(SocketUtil.getInstance().getSocket()==null)
        {
            Button btn = (Button)findViewById(R.id.button3);
            btn.setEnabled(false);
            added=true;
            invalidateOptionsMenu();
        }

        else
        {
            Button btn = (Button)findViewById(R.id.button3);
            btn.setEnabled(true);
            added=false;
            invalidateOptionsMenu();
        }
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

    public void clientSendMessage(View v)
    {
        EditText messageET = (EditText)findViewById(R.id.editText);
        String message = messageET.getText().toString();
        if (message.matches("")) {}
        else
        {
            sendThread = new ClientSendMessageThread(message,hostAddress,this,8867);
            Thread t = new Thread(sendThread);
            t.start();
        }
    }

    public void clientForceDisconnect()
    {
        mManager.removeGroup(mChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {}

            @Override
            public void onFailure(int reasonCode) {}
        });

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                clientReceiveThread.setIsConnected(false);
                clientReceiveThread.interruptSocket();
                clientSendNameThread.interruptSocket();
                sendProfileThread.interruptProfileSocket();
                quitBtn(true);
            }

        }, 3000);
    }

    public void setClientName(String s)
    {
        TextView messageTV = (TextView)findViewById(R.id.textView2);
        messageTV.setText(s);
    }

    public void setHostAddress(String address)
    {
        hostAddress = address;
    }

    public void updateReceiveChat(String message)
    {
        left=false;//left side
        chatArrayAdapter.add(new ChatMessage(message,left));
    }

    public void updateSendChat(String message)
    {
        left=true;//right side
        EditText messageET = (EditText)findViewById(R.id.editText);
        chatArrayAdapter.add(new ChatMessage(message,left));
        messageET.setText("");
    }

    public void disconnectChat (View v)
    {
        mManager.removeGroup(mChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {}

            @Override
            public void onFailure(int reasonCode) {}
        });

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                clientReceiveThread.setIsConnected(false);
                clientReceiveThread.interruptSocket();
                clientSendNameThread.interruptSocket();
                sendProfileThread.interruptProfileSocket();
                quitBtn(true);
            }

        }, 3000);
    }

    public void accept()
    {
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

    @Override
    public void onStart()
    {
        super.onStart();
        final Handler handler = new Handler();
    }

    public void quitChat(View v)
    {
        Intent a = new Intent(ClientChatActivity.this, MenuSelectionActivity.class);
        startActivity(a);
    }

    public void quitBtn(boolean b)
    {
        Button btn = (Button)findViewById(R.id.quitBtn);
        btn.setEnabled(b);
    }

    public void clientSend(boolean b)
    {
        Button btn = (Button)findViewById(R.id.button3);
        btn.setEnabled(b);
    }

    public void clientStartReceivingMessage()
    {
        clientReceiveThread = new ClientReceiveMessageThread(this,hostAddress,8888);
        Thread t = new Thread(clientReceiveThread);
        t.start();
        System.out.println("678");
    }

    public void setTime()
    {
        TextView messageTV = (TextView)findViewById(R.id.textView24);
        Calendar cal = Calendar.getInstance();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String date_str = df.format(cal.getTime());
        messageTV.setText(date_str);
    }

    public void select()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Profile Request");
        builder.setMessage("User want to add your profile")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        try{
                            s = SocketUtil.getInstance().getClientSocket();
                            oos = new ObjectOutputStream(s.getOutputStream());
                            oos.writeObject(RequestProfile.Result.RECEIVE);
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
                            s = SocketUtil.getInstance().getClientSocket();
                            oos = new ObjectOutputStream(s.getOutputStream());
                            oos.writeObject(RequestProfile.Result.REJECTED);
                            oos.flush();
                        } catch (IOException e) {
                            Log.v("error", e.toString());
                        }
                    }
                });
        alert = builder.create();
        alert.show();
    }

    public void saveProfile()
    {
        setAdded(true);
        receiveProfileThread = new ClientReceiveProfileThread(this,hostAddress,4444);
        Thread t = new Thread(receiveProfileThread);
        t.start();
    }

    public void receiveProfile()
    {
        sendProfileThread = new ClientSendProfileThread(this,hostAddress,7777);
        Thread t = new Thread(sendProfileThread);
        t.start();
    }

    public void clientSendName()
    {
        clientSendNameThread = new ClientSendNameThread(this,hostAddress,5555);
        Thread t = new Thread(clientSendNameThread);
        t.start();
        System.out.println("987");
    }

    @Override
    protected void onResume() {
        super.onResume();
        mReceiver = new ClientChatBroadcastReceiver(mManager, mChannel, this);
        registerReceiver(mReceiver, intentFilter);
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
