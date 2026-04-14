package com.netfeige.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.netfeige.common.Public_Tools;
import java.util.ArrayList;

/* JADX INFO: loaded from: classes.dex */
public class NetStatusBroadcast extends BroadcastReceiver {
    public static ArrayList<EventHandler> ehList = new ArrayList<>();

    public interface EventHandler {
        void wifiStatusNotification(boolean z);
    }

    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        for (int i = 0; i < ehList.size(); i++) {
            ehList.get(i).wifiStatusNotification(Public_Tools.isWifiConnect());
        }
    }
}

