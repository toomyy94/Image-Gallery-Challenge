package challenge.tomas.pt.imagegalleryapp.network;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;

import challenge.tomas.pt.imagegalleryapp.ImageGalleryApplication;

/**
 * Manager to get Wifi network related information.
 * <p>
 * Modified by Tomás Rodrigues on 22/05/2018
 */
public class WifiNetworkManager{

    public final static int CONNECTION_STATE_COMPLETED = 3;

    private final static int CONNECTION_STATE_DISCONNECTED = 4;

    private final static int CONNECTION_STATE_AUTHENTICATING = 2;

    private final static int CONNECTION_STATE_INTERFACE_DISABLED = 9;

    private final static int CONNECTION_STATE_SCANNING = 11;

    private final static int CONNECTION_STATE_UNINITIALIZED = 12;

    private final static int CONNECTION_STATE_INVALID = 10;

    private static final int UNKNOWN = -1;

    private static WifiNetworkManager instance;

    private final ImageGalleryApplication application;

    public final WifiManager wifiManager;

    private int signalStrength = UNKNOWN;

    private int rssi = UNKNOWN;

    private int wifiState = CONNECTION_STATE_INVALID;

    private String ssid;

    private String bssid;

    private final ConnectivityManager connectivityManager;

    private boolean wifiRebootRequest;

    private WifiNetworkManager(ImageGalleryApplication application) {
        this.application = application;
        wifiManager = (WifiManager) application.getSystemService(Context.WIFI_SERVICE);
        connectivityManager = (ConnectivityManager) application.getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    /**
     * Gets the manager instance.
     *
     * @param application the application object.
     * @return the manager instance.
     */
    public synchronized static WifiNetworkManager getInstance(ImageGalleryApplication application) {
        if (instance == null) {
            instance = new WifiNetworkManager(application);
        }
        return instance;
    }

    /**
     * Initializes the manager by registering listeners that watch for network changes.
     */
    public void init() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        application.registerReceiver(wifiStateChangedReceiver, intentFilter);

        IntentFilter rssiIntentFilter = new IntentFilter();
        rssiIntentFilter.addAction(WifiManager.RSSI_CHANGED_ACTION);
        application.registerReceiver(wifiSignalReceiver, rssiIntentFilter);

        IntentFilter scanIntentFilter = new IntentFilter();
        scanIntentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        application.registerReceiver(wifiScanReceiver, scanIntentFilter);
    }

    /**
     * Finalizes the manager un registering the listeners.
     */
    public void close() {
        application.unregisterReceiver(wifiStateChangedReceiver);
        application.unregisterReceiver(wifiSignalReceiver);
        application.unregisterReceiver(wifiScanReceiver);
    }

    /**
     * Gets if the wifi network is available.
     *
     * @return if the wifi network is available.
     */
    public boolean isWifiAvailable() {
        return wifiManager.isWifiEnabled();
    }

    /**
     * Gets if the wifi network or the mobile data are available.
     *
     * @return if the wifi network OR mobile data are available.
     */
    public boolean hasConnectivity(){
        if (ssid != null)
            return true;
        else
            return false;
    }

    /**
     * Tries to restart the wifi connection.
     */
    public void restartWifi() {
        wifiRebootRequest = wifiManager.setWifiEnabled(false);
    }

    private void turnWifiOn() {
        wifiManager.setWifiEnabled(true);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private boolean isWifiConnected() {
        Network[] allNetworks;
        try {
            allNetworks = connectivityManager.getAllNetworks();
        } catch (Exception e) {
//            LOGGER.error("Error getting network list", e);
            return false;
        }

        for (Network network : allNetworks) {
            try {
                NetworkInfo networkInfo = connectivityManager.getNetworkInfo(network);
                if (networkInfo != null && networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                    return networkInfo.isConnectedOrConnecting();
                }
            } catch (Exception e) {
//                LOGGER.error("Error getting network info", e);
            }
        }

        return false;
    }

    private void disconnectingWifi() {
        wifiState = CONNECTION_STATE_DISCONNECTED;
    }

    private void disconnectedWifi() {
        wifiState = CONNECTION_STATE_DISCONNECTED;
        signalStrength = UNKNOWN;
        ssid = null;
        bssid = null;

    }

    private void connectedWifi(String ssid, int signal, String bssid) {
        wifiState = CONNECTION_STATE_COMPLETED;
        signalStrength = signal;
        this.ssid = ssid;
        this.bssid = bssid;
    }

    private void connectingWifi(String ssid) {
        wifiState = CONNECTION_STATE_AUTHENTICATING;
        this.ssid = ssid;
    }

    private void disablingWifi() {
        wifiState = CONNECTION_STATE_INTERFACE_DISABLED;
        signalStrength = UNKNOWN;
        ssid = null;
    }

    private void disabledWifi() {
        wifiState = CONNECTION_STATE_UNINITIALIZED;
        signalStrength = UNKNOWN;
        ssid = null;
    }

    private void enablingWifi() {
        wifiState = CONNECTION_STATE_SCANNING;
    }

    private void enabledWifi() {
        wifiState = CONNECTION_STATE_SCANNING;
    }

//    private void broadcastUpdate() {
//        Intent intent = new Intent(ACTION_WIFI_DATA_CHANGED);
//        application.sendBroadcast(intent);
//    }

    private BroadcastReceiver wifiSignalReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            WifiInfo info = wifiManager.getConnectionInfo();

            signalStrength = WifiManager.calculateSignalLevel(info.getRssi(), 6);
            rssi = info.getRssi();
//            broadcastUpdate();
        }
    };

    private BroadcastReceiver wifiScanReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
//            broadcastUpdate();
        }

    };

    private BroadcastReceiver wifiStateChangedReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(intent.getAction())) {
                NetworkInfo networkInfo = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                if (NetworkInfo.State.CONNECTED.equals(networkInfo.getState())) {
                    WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                    String ssid = wifiInfo.getSSID();
                    int signal = WifiManager.calculateSignalLevel(wifiInfo.getRssi(), 6);
                    connectedWifi(ssid, signal, wifiInfo.getBSSID());

                } else if (NetworkInfo.State.DISCONNECTED.equals(networkInfo.getState())) {
                    disconnectedWifi();

                } else if (NetworkInfo.State.DISCONNECTING.equals(networkInfo.getState())) {
                    disconnectingWifi();

                } else if (NetworkInfo.State.CONNECTING.equals(networkInfo.getState())) {
                    WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                    String ssid = wifiInfo.getSSID();
                    connectingWifi(ssid);
                }
//                broadcastUpdate();
                return;
            }

            int extraWifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_UNKNOWN);
            switch (extraWifiState) {
                case WifiManager.WIFI_STATE_DISABLED:
                    disabledWifi();
                    if (wifiRebootRequest) {
                        turnWifiOn();
                    }
                    break;
                case WifiManager.WIFI_STATE_DISABLING:
                    disablingWifi();
                    break;
                case WifiManager.WIFI_STATE_ENABLED:
                    enabledWifi();
                    break;
                case WifiManager.WIFI_STATE_ENABLING:
                    enablingWifi();
                    break;
                case WifiManager.WIFI_STATE_UNKNOWN:
                    disabledWifi();
                    break;
            }

//            broadcastUpdate();
        }
    };
}
