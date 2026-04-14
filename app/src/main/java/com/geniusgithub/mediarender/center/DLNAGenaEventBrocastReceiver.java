package com.geniusgithub.mediarender.center;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.geniusgithub.mediarender.jni.PlatinumJniProxy;
import com.geniusgithub.mediarender.jni.PlatinumReflection;
import com.geniusgithub.mediarender.util.CommonLog;
import com.geniusgithub.mediarender.util.LogFactory;

/* JADX INFO: loaded from: classes.dex */
public class DLNAGenaEventBrocastReceiver extends BroadcastReceiver {
    private static final CommonLog log = LogFactory.createLog();

    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action != null && PlatinumReflection.RENDERER_TOCONTRPOINT_CMD_INTENT_NAME.equalsIgnoreCase(action)) {
            onTransdelGenaEvent(intent);
        }
    }

    private void onTransdelGenaEvent(Intent intent) {
        switch (intent.getIntExtra(PlatinumReflection.GET_RENDERER_TOCONTRPOINT_CMD, 0)) {
            case 256:
                PlatinumJniProxy.responseGenaEvent(256, intent.getStringExtra(PlatinumReflection.GET_PARAM_MEDIA_DURATION), (String) null);
                break;
            case 257:
                PlatinumJniProxy.responseGenaEvent(257, intent.getStringExtra(PlatinumReflection.GET_PARAM_MEDIA_POSITION), (String) null);
                break;
            case 258:
                String stringExtra = intent.getStringExtra(PlatinumReflection.GET_PARAM_MEDIA_PLAYINGSTATE);
                PlatinumJniProxy.responseGenaEvent(258, stringExtra, (String) null);
                if (stringExtra.equalsIgnoreCase(PlatinumReflection.MEDIA_PLAYINGSTATE_STOP)) {
                    PlatinumJniProxy.responseGenaEvent(257, "00:00:00", (String) null);
                }
                break;
        }
    }
}

