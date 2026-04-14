package com.netfeige.dlna;

import android.net.wifi.WifiManager;
import org.teleal.cling.android.AndroidUpnpServiceConfiguration;
import org.teleal.cling.android.AndroidUpnpServiceImpl;
import org.teleal.cling.model.types.ServiceType;
import org.teleal.cling.model.types.UDAServiceType;

/* JADX INFO: loaded from: classes.dex */
public class IpMsgUPnPService extends AndroidUpnpServiceImpl {
    @Override // org.teleal.cling.android.AndroidUpnpServiceImpl
    protected AndroidUpnpServiceConfiguration createConfiguration(WifiManager wifiManager) {
        return new AndroidUpnpServiceConfiguration(wifiManager) { // from class: com.netfeige.dlna.IpMsgUPnPService.1
            @Override // org.teleal.cling.DefaultUpnpServiceConfiguration, org.teleal.cling.UpnpServiceConfiguration
            public ServiceType[] getExclusiveServiceTypes() {
                return new ServiceType[]{new UDAServiceType("AVTransport")};
            }
        };
    }
}

