package com.netfeige.wt;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import com.netfeige.common.Public_MsgID;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class WifiAdmin {
    private static WifiAdmin wiFiAdmin;
    private List<WifiConfiguration> mWifiConfiguration;
    private WifiInfo mWifiInfo;
    private List<ScanResult> mWifiList;
    WifiManager.WifiLock mWifiLock;
    public WifiManager mWifiManager;

    public WifiAdmin(Context context) {
        WifiManager wifiManager = (WifiManager) context.getSystemService("wifi");
        this.mWifiManager = wifiManager;
        this.mWifiInfo = wifiManager.getConnectionInfo();
    }

    public static WifiAdmin getInstance(Context context) {
        if (wiFiAdmin == null) {
            wiFiAdmin = new WifiAdmin(context);
        }
        return wiFiAdmin;
    }

    public void OpenWifi() {
        if (this.mWifiManager.isWifiEnabled()) {
            return;
        }
        this.mWifiManager.setWifiEnabled(true);
    }

    public void closeWifi() {
        this.mWifiManager.setWifiEnabled(false);
    }

    public void AcquireWifiLock() {
        this.mWifiLock.acquire();
    }

    public void ReleaseWifiLock() {
        if (this.mWifiLock.isHeld()) {
            this.mWifiLock.acquire();
        }
    }

    public void CreatWifiLock() {
        this.mWifiLock = this.mWifiManager.createWifiLock("Test");
    }

    public List<WifiConfiguration> getConfiguration() {
        return this.mWifiConfiguration;
    }

    public void connectConfiguration(int i) {
        if (i > this.mWifiConfiguration.size()) {
            return;
        }
        this.mWifiManager.enableNetwork(this.mWifiConfiguration.get(i).networkId, true);
    }

    public void startScan() {
        this.mWifiManager.startScan();
    }

    public List<ScanResult> getWifiList() {
        return this.mWifiList;
    }

    public void setWifiList() {
        this.mWifiList = this.mWifiManager.getScanResults();
    }

    public StringBuilder lookUpScan() {
        StringBuilder sb = new StringBuilder();
        int i = 0;
        while (i < 2) {
            StringBuilder sb2 = new StringBuilder();
            sb2.append("Index_");
            int i2 = i + 1;
            sb2.append(new Integer(i2).toString());
            sb2.append(Public_MsgID.PRO_SPACE);
            sb.append(sb2.toString());
            sb.append(this.mWifiList.get(i).toString());
            sb.append("/n");
            i = i2;
        }
        return sb;
    }

    public String getMacAddress() {
        WifiInfo wifiInfo = this.mWifiInfo;
        return wifiInfo == null ? "NULL" : wifiInfo.getMacAddress();
    }

    public String getBSSID() {
        WifiInfo wifiInfo = this.mWifiInfo;
        return wifiInfo == null ? "NULL" : wifiInfo.getBSSID();
    }

    public int getIPAddress() {
        WifiInfo wifiInfo = this.mWifiInfo;
        if (wifiInfo == null) {
            return 0;
        }
        return wifiInfo.getIpAddress();
    }

    public int getNetworkId() {
        WifiInfo wifiInfo = this.mWifiInfo;
        if (wifiInfo == null) {
            return 0;
        }
        return wifiInfo.getNetworkId();
    }

    public WifiInfo getWifiInfo() {
        return this.mWifiManager.getConnectionInfo();
    }

    public void addNetwork(WifiConfiguration wifiConfiguration) {
        this.mWifiManager.enableNetwork(this.mWifiManager.addNetwork(wifiConfiguration), true);
    }

    public void disconnectWifi(int i) {
        this.mWifiManager.disableNetwork(i);
    }

    public WifiConfiguration createWifiInfo(String str, String str2, int i, String str3) {
        WifiConfiguration wifiConfiguration = new WifiConfiguration();
        wifiConfiguration.allowedAuthAlgorithms.clear();
        wifiConfiguration.allowedGroupCiphers.clear();
        wifiConfiguration.allowedKeyManagement.clear();
        wifiConfiguration.allowedPairwiseCiphers.clear();
        wifiConfiguration.allowedProtocols.clear();
        if (str3.equals("wt")) {
            wifiConfiguration.SSID = "\"" + str + "\"";
        } else {
            wifiConfiguration.SSID = str;
        }
        WifiConfiguration wifiConfigurationIsExsits = isExsits(str);
        if (wifiConfigurationIsExsits != null) {
            this.mWifiManager.removeNetwork(wifiConfigurationIsExsits.networkId);
        }
        if (i == 1) {
            wifiConfiguration.wepKeys[0] = "";
            wifiConfiguration.allowedKeyManagement.set(0);
            wifiConfiguration.wepTxKeyIndex = 0;
        }
        if (i == 2) {
            wifiConfiguration.hiddenSSID = true;
            if (str3.equals("wt")) {
                wifiConfiguration.wepKeys[0] = "\"" + str2 + "\"";
            } else {
                wifiConfiguration.wepKeys[0] = str2;
            }
            wifiConfiguration.allowedAuthAlgorithms.set(1);
            wifiConfiguration.allowedGroupCiphers.set(3);
            wifiConfiguration.allowedGroupCiphers.set(2);
            wifiConfiguration.allowedGroupCiphers.set(0);
            wifiConfiguration.allowedGroupCiphers.set(1);
            wifiConfiguration.allowedKeyManagement.set(0);
            wifiConfiguration.wepTxKeyIndex = 0;
        }
        if (i == 3) {
            if (str3.equals("wt")) {
                wifiConfiguration.preSharedKey = "\"" + str2 + "\"";
                wifiConfiguration.hiddenSSID = true;
                wifiConfiguration.allowedAuthAlgorithms.set(0);
                wifiConfiguration.allowedGroupCiphers.set(2);
                wifiConfiguration.allowedKeyManagement.set(1);
                wifiConfiguration.allowedPairwiseCiphers.set(1);
                wifiConfiguration.allowedGroupCiphers.set(3);
                wifiConfiguration.allowedPairwiseCiphers.set(2);
                wifiConfiguration.status = 2;
            } else {
                wifiConfiguration.preSharedKey = str2;
                wifiConfiguration.allowedAuthAlgorithms.set(0);
                wifiConfiguration.allowedProtocols.set(1);
                wifiConfiguration.allowedProtocols.set(0);
                wifiConfiguration.allowedKeyManagement.set(1);
                wifiConfiguration.allowedPairwiseCiphers.set(2);
                wifiConfiguration.allowedPairwiseCiphers.set(1);
            }
        }
        return wifiConfiguration;
    }

    private WifiConfiguration isExsits(String str) {
        for (WifiConfiguration wifiConfiguration : this.mWifiManager.getConfiguredNetworks()) {
            if (wifiConfiguration.SSID.equals("\"" + str + "\"")) {
                return wifiConfiguration;
            }
        }
        return null;
    }

    public void createWiFiAP(WifiConfiguration wifiConfiguration, boolean z) {
        try {
            this.mWifiManager.getClass().getMethod("setWifiApEnabled", WifiConfiguration.class, Boolean.TYPE).invoke(this.mWifiManager, wifiConfiguration, Boolean.valueOf(z));
        } catch (Exception unused) {
        }
    }

    public int getWifiApState() {
        try {
            return ((Integer) this.mWifiManager.getClass().getMethod("getWifiApState", new Class[0]).invoke(this.mWifiManager, new Object[0])).intValue();
        } catch (Exception unused) {
            return 4;
        }
    }

    public String getApSSID() {
        Object objInvoke;
        String str = null;
        try {
            Method declaredMethod = this.mWifiManager.getClass().getDeclaredMethod("getWifiApConfiguration", new Class[0]);
            if (declaredMethod != null && (objInvoke = declaredMethod.invoke(this.mWifiManager, new Object[0])) != null) {
                WifiConfiguration wifiConfiguration = (WifiConfiguration) objInvoke;
                if (wifiConfiguration.SSID != null) {
                    str = wifiConfiguration.SSID;
                } else {
                    Field declaredField = WifiConfiguration.class.getDeclaredField("mWifiApProfile");
                    if (declaredField != null) {
                        declaredField.setAccessible(true);
                        Object obj = declaredField.get(wifiConfiguration);
                        declaredField.setAccessible(false);
                        if (obj != null) {
                            Field declaredField2 = obj.getClass().getDeclaredField("SSID");
                            declaredField2.setAccessible(true);
                            Object obj2 = declaredField2.get(obj);
                            if (obj2 != null) {
                                declaredField2.setAccessible(false);
                                str = (String) obj2;
                            }
                        }
                    }
                }
            }
        } catch (Exception unused) {
        }
        return str;
    }
}

