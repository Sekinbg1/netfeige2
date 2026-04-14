package com.geniusgithub.mediarender.jni;

import com.geniusgithub.mediarender.util.CommonLog;
import com.geniusgithub.mediarender.util.LogFactory;

/* JADX INFO: loaded from: classes.dex */
public class PlatinumReflection {
    public static final String GET_PARAM_MEDIA_DURATION = "get_param_media_duration";
    public static final String GET_PARAM_MEDIA_PLAYINGSTATE = "get_param_media_playingstate";
    public static final String GET_PARAM_MEDIA_POSITION = "get_param_media_position";
    public static final String GET_RENDERER_TOCONTRPOINT_CMD = "get_dlna_renderer_tocontrolpointer.cmd";
    public static final String MEDIA_PLAYINGSTATE_NOMEDIA = "NO_MEDIA_PRESENT";
    public static final String MEDIA_PLAYINGSTATE_PAUSE = "PAUSED_PLAYBACK";
    public static final String MEDIA_PLAYINGSTATE_PLAYING = "PLAYING";
    public static final String MEDIA_PLAYINGSTATE_STOP = "STOPPED";
    public static final String MEDIA_PLAYINGSTATE_TRANSTION = "TRANSITIONING";
    private static final int MEDIA_RENDER_CTL_MSG_BASE = 256;
    public static final int MEDIA_RENDER_CTL_MSG_NEXT = 265;
    public static final int MEDIA_RENDER_CTL_MSG_PAUSE = 259;
    public static final int MEDIA_RENDER_CTL_MSG_PLAY = 258;
    public static final int MEDIA_RENDER_CTL_MSG_PRE = 264;
    public static final int MEDIA_RENDER_CTL_MSG_SEEK = 260;
    public static final int MEDIA_RENDER_CTL_MSG_SETMUTE = 262;
    public static final int MEDIA_RENDER_CTL_MSG_SETPLAYMODE = 263;
    public static final int MEDIA_RENDER_CTL_MSG_SETVOLUME = 261;
    public static final int MEDIA_RENDER_CTL_MSG_SET_AV_URL = 256;
    public static final int MEDIA_RENDER_CTL_MSG_STOP = 257;
    public static final int MEDIA_RENDER_TOCONTRPOINT_SET_MEDIA_DURATION = 256;
    public static final int MEDIA_RENDER_TOCONTRPOINT_SET_MEDIA_PLAYINGSTATE = 258;
    public static final int MEDIA_RENDER_TOCONTRPOINT_SET_MEDIA_POSITION = 257;
    public static final String MEDIA_SEEK_TIME_TYPE_REL_TIME = "REL_TIME";
    public static final String MEDIA_SEEK_TIME_TYPE_TRACK_NR = "TRACK_NR";
    public static final String RENDERER_TOCONTRPOINT_CMD_INTENT_NAME = "com.geniusgithub.platinum.tocontrolpointer.cmd.intent";
    private static final CommonLog log = LogFactory.createLog();
    private static ActionReflectionListener mListener;

    public interface ActionReflectionListener {
        void onActionInvoke(int i, String str, String str2);
    }

    public static void onActionReflection(int i, String str, String str2) {
        ActionReflectionListener actionReflectionListener = mListener;
        if (actionReflectionListener != null) {
            actionReflectionListener.onActionInvoke(i, str, str2);
        }
    }

    public static void setActionInvokeListener(ActionReflectionListener actionReflectionListener) {
        mListener = actionReflectionListener;
    }
}

