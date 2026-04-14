package org.teleal.cling.android;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import org.teleal.cling.UpnpService;
import org.teleal.cling.UpnpServiceConfiguration;
import org.teleal.cling.UpnpServiceImpl;
import org.teleal.cling.controlpoint.ControlPoint;
import org.teleal.cling.model.ModelUtil;
import org.teleal.cling.protocol.ProtocolFactory;
import org.teleal.cling.registry.Registry;
import org.teleal.cling.registry.RegistryListener;
import org.teleal.cling.transport.Router;

/* JADX INFO: loaded from: classes.dex */
public class AndroidUpnpServiceImpl extends Service {
    protected Binder binder = new Binder();
    protected UpnpService upnpService;

    protected boolean isListeningForConnectivityChanges() {
        return true;
    }

    @Override // android.app.Service
    public void onCreate() {
        super.onCreate();
        final WifiManager wifiManager = (WifiManager) getSystemService("wifi");
        final ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService("connectivity");
        this.upnpService = new UpnpServiceImpl(createConfiguration(wifiManager), new RegistryListener[0]) { // from class: org.teleal.cling.android.AndroidUpnpServiceImpl.1
            @Override // org.teleal.cling.UpnpServiceImpl
            protected Router createRouter(ProtocolFactory protocolFactory, Registry registry) {
                AndroidWifiSwitchableRouter androidWifiSwitchableRouterCreateRouter = AndroidUpnpServiceImpl.this.createRouter(getConfiguration(), protocolFactory, wifiManager, connectivityManager);
                if (!ModelUtil.ANDROID_EMULATOR && AndroidUpnpServiceImpl.this.isListeningForConnectivityChanges()) {
                    AndroidUpnpServiceImpl.this.registerReceiver(androidWifiSwitchableRouterCreateRouter.getBroadcastReceiver(), new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
                }
                return androidWifiSwitchableRouterCreateRouter;
            }
        };
    }

    protected AndroidUpnpServiceConfiguration createConfiguration(WifiManager wifiManager) {
        return new AndroidUpnpServiceConfiguration(wifiManager);
    }

    protected AndroidWifiSwitchableRouter createRouter(UpnpServiceConfiguration upnpServiceConfiguration, ProtocolFactory protocolFactory, WifiManager wifiManager, ConnectivityManager connectivityManager) {
        return new AndroidWifiSwitchableRouter(upnpServiceConfiguration, protocolFactory, wifiManager, connectivityManager);
    }

    @Override // android.app.Service
    public void onDestroy() {
        if (!ModelUtil.ANDROID_EMULATOR && isListeningForConnectivityChanges()) {
            unregisterReceiver(((AndroidWifiSwitchableRouter) this.upnpService.getRouter()).getBroadcastReceiver());
        }
        this.upnpService.shutdown();
    }

    @Override // android.app.Service
    public IBinder onBind(Intent intent) {
        return this.binder;
    }

    protected class Binder extends android.os.Binder implements AndroidUpnpService {
        protected Binder() {
        }

        @Override // org.teleal.cling.android.AndroidUpnpService
        public UpnpService get() {
            return AndroidUpnpServiceImpl.this.upnpService;
        }

        @Override // org.teleal.cling.android.AndroidUpnpService
        public UpnpServiceConfiguration getConfiguration() {
            return AndroidUpnpServiceImpl.this.upnpService.getConfiguration();
        }

        @Override // org.teleal.cling.android.AndroidUpnpService
        public Registry getRegistry() {
            return AndroidUpnpServiceImpl.this.upnpService.getRegistry();
        }

        @Override // org.teleal.cling.android.AndroidUpnpService
        public ControlPoint getControlPoint() {
            return AndroidUpnpServiceImpl.this.upnpService.getControlPoint();
        }
    }
}

