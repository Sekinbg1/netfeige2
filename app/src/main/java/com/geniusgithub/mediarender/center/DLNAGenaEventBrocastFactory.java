package com.geniusgithub.mediarender.center;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import com.geniusgithub.mediarender.jni.PlatinumReflection;
import com.geniusgithub.mediarender.util.CommonLog;
import com.geniusgithub.mediarender.util.DlnaUtils;
import com.geniusgithub.mediarender.util.LogFactory;

/* JADX INFO: loaded from: classes.dex */
public class DLNAGenaEventBrocastFactory {
    private static final CommonLog log = LogFactory.createLog();
    private Context mContext;
    private DLNAGenaEventBrocastReceiver mReceiver;

    public DLNAGenaEventBrocastFactory(Context context) {
        this.mContext = context;
    }

    public void registerBrocast() {
        if (this.mReceiver == null) {
            DLNAGenaEventBrocastReceiver dLNAGenaEventBrocastReceiver = new DLNAGenaEventBrocastReceiver();
            this.mReceiver = dLNAGenaEventBrocastReceiver;
            this.mContext.registerReceiver(dLNAGenaEventBrocastReceiver, new IntentFilter(PlatinumReflection.RENDERER_TOCONTRPOINT_CMD_INTENT_NAME));
        }
    }

    public void unRegisterBrocast() {
        DLNAGenaEventBrocastReceiver dLNAGenaEventBrocastReceiver = this.mReceiver;
        if (dLNAGenaEventBrocastReceiver != null) {
            this.mContext.unregisterReceiver(dLNAGenaEventBrocastReceiver);
            this.mReceiver = null;
        }
    }

    public static void sendTranstionEvent(Context context) {
        sendGenaPlayState(context, PlatinumReflection.MEDIA_PLAYINGSTATE_TRANSTION);
    }

    public static void sendDurationEvent(Context context, int i) {
        if (i != 0) {
            Intent intent = new Intent(PlatinumReflection.RENDERER_TOCONTRPOINT_CMD_INTENT_NAME);
            intent.putExtra(PlatinumReflection.GET_RENDERER_TOCONTRPOINT_CMD, 256);
            intent.putExtra(PlatinumReflection.GET_PARAM_MEDIA_DURATION, DlnaUtils.formatTimeFromMSInt(i));
            context.sendBroadcast(intent);
        }
    }

    public static void sendSeekEvent(Context context, int i) {
        if (i != 0) {
            Intent intent = new Intent(PlatinumReflection.RENDERER_TOCONTRPOINT_CMD_INTENT_NAME);
            intent.putExtra(PlatinumReflection.GET_RENDERER_TOCONTRPOINT_CMD, 257);
            intent.putExtra(PlatinumReflection.GET_PARAM_MEDIA_POSITION, DlnaUtils.formatTimeFromMSInt(i));
            context.sendBroadcast(intent);
        }
    }

    public static void sendPlayStateEvent(Context context) {
        sendGenaPlayState(context, PlatinumReflection.MEDIA_PLAYINGSTATE_PLAYING);
    }

    public static void sendPauseStateEvent(Context context) {
        sendGenaPlayState(context, PlatinumReflection.MEDIA_PLAYINGSTATE_PAUSE);
    }

    public static void sendStopStateEvent(Context context) {
        sendGenaPlayState(context, PlatinumReflection.MEDIA_PLAYINGSTATE_STOP);
    }

    private static void sendGenaPlayState(Context context, String str) {
        Intent intent = new Intent(PlatinumReflection.RENDERER_TOCONTRPOINT_CMD_INTENT_NAME);
        intent.putExtra(PlatinumReflection.GET_RENDERER_TOCONTRPOINT_CMD, 258);
        intent.putExtra(PlatinumReflection.GET_PARAM_MEDIA_PLAYINGSTATE, str);
        context.sendBroadcast(intent);
    }
}

