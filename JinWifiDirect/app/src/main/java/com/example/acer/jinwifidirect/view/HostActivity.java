package com.example.acer.jinwifidirect.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.acer.jinwifidirect.R;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HostActivity extends Activity {

    private WifiP2pManager mManager;
    private WifiP2pManager.Channel mChannel;
    private final IntentFilter intentFilter = new IntentFilter();
    private BroadcastReceiver mReceiver = null;
    //An intent filter declares  what an activity or service can do and what types of broadcasts a receiver can handle.
    private List<WifiP2pDevice> deviceList = new ArrayList<>();
    private ArrayAdapter adapter;
    private WifiP2pDnsSdServiceInfo service;
    private AlertDialog alert;
    private ProgressDialog dialog1;
    private ImageView imageView;
    private TextView textView;
    private boolean clicked;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host);
        textView = (TextView) findViewById(R.id.textView25);
        //Get an instance of the WifiP2pManager, and call its initialize() method.
        // This method returns a WifiP2pManager.Channel object, which you'll use later to connect your app to the Wi-Fi P2P framework
        mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mManager.initialize(this, getMainLooper(), null);

        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
        deletePersistentGroups(mManager,mChannel);
        ListView listView = (ListView)findViewById(R.id.listView);
        adapter = new ArrayAdapter<WifiP2pDevice>(this,android.R.layout.simple_list_item_1, deviceList);
        listView.setAdapter(adapter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.host, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.guideBtn:
                mManager.clearLocalServices(mChannel, null);
                Intent i = new Intent(HostActivity.this, GuideActivity.class);
                startActivity(i);
                break;
            case R.id.editBtn:
                mManager.clearLocalServices(mChannel, null);
                Intent a = new Intent(HostActivity.this, EditProfileActivity.class);
                startActivity(a);
                break;
            case R.id.homeBtn:
                mManager.clearLocalServices(mChannel, null);
                mManager.cancelConnect(mChannel,null);
                Intent b = new Intent(HostActivity.this, MenuSelectionActivity.class);
                startActivity(b);
                break;
            case R.id.friendBtn:
                mManager.clearLocalServices(mChannel, null);
                Intent c = new Intent(HostActivity.this, ProfileListActivity.class);
                startActivity(c);
                break;
            case R.id.profileBtn:
                mManager.clearLocalServices(mChannel, null);
                Intent d = new Intent(HostActivity.this, ProfileActivity.class);
                startActivity(d);
                break;
        }
        return true;
    }

    private void startRegistration() {
        Map<String, String> record = new HashMap<String, String>();
        record.put("available", "visible");
        record.put("buddyname", "Annoymous Room." + (int) (Math.random() * 1000));
        service = WifiP2pDnsSdServiceInfo.newInstance("_test", "_presence._tcp", record);
        mManager.clearLocalServices(mChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {}

            @Override
            public void onFailure(int code) {
                Toast.makeText(HostActivity.this, "Fail to Clear All Local Service", Toast.LENGTH_SHORT).show();
            }

        });

        mManager.addLocalService(mChannel, service, new WifiP2pManager.ActionListener() {

            @Override
            public void onSuccess()
            {
                clicked=true;
                AlertDialog.Builder builder = new AlertDialog.Builder(HostActivity.this);
                builder.setTitle("Room Creation");
                builder.setMessage("Your room is created")
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                            }
                        });
                alert = builder.create();
                alert.show();
            }

            @Override
            public void onFailure(int error) {
                Toast.makeText(HostActivity.this, "Failed to create room", Toast.LENGTH_SHORT).show();
            }
        });

        //nid this 1 if not cannot be discovered
        mManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {}

            @Override
            public void onFailure(int error) {}
        });
    }

    public void hostBack(View v)
    {
        mManager.clearLocalServices(mChannel, null);
        Intent i = new Intent(HostActivity.this, MenuSelectionActivity.class);
        startActivity(i);
    }

    public void searchPeer(View v)
    {
        startRegistration();
        textView.setText("You created a room, Please wait for other people to join your room");
    }

    @Override
    public void onStart()
    {
        super.onStart();
        mManager.removeGroup(mChannel,null);
        clicked=false;
        textView.setText("You haven't create a room yet");
    }

    public void updateList()
    {
        adapter.notifyDataSetChanged();
    }
    /* register the broadcast receiver with the intent values to be matched */
    //Register the intent filter and broadcast receiver when your main activity is active, and unregister them when the activity is paused.
    @Override
    protected void onResume() {
        super.onResume();
        mReceiver = new HostBroadcastReceiver(mManager, mChannel, this, deviceList);
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

    public void toHostChat()
    {
        Intent i = new Intent(HostActivity.this, HostChatActivity.class);
        startActivity(i);
        mManager.removeLocalService(mChannel, service,null);
    }

    public void disconnectChat (View v)
    {
        mManager.removeGroup(mChannel, new WifiP2pManager.ActionListener() {

            @Override
            public void onSuccess() {}

            @Override
            public void onFailure(int reasonCode) {}
        });
    }

    public void setDiscoverButton (boolean b) {
        Button btn = (Button)findViewById(R.id.discoverBtn);
        btn.setEnabled(b);
    }

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
    @Override
    public void onBackPressed() {}

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        mManager.removeGroup(mChannel,null);
        if(clicked)
        {
            mManager.removeLocalService(mChannel, service,null);
        }
    }

}
