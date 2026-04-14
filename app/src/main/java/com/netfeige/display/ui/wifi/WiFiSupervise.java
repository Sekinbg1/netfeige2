package com.netfeige.display.ui.wifi;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import com.netfeige.common.Public_MsgID;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class WiFiSupervise implements IWiFiSupervise {
    private static IWiFiSupervise m_iWiFiSupervise;
    private List<WifiConfiguration> m_wifiConfiguration;
    private WifiInfo m_wifiInfo;
    private List<ScanResult> m_wifiList;
    private WifiManager m_wifiManager;

    @Override // com.netfeige.display.ui.wifi.IWiFiSupervise
    public void stopSearch() {
    }

    public WiFiSupervise(Context context) {
        WifiManager wifiManager = (WifiManager) context.getSystemService("wifi");
        this.m_wifiManager = wifiManager;
        this.m_wifiInfo = wifiManager.getConnectionInfo();
    }

    public static IWiFiSupervise getInstance(Context context) {
        if (m_iWiFiSupervise == null) {
            m_iWiFiSupervise = new WiFiSupervise(context);
        }
        return m_iWiFiSupervise;
    }

    @Override // com.netfeige.display.ui.wifi.IWiFiSupervise
    public void openWiFi() {
        if (this.m_wifiManager.isWifiEnabled()) {
            return;
        }
        this.m_wifiManager.setWifiEnabled(true);
    }

    @Override // com.netfeige.display.ui.wifi.IWiFiSupervise
    public void closeWiFi() {
        if (this.m_wifiManager.isWifiEnabled()) {
            this.m_wifiManager.setWifiEnabled(false);
        }
    }

    @Override // com.netfeige.display.ui.wifi.IWiFiSupervise
    public void createWifiAp(WifiConfiguration wifiConfiguration, boolean z) {
        try {
            this.m_wifiManager.getClass().getMethod("setWifiApEnabled", WifiConfiguration.class, Boolean.TYPE).invoke(this.m_wifiManager, wifiConfiguration, Boolean.valueOf(z));
        } catch (Exception unused) {
        }
    }

    @Override // com.netfeige.display.ui.wifi.IWiFiSupervise
    public void closeWifiAp() {
        if (3 == getWifiAPState() || 13 == getWifiAPState()) {
            try {
                Method method = this.m_wifiManager.getClass().getMethod("getWifiApConfiguration", new Class[0]);
                method.setAccessible(true);
                this.m_wifiManager.getClass().getMethod("setWifiApEnabled", WifiConfiguration.class, Boolean.TYPE).invoke(this.m_wifiManager, (WifiConfiguration) method.invoke(this.m_wifiManager, new Object[0]), false);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (IllegalArgumentException e2) {
                e2.printStackTrace();
            } catch (NoSuchMethodException e3) {
                e3.printStackTrace();
            } catch (InvocationTargetException e4) {
                e4.printStackTrace();
            }
        }
    }

    @Override // com.netfeige.display.ui.wifi.IWiFiSupervise
    public void searchWifiAp() {
        this.m_wifiManager.startScan();
    }

    public List<WifiConfiguration> getConfiguration() {
        return this.m_wifiConfiguration;
    }

    @Override // com.netfeige.display.ui.wifi.IWiFiSupervise
    public void connectConfiguration(int i) {
        if (i > this.m_wifiConfiguration.size()) {
            return;
        }
        this.m_wifiManager.enableNetwork(this.m_wifiConfiguration.get(i).networkId, true);
    }

    @Override // com.netfeige.display.ui.wifi.IWiFiSupervise
    public void disconnectWifi(int i) {
        this.m_wifiManager.disableNetwork(i);
    }

    @Override // com.netfeige.display.ui.wifi.IWiFiSupervise
    public int getWifiAPState() {
        try {
            return ((Integer) this.m_wifiManager.getClass().getMethod("getWifiApState", new Class[0]).invoke(this.m_wifiManager, new Object[0])).intValue();
        } catch (Exception unused) {
            return 4;
        }
    }

    @Override // com.netfeige.display.ui.wifi.IWiFiSupervise
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
            this.m_wifiManager.removeNetwork(wifiConfigurationIsExsits.networkId);
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
        try {
            for (WifiConfiguration wifiConfiguration : this.m_wifiManager.getConfiguredNetworks()) {
                if (wifiConfiguration.SSID.equals("\"" + str + "\"")) {
                    return wifiConfiguration;
                }
            }
            return null;
        } catch (NullPointerException e) {
            e.printStackTrace();
            return null;
        } catch (Exception e2) {
            e2.printStackTrace();
            return null;
        }
    }

    @Override // com.netfeige.display.ui.wifi.IWiFiSupervise
    public String getApSSID() {
        Object objInvoke;
        String str = null;
        try {
            Method declaredMethod = this.m_wifiManager.getClass().getDeclaredMethod("getWifiApConfiguration", new Class[0]);
            if (declaredMethod != null && (objInvoke = declaredMethod.invoke(this.m_wifiManager, new Object[0])) != null) {
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

    @Override // com.netfeige.display.ui.wifi.IWiFiSupervise
    public boolean wifiEnable() {
        return this.m_wifiManager.isWifiEnabled();
    }

    @Override // com.netfeige.display.ui.wifi.IWiFiSupervise
    public WifiManager getWifiManager() {
        return this.m_wifiManager;
    }

    @Override // com.netfeige.display.ui.wifi.IWiFiSupervise
    public WifiInfo getWifiInfo() {
        return this.m_wifiManager.getConnectionInfo();
    }

    @Override // com.netfeige.display.ui.wifi.IWiFiSupervise
    public void addNetwork(WifiConfiguration wifiConfiguration) {
        this.m_wifiManager.enableNetwork(this.m_wifiManager.addNetwork(wifiConfiguration), true);
    }

    public List<ScanResult> getWifiList() {
        return this.m_wifiList;
    }

    public void setWifiList() {
        this.m_wifiList = this.m_wifiManager.getScanResults();
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
            sb.append(this.m_wifiList.get(i).toString());
            sb.append("/n");
            i = i2;
        }
        return sb;
    }

    public String getMacAddress() {
        WifiInfo wifiInfo = this.m_wifiInfo;
        return wifiInfo == null ? "NULL" : wifiInfo.getMacAddress();
    }

    public String getBSSID() {
        WifiInfo wifiInfo = this.m_wifiInfo;
        return wifiInfo == null ? "NULL" : wifiInfo.getBSSID();
    }

    public int getIPAddress() {
        WifiInfo wifiInfo = this.m_wifiInfo;
        if (wifiInfo == null) {
            return 0;
        }
        return wifiInfo.getIpAddress();
    }

    public int getNetworkId() {
        WifiInfo wifiInfo = this.m_wifiInfo;
        if (wifiInfo == null) {
            return 0;
        }
        return wifiInfo.getNetworkId();
    }
}

