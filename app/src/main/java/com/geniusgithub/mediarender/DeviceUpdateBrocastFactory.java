package com.geniusgithub.mediarender;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

/* JADX INFO: loaded from: classes.dex */
public class DeviceUpdateBrocastFactory {
    public static final String PARAM_DEV_UPDATE = "com.geniusgithub.PARAM_DEV_UPDATE";
    private Context mContext;
    private DeviceUpdateBrocastReceiver mReceiver;

    public interface IDevUpdateListener {
        void onUpdate();
    }

    public DeviceUpdateBrocastFactory(Context context) {
        this.mContext = context;
    }

    public void register(IDevUpdateListener iDevUpdateListener) {
        if (this.mReceiver == null) {
            DeviceUpdateBrocastReceiver deviceUpdateBrocastReceiver = new DeviceUpdateBrocastReceiver();
            this.mReceiver = deviceUpdateBrocastReceiver;
            deviceUpdateBrocastReceiver.setListener(iDevUpdateListener);
            this.mContext.registerReceiver(this.mReceiver, new IntentFilter(PARAM_DEV_UPDATE));
        }
    }

    public void unregister() {
        DeviceUpdateBrocastReceiver deviceUpdateBrocastReceiver = this.mReceiver;
        if (deviceUpdateBrocastReceiver != null) {
            this.mContext.unregisterReceiver(deviceUpdateBrocastReceiver);
            this.mReceiver = null;
        }
    }

    public static void sendDevUpdateBrocast(Context context) {
        Intent intent = new Intent();
        intent.setAction(PARAM_DEV_UPDATE);
        context.sendBroadcast(intent);
    }
}

