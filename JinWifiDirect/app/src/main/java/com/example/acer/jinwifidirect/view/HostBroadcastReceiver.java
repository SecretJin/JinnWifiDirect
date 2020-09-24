package com.example.acer.jinwifidirect.view;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import java.util.List;

/**
 * Created by Acer on 6/3/2017.
 */
public class HostBroadcastReceiver extends BroadcastReceiver
{
    private WifiP2pManager mManager;
    private Channel mChannel;
    private HostActivity mActivity;
    private List<WifiP2pDevice> deviceList;

    public HostBroadcastReceiver(){}

    public HostBroadcastReceiver(WifiP2pManager manager, Channel channel,
                                 HostActivity activity, List<WifiP2pDevice> list) {
        super();
        mManager = manager;
        mChannel = channel;
        mActivity = activity;
        deviceList = list;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if (WifiP2pManager.WIFI_P2P_DISCOVERY_CHANGED_ACTION.equals(action)) {
            int discoveryState = intent.getIntExtra(WifiP2pManager.EXTRA_DISCOVERY_STATE, -1);
            if (discoveryState == WifiP2pManager.WIFI_P2P_DISCOVERY_STOPPED) {
                mManager.discoverPeers(mChannel, null);
            }
        }
        //Check to see if Wi-Fi P2P is on and supported
        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
            int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
            if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
                // Wifi Direct is enabled
                mActivity.setDiscoverButton(true);
            }
            else {
                // Wi-Fi Direct is not enabled
                mActivity.setDiscoverButton(false);
            }
        }



        else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
            // Call WifiP2pManager.requestPeers() to get a list of current peers
            //check if list changed or got peers

            // request available peers from the wifi p2p manager. This is an
            // asynchronous call and the calling activity is notified with a
            // callback on PeerListListener.onPeersAvailable()

            /** this is where you display all ur list of available peers **/
            if (mManager != null) {
                mManager.requestPeers(mChannel, new WifiP2pManager.PeerListListener() {
                    public void onPeersAvailable(WifiP2pDeviceList peers) {
                        //execute here
                        deviceList.clear();
                        for(WifiP2pDevice peer : peers.getDeviceList())
                        {
                            if(peer.status == WifiP2pDevice.CONNECTED)
                            {

                            }
                        }

                        mActivity.updateList();
                    }
                });
            }
        }
        else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
            // Respond to new connection or disconnections
            if (mManager == null) {
                return;
            }
            //extract network info
            NetworkInfo networkInfo = (NetworkInfo) intent
                    .getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);
            //check if we are connected
            if (networkInfo.isConnected())
            {

                mManager.requestConnectionInfo(mChannel,
                        new WifiP2pManager.ConnectionInfoListener() {
                            public void onConnectionInfoAvailable(final WifiP2pInfo info) {

                                // If the connection is established
                                // If it is server
                                if (info.groupFormed && info.isGroupOwner)
                                {
                                    mActivity.toHostChat();
                                    System.out.println("HOST");
                                }
                                //If it is client
                                else if (info.groupFormed)
                                {
                                    System.out.println("CLIENT");
                                }

                            }
                        });
            }
        }
        else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
            // Respond to this device's wifi state changing
        }
    }
}
