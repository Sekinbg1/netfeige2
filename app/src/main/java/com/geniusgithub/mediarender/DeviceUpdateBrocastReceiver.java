package com.geniusgithub.mediarender;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.geniusgithub.mediarender.DeviceUpdateBrocastFactory;
import com.geniusgithub.mediarender.util.CommonLog;
import com.geniusgithub.mediarender.util.LogFactory;

/* JADX INFO: loaded from: classes.dex */
public class DeviceUpdateBrocastReceiver extends BroadcastReceiver {
    private static final CommonLog log = LogFactory.createLog();
    private DeviceUpdateBrocastFactory.IDevUpdateListener mListener;

    public void setListener(DeviceUpdateBrocastFactory.IDevUpdateListener iDevUpdateListener) {
        this.mListener = iDevUpdateListener;
    }

    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        DeviceUpdateBrocastFactory.IDevUpdateListener iDevUpdateListener;
        String action = intent.getAction();
        if (action == null || !DeviceUpdateBrocastFactory.PARAM_DEV_UPDATE.equalsIgnoreCase(action) || (iDevUpdateListener = this.mListener) == null) {
            return;
        }
        iDevUpdateListener.onUpdate();
    }
}

