package org.teleal.cling.android;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import java.util.logging.Logger;
import org.teleal.cling.UpnpServiceConfiguration;
import org.teleal.cling.model.ModelUtil;
import org.teleal.cling.protocol.ProtocolFactory;
import org.teleal.cling.transport.Router;
import org.teleal.cling.transport.SwitchableRouterImpl;
import org.teleal.cling.transport.spi.InitializationException;

/* JADX INFO: loaded from: classes.dex */
public class AndroidWifiSwitchableRouter extends SwitchableRouterImpl {
    private static Logger log = Logger.getLogger(Router.class.getName());
    final BroadcastReceiver broadcastReceiver;
    private final ConnectivityManager connectivityManager;
    private WifiManager.MulticastLock multicastLock;
    private final WifiManager wifiManager;

    @Override // org.teleal.cling.transport.SwitchableRouterImpl
    protected int getLockTimeoutMillis() {
        return 10000;
    }

    public AndroidWifiSwitchableRouter(UpnpServiceConfiguration upnpServiceConfiguration, ProtocolFactory protocolFactory, WifiManager wifiManager, ConnectivityManager connectivityManager) {
        super(upnpServiceConfiguration, protocolFactory);
        this.broadcastReceiver = new BroadcastReceiver() { // from class: org.teleal.cling.android.AndroidWifiSwitchableRouter.1
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals("android.net.conn.CONNECTIVITY_CHANGE")) {
                    if (!AndroidWifiSwitchableRouter.this.getConnectivityManager().getNetworkInfo(1).isConnected()) {
                        AndroidWifiSwitchableRouter.log.info("WiFi state changed, trying to disable router");
                        AndroidWifiSwitchableRouter.this.disable();
                    } else {
                        AndroidWifiSwitchableRouter.log.info("WiFi state changed, trying to enable router");
                        AndroidWifiSwitchableRouter.this.enable();
                    }
                }
            }
        };
        this.wifiManager = wifiManager;
        this.connectivityManager = connectivityManager;
        if (getConnectivityManager().getNetworkInfo(1).isConnected() || ModelUtil.ANDROID_EMULATOR) {
            log.info("WiFi is enabled (or running on Android emulator), starting router immediately");
            enable();
        }
    }

    public BroadcastReceiver getBroadcastReceiver() {
        return this.broadcastReceiver;
    }

    protected WifiManager getWifiManager() {
        return this.wifiManager;
    }

    protected ConnectivityManager getConnectivityManager() {
        return this.connectivityManager;
    }

    @Override // org.teleal.cling.transport.SwitchableRouterImpl, org.teleal.cling.transport.SwitchableRouter
    public boolean enable() throws SwitchableRouterImpl.RouterLockAcquisitionException {
        lock(this.writeLock);
        try {
            boolean zEnable = super.enable();
            if (zEnable) {
                WifiManager.MulticastLock multicastLockCreateMulticastLock = getWifiManager().createMulticastLock(getClass().getSimpleName());
                this.multicastLock = multicastLockCreateMulticastLock;
                multicastLockCreateMulticastLock.acquire();
            }
            return zEnable;
        } finally {
            unlock(this.writeLock);
        }
    }

    @Override // org.teleal.cling.transport.SwitchableRouterImpl, org.teleal.cling.transport.SwitchableRouter
    public void handleStartFailure(InitializationException initializationException) {
        WifiManager.MulticastLock multicastLock = this.multicastLock;
        if (multicastLock != null && multicastLock.isHeld()) {
            this.multicastLock.release();
            this.multicastLock = null;
        }
        super.handleStartFailure(initializationException);
    }

    @Override // org.teleal.cling.transport.SwitchableRouterImpl, org.teleal.cling.transport.SwitchableRouter
    public boolean disable() throws SwitchableRouterImpl.RouterLockAcquisitionException {
        lock(this.writeLock);
        try {
            if (this.multicastLock != null && this.multicastLock.isHeld()) {
                this.multicastLock.release();
                this.multicastLock = null;
            }
            return super.disable();
        } finally {
            unlock(this.writeLock);
        }
    }
}

