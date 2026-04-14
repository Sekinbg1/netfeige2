package com.geniusgithub.mediarender.center;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

/* JADX INFO: loaded from: classes.dex */
public class MediaControlBrocastFactory {
    public static final String MEDIA_RENDERER_CMD_PAUSE = "com.geniusgithub.control.pause_command";
    public static final String MEDIA_RENDERER_CMD_PLAY = "com.geniusgithub.control.play_command";
    public static final String MEDIA_RENDERER_CMD_SEEKPS = "com.geniusgithub.control.seekps_command";
    public static final String MEDIA_RENDERER_CMD_STOP = "com.geniusgithub.control.stop_command";
    public static final String PARAM_CMD_SEEKPS = "get_param_seekps";
    private Context mContext;
    private MediaControlBrocastReceiver mMediaControlReceiver;

    public interface IMediaControlListener {
        void onPauseCommand();

        void onPlayCommand();

        void onSeekCommand(int i);

        void onStopCommand();
    }

    public MediaControlBrocastFactory(Context context) {
        this.mContext = context;
    }

    public void register(IMediaControlListener iMediaControlListener) {
        if (this.mMediaControlReceiver == null) {
            MediaControlBrocastReceiver mediaControlBrocastReceiver = new MediaControlBrocastReceiver();
            this.mMediaControlReceiver = mediaControlBrocastReceiver;
            mediaControlBrocastReceiver.setMediaControlListener(iMediaControlListener);
            this.mContext.registerReceiver(this.mMediaControlReceiver, new IntentFilter(MEDIA_RENDERER_CMD_PLAY));
            this.mContext.registerReceiver(this.mMediaControlReceiver, new IntentFilter(MEDIA_RENDERER_CMD_PAUSE));
            this.mContext.registerReceiver(this.mMediaControlReceiver, new IntentFilter(MEDIA_RENDERER_CMD_STOP));
            this.mContext.registerReceiver(this.mMediaControlReceiver, new IntentFilter(MEDIA_RENDERER_CMD_SEEKPS));
        }
    }

    public void unregister() {
        MediaControlBrocastReceiver mediaControlBrocastReceiver = this.mMediaControlReceiver;
        if (mediaControlBrocastReceiver != null) {
            this.mContext.unregisterReceiver(mediaControlBrocastReceiver);
            this.mMediaControlReceiver = null;
        }
    }

    public static void sendPlayBrocast(Context context) {
        context.sendBroadcast(new Intent(MEDIA_RENDERER_CMD_PLAY));
    }

    public static void sendPauseBrocast(Context context) {
        context.sendBroadcast(new Intent(MEDIA_RENDERER_CMD_PAUSE));
    }

    public static void sendStopBorocast(Context context) {
        context.sendBroadcast(new Intent(MEDIA_RENDERER_CMD_STOP));
    }

    public static void sendSeekBrocast(Context context, int i) {
        Intent intent = new Intent(MEDIA_RENDERER_CMD_SEEKPS);
        intent.putExtra(PARAM_CMD_SEEKPS, i);
        context.sendBroadcast(intent);
    }
}

