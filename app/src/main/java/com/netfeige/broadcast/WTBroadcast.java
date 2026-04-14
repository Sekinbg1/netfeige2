package com.netfeige.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import java.util.ArrayList;

/* JADX INFO: loaded from: classes.dex */
public class WTBroadcast extends BroadcastReceiver {
    public static ArrayList<EventHandler> ehList = new ArrayList<>();

    public interface EventHandler {
        void handleConnectChange();

        void scanResultsAvailable();

        void wifiStatusNotification(Intent intent);
    }

    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        int i = 0;
        if (intent.getAction().equals("android.net.wifi.SCAN_RESULTS")) {
            while (i < ehList.size()) {
                ehList.get(i).scanResultsAvailable();
                i++;
            }
        } else if (intent.getAction().equals("android.net.wifi.WIFI_STATE_CHANGED")) {
            while (i < ehList.size()) {
                ehList.get(i).wifiStatusNotification(intent);
                i++;
            }
        } else if (intent.getAction().equals("android.net.wifi.STATE_CHANGE")) {
            while (i < ehList.size()) {
                ehList.get(i).handleConnectChange();
                i++;
            }
        }
    }
}

