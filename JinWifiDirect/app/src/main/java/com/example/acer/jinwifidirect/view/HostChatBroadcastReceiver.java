package com.example.acer.jinwifidirect.view;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.widget.Toast;

public class HostChatBroadcastReceiver extends BroadcastReceiver
{
    private WifiP2pManager mManager;
    private WifiP2pManager.Channel mChannel;
    private HostChatActivity mActivity;

    public HostChatBroadcastReceiver(){}

    public HostChatBroadcastReceiver(WifiP2pManager manager, WifiP2pManager.Channel channel,
                                 HostChatActivity activity) {
        super();
        mManager = manager;
        mChannel = channel;
        mActivity = activity;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        //Check to see if Wi-Fi P2P is on and supported
        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
            int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
            if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
                // Wifi Direct is enabled
                mActivity.hostSend(true);
                mActivity.quitBtn(false);
                mActivity.setAdded(false);
            }
            else {
                // Wi-Fi Direct is not enabled
                mActivity.hostSend(false);
                mActivity.setAdded(true);
                mActivity.quitBtn(true);
            }
        }
        else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
            // Call WifiP2pManager.requestPeers() to get a list of current peers
            //check if list changed or got peers

            // request available peers from the wifi p2p manager. This is an
            // asynchronous call and the calling activity is notified with a
            // callback on PeerListListener.onPeersAvailable()

            /** this is where you display all ur list of available peers **/

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
                                if (info.groupFormed && info.isGroupOwner) {
                                    mActivity.quitBtn(false);
                                    mActivity.setAdded(false);
                                    mActivity.saveHProfile();
                                    mActivity.serverReceivingMessage();
                                    mActivity.serverSendingMessage();
                                    mActivity.serverProfileName();
                                    mActivity.receiveProfileThread();
                                    mActivity.setTime();

                                } else if (info.groupFormed) {

                                }
                            }
                        });
            }
            else
            {
                Toast.makeText(mActivity, "Disconnected from the chat", Toast.LENGTH_LONG).show();
                mManager.removeGroup(mChannel,null);
                mActivity.setAdded(false);
                mActivity.hostSend(false);
                mActivity.hostForceDisconnect();
            }
        }
        else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
            // Respond to this device's wifi state changing
        }
    }
}
