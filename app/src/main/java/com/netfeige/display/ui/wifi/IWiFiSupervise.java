package com.netfeige.display.ui.wifi;

import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

/* JADX INFO: loaded from: classes.dex */
public interface IWiFiSupervise {
    void addNetwork(WifiConfiguration wifiConfiguration);

    void closeWiFi();

    void closeWifiAp();

    void connectConfiguration(int i);

    void createWifiAp(WifiConfiguration wifiConfiguration, boolean z);

    WifiConfiguration createWifiInfo(String str, String str2, int i, String str3);

    void disconnectWifi(int i);

    String getApSSID();

    int getWifiAPState();

    WifiInfo getWifiInfo();

    WifiManager getWifiManager();

    void openWiFi();

    void searchWifiAp();

    void stopSearch();

    boolean wifiEnable();
}

