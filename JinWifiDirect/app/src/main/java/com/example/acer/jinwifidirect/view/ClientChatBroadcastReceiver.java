package com.example.acer.jinwifidirect.view;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.widget.Toast;

public class ClientChatBroadcastReceiver extends BroadcastReceiver
{
    private WifiP2pManager mManager;
    private WifiP2pManager.Channel mChannel;
    private ClientChatActivity mActivity;

    public ClientChatBroadcastReceiver(){}

    public ClientChatBroadcastReceiver(WifiP2pManager manager, WifiP2pManager.Channel channel,ClientChatActivity activity) {
        super();
        mManager = manager;
        mChannel = channel;
        mActivity = activity;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) { //check if wifi direct is on
            // UI update to indicate wifi p2p status.
            int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
            if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
                // Wifi Direct mode is enabled
                mActivity.clientSend(true);
                mActivity.setAdded(false);
                mActivity.quitBtn(false);
            } else {
                mActivity.clientSend(false);
                mActivity.setAdded(true);
                mActivity.quitBtn(true);

            }

        } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {



        } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {


            if (mManager == null) { //if no manager thn do nth
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

                                } else if (info.groupFormed) {
                                    mActivity.setHostAddress(info.groupOwnerAddress.getHostAddress());
                                    mActivity.setTime();
                                    mActivity.setAdded(false);
                                    mActivity.quitBtn(false);
                                    mActivity.clientStartReceivingMessage();
                                    mActivity.clientSendName();
                                    mActivity.receiveProfile();
                                }
                            }
                        });
            }
            else
            {
                Toast.makeText(mActivity, "Disconnected from the chat", Toast.LENGTH_LONG).show();
                mManager.removeGroup(mChannel, null);
                mActivity.clientForceDisconnect();
                mActivity.clientSend(false);
                mActivity.setAdded(false);
            }

        } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {


        }
    }
}
