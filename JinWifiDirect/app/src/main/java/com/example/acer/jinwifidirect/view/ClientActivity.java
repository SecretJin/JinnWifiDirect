package com.example.acer.jinwifidirect.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceRequest;
import android.os.Handler;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.acer.jinwifidirect.R;

import java.lang.reflect.InvocationTargetException;
import java.util.Random;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClientActivity extends Activity {

    private WifiP2pManager mManager;
    private WifiP2pManager.Channel mChannel;
    private final IntentFilter intentFilter = new IntentFilter();
    private BroadcastReceiver mReceiver = null;
    private WifiP2pDnsSdServiceRequest serviceRequest;
    private HashMap<String, String> buddies = new HashMap<String, String>();
    //An intent filter declares  what an activity or service can do and what types of broadcasts a receiver can handle.
    private List<WifiP2pDevice> deviceList = new ArrayList<>();
    private ArrayAdapter adapter;
    private String hostAddress;
    private int number;
    private ListView listView;
    private AlertDialog alert;
    private AlertDialog alert1;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client);
        mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mManager.initialize(this, getMainLooper(), null);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
        deletePersistentGroups(mManager,mChannel);
        adapter = new ArrayAdapter<WifiP2pDevice>(this,android.R.layout.simple_list_item_1, deviceList);
        listView = (ListView)findViewById(R.id.availableRoomList);
        listView.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.client, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.guideBtn:
                Intent i = new Intent(ClientActivity.this, GuideActivity.class);
                startActivity(i);
                break;
            case R.id.editBtn:
                Intent a = new Intent(ClientActivity.this, EditProfileActivity.class);
                startActivity(a);
                break;
            case R.id.homeBtn:
                Intent b = new Intent(ClientActivity.this, MenuSelectionActivity.class);
                startActivity(b);
                break;
            case R.id.friendBtn:
                Intent c = new Intent(ClientActivity.this, ProfileListActivity.class);
                startActivity(c);
                break;
            case R.id.profileBtn:
                Intent d = new Intent(ClientActivity.this, ProfileActivity.class);
                startActivity(d);
                break;
        }
        return true;
    }

    public void searchPeer(View v)
    {
        deviceList.clear();
        adapter.notifyDataSetChanged();
        mManager.clearServiceRequests(mChannel,null);
        discoverService();
    }

    private void setFirstItem() {
        Random rand = new Random();
        number = rand.nextInt((listView.getAdapter().getCount()- 0) + listView.getAdapter().getCount()) + 0;
        System.out.println((listView.getAdapter().getCount()-1));
        listView.setSelection(number);
        il.onItemSelected(listView, listView.getChildAt(number), listView.getSelectedItemPosition(),
                listView.getSelectedItemId());
    }

    private AdapterView.OnItemSelectedListener il = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            final WifiP2pConfig config = new WifiP2pConfig();
            config.deviceAddress = deviceList.get(position+1).deviceAddress;
            System.out.println(position);
            config.wps.setup = WpsInfo.PBC;
            config.groupOwnerIntent = 0 ;
            //Toast.makeText(MainActivity.this, " selected item position " + position, Toast.LENGTH_LONG).show();
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mManager.connect(mChannel, config, new WifiP2pManager.ActionListener() {
                        @Override
                        public void onSuccess() {
                            //clear the list
                            deviceList.clear();
                            adapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onFailure(int reason) {
                            Toast.makeText(ClientActivity.this, "Connect failed,please REFRESH.", Toast.LENGTH_SHORT).show();
                        }

                    });
                }
            }, 10);

        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
            // TODO Auto-generated method stub
        }

    };

    @Override
    public void onStart()
    {
        super.onStart();
        deviceList.clear();
        mManager.clearServiceRequests(mChannel,null);
        mManager.removeGroup(mChannel, null);
    }

    private void discoverService() {
        /*
         * Register listeners for DNS-SD services. These are callbacks invoked
         * by the system when a service is actually discovered.
         */

        WifiP2pManager.DnsSdServiceResponseListener servListener = new WifiP2pManager.DnsSdServiceResponseListener() {
            @Override
            public void onDnsSdServiceAvailable(String instanceName,
                                                String registrationType, WifiP2pDevice srcDevice) {

                // A service has been discovered. Is this our app?
                if (instanceName.equalsIgnoreCase("_test")) {

                    // Update the device name with the human-friendly version from
                    // the DnsTxtRecord, assuming one arrived
                    srcDevice.deviceName = buddies
                            .containsKey(srcDevice.deviceAddress) ? buddies
                            .get(srcDevice.deviceAddress) : srcDevice.deviceName;

                    // update the UI and add the item the discovered
                    // device.
                    deviceList.add(srcDevice);
                    adapter.notifyDataSetChanged();
                }
            }
        };

        WifiP2pManager.DnsSdTxtRecordListener txtListener = new WifiP2pManager.DnsSdTxtRecordListener() {
            /**
             * A new TXT record is available. Pick up the advertised
             * buddy name.
             */
            @Override
            public void onDnsSdTxtRecordAvailable(
                    String fullDomainName, Map<String, String> record, WifiP2pDevice device) {

                Log.d("client", "DnsSdTxtRecord available -" + record.toString());
                buddies.put(device.deviceAddress, record.get("buddyname"));

            }
        };

        mManager.setDnsSdResponseListeners(mChannel, servListener, txtListener);

        mManager.clearServiceRequests(mChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onFailure(int arg0) {
            }
        });

        serviceRequest = WifiP2pDnsSdServiceRequest.newInstance();
        mManager.addServiceRequest(mChannel, serviceRequest, new WifiP2pManager.ActionListener() {

            @Override
            public void onSuccess() {
                mManager.discoverServices(mChannel, new WifiP2pManager.ActionListener() {

                    @Override
                    public void onSuccess() {
                        dialog = ProgressDialog.show(ClientActivity.this, "","Searching Room", true);
                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if(listView.getAdapter().getCount()==0)
                                {
                                    dialog.dismiss();
                                    AlertDialog.Builder builder = new AlertDialog.Builder(ClientActivity.this);
                                    builder.setMessage("No Available Room!")
                                            .setTitle("Search Room")
                                            .setCancelable(false)
                                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                    //do things
                                                }
                                            });
                                    alert = builder.create();
                                    alert.show();
                                }

                                else
                                {
                                    dialog.dismiss();
                                    setFirstItem();
                                }
                            }

                        }, 7000);
                    }

                    @Override
                    public void onFailure(int arg0) {
                        AlertDialog.Builder builder1 = new AlertDialog.Builder(ClientActivity.this);
                        builder1.setMessage("Fail to search, Please refresh")
                                .setTitle("Search Room")
                                .setCancelable(false)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        //do things
                                    }
                                });
                        alert1 = builder1.create();
                        alert1.show();
                    }
                });
            }

            @Override
            public void onFailure(int arg0) {
                Toast.makeText(ClientActivity.this, "Failed adding service discovery request", Toast.LENGTH_SHORT).show();
            }
        });

    }


    public void setHostAddress(String address)
    {
        hostAddress = address;
    }

    public void setDiscoverButton (boolean b) {
        Button btn = (Button)findViewById(R.id.searchBtn);
        btn.setEnabled(b);
    }

    public void disconnectChat (View v)
    {
        deviceList.clear();
        adapter.notifyDataSetChanged();
        final ProgressDialog dialog1 = ProgressDialog.show(ClientActivity.this, "","Refreshing", true);
        final WifiManager wifiManager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
        wifiManager.setWifiEnabled(false);
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                wifiManager.setWifiEnabled(true);
                dialog1.dismiss();
            }

        }, 7000);
    }


    public void clientBack(View v)
    {
        Intent i = new Intent(ClientActivity.this, MenuSelectionActivity.class);
        startActivity(i);
    }

    public void toClientChat()
    {
        Intent i = new Intent(ClientActivity.this,ClientChatActivity.class);
        startActivity(i);
    }
    public void updateList()
    {
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mReceiver = new ClientBroadcastReceiver(mManager, mChannel, this, deviceList);
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
    }

}
