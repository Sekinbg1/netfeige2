package com.geniusgithub.mediarender.center;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.geniusgithub.mediarender.center.MediaControlBrocastFactory;
import com.geniusgithub.mediarender.util.CommonLog;
import com.geniusgithub.mediarender.util.LogFactory;

/* JADX INFO: loaded from: classes.dex */
public class MediaControlBrocastReceiver extends BroadcastReceiver {
    private static final CommonLog log = LogFactory.createLog();
    private MediaControlBrocastFactory.IMediaControlListener mMediaControlListener;

    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction() == null || this.mMediaControlListener == null) {
            return;
        }
        TransdelControlCommand(intent);
    }

    public void setMediaControlListener(MediaControlBrocastFactory.IMediaControlListener iMediaControlListener) {
        this.mMediaControlListener = iMediaControlListener;
    }

    private void TransdelControlCommand(Intent intent) {
        String action = intent.getAction();
        if (action.equalsIgnoreCase(MediaControlBrocastFactory.MEDIA_RENDERER_CMD_PLAY)) {
            this.mMediaControlListener.onPlayCommand();
            return;
        }
        if (action.equalsIgnoreCase(MediaControlBrocastFactory.MEDIA_RENDERER_CMD_PAUSE)) {
            this.mMediaControlListener.onPauseCommand();
            return;
        }
        if (action.equalsIgnoreCase(MediaControlBrocastFactory.MEDIA_RENDERER_CMD_STOP)) {
            this.mMediaControlListener.onStopCommand();
        } else if (action.equalsIgnoreCase(MediaControlBrocastFactory.MEDIA_RENDERER_CMD_SEEKPS)) {
            this.mMediaControlListener.onSeekCommand(intent.getIntExtra(MediaControlBrocastFactory.PARAM_CMD_SEEKPS, 0));
        }
    }
}

