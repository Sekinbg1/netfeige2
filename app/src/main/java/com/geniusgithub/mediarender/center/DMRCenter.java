package com.geniusgithub.mediarender.center;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import com.geniusgithub.mediarender.image.ImageActivity;
import com.geniusgithub.mediarender.jni.PlatinumReflection;
import com.geniusgithub.mediarender.music.MusicActivity;
import com.geniusgithub.mediarender.util.CommonLog;
import com.geniusgithub.mediarender.util.CommonUtil;
import com.geniusgithub.mediarender.util.DlnaUtils;
import com.geniusgithub.mediarender.util.LogFactory;
import com.geniusgithub.mediarender.video.VideoActivity;
import com.netfeige.dlna.ContentTree;

/* JADX INFO: loaded from: classes.dex */
public class DMRCenter implements PlatinumReflection.ActionReflectionListener, IDMRAction {
    public static final int CUR_MEDIA_TYPE_MUSCI = 1;
    public static final int CUR_MEDIA_TYPE_PICTURE = 3;
    public static final int CUR_MEDIA_TYPE_VIDEO = 2;
    private static final int DELAYTIME = 200;
    private static final int MSG_SEND_STOPCMD = 4;
    private static final int MSG_START_MUSICPLAY = 1;
    private static final int MSG_START_PICPLAY = 2;
    private static final int MSG_START_VIDOPLAY = 3;
    private static final CommonLog log = LogFactory.createLog();
    private Context mContext;
    private int mCurMediaInfoType = -1;
    private Handler mHandler = new Handler() { // from class: com.geniusgithub.mediarender.center.DMRCenter.1
        @Override // android.os.Handler
        public void dispatchMessage(Message message) {
            try {
                int i = message.what;
                if (i == 1) {
                    DMRCenter.this.startPlayMusic((DlnaMediaModel) message.obj);
                } else if (i == 2) {
                    DMRCenter.this.startPlayPicture((DlnaMediaModel) message.obj);
                } else if (i == 3) {
                    DMRCenter.this.startPlayVideo((DlnaMediaModel) message.obj);
                } else if (i == 4) {
                    MediaControlBrocastFactory.sendStopBorocast(DMRCenter.this.mContext);
                }
            } catch (Exception e) {
                e.printStackTrace();
                DMRCenter.log.e("DMRCenter transdel msg catch Exception!!! msgID = " + message.what);
            }
        }
    };
    private DlnaMediaModel mImageMediaInfo;
    private DlnaMediaModel mMusicMediaInfo;
    private DlnaMediaModel mVideoMediaInfo;

    public DMRCenter(Context context) {
        this.mContext = context;
    }

    @Override // com.geniusgithub.mediarender.jni.PlatinumReflection.ActionReflectionListener
    public synchronized void onActionInvoke(int i, String str, String str2) {
        try {
            switch (i) {
                case 256:
                    onRenderAvTransport(str, str2);
                    break;
                case 257:
                    onRenderStop(str, str2);
                    break;
                case 258:
                    onRenderPlay(str, str2);
                    break;
                case PlatinumReflection.MEDIA_RENDER_CTL_MSG_PAUSE /* 259 */:
                    onRenderPause(str, str2);
                    break;
                case PlatinumReflection.MEDIA_RENDER_CTL_MSG_SEEK /* 260 */:
                    onRenderSeek(str, str2);
                    break;
                case PlatinumReflection.MEDIA_RENDER_CTL_MSG_SETVOLUME /* 261 */:
                    onRenderSetVolume(str, str2);
                    break;
                case PlatinumReflection.MEDIA_RENDER_CTL_MSG_SETMUTE /* 262 */:
                    onRenderSetMute(str, str2);
                    break;
                default:
                    log.e("unrognized cmd!!!");
                    break;
            }
        } catch (Throwable th) {
            throw th;
        }
    }

    @Override // com.geniusgithub.mediarender.center.IDMRAction
    public void onRenderAvTransport(String str, String str2) {
        if (str2 == null) {
            log.e("meteData = null!!!");
            return;
        }
        if (str == null || str.length() < 2) {
            log.e("url = " + str + ", it's invalid...");
            return;
        }
        DlnaMediaModel dlnaMediaModelCreateFromMetaData = DlnaMediaModelFactory.createFromMetaData(str2);
        dlnaMediaModelCreateFromMetaData.setUrl(str);
        if (DlnaUtils.isAudioItem(dlnaMediaModelCreateFromMetaData) || DlnaUtils.isAudioFile(dlnaMediaModelCreateFromMetaData.getUrl())) {
            this.mMusicMediaInfo = dlnaMediaModelCreateFromMetaData;
            this.mCurMediaInfoType = 1;
            return;
        }
        if (DlnaUtils.isVideoItem(dlnaMediaModelCreateFromMetaData) || DlnaUtils.isVideoFile(dlnaMediaModelCreateFromMetaData.getUrl())) {
            this.mVideoMediaInfo = dlnaMediaModelCreateFromMetaData;
            this.mCurMediaInfoType = 2;
        } else {
            if (DlnaUtils.isImageItem(dlnaMediaModelCreateFromMetaData) || DlnaUtils.isImageFile(dlnaMediaModelCreateFromMetaData.getUrl())) {
                this.mImageMediaInfo = dlnaMediaModelCreateFromMetaData;
                this.mCurMediaInfoType = 3;
                return;
            }
            log.e("unknow media type!!! mediainfo.objectclass = \n" + dlnaMediaModelCreateFromMetaData.getObjectClass());
        }
    }

    @Override // com.geniusgithub.mediarender.center.IDMRAction
    public void onRenderPlay(String str, String str2) {
        int i = this.mCurMediaInfoType;
        if (i == 1) {
            DlnaMediaModel dlnaMediaModel = this.mMusicMediaInfo;
            if (dlnaMediaModel != null) {
                delayToPlayMusic(dlnaMediaModel);
            } else {
                MediaControlBrocastFactory.sendPlayBrocast(this.mContext);
            }
            clearState();
            return;
        }
        if (i == 2) {
            DlnaMediaModel dlnaMediaModel2 = this.mVideoMediaInfo;
            if (dlnaMediaModel2 != null) {
                delayToPlayVideo(dlnaMediaModel2);
            } else {
                MediaControlBrocastFactory.sendPlayBrocast(this.mContext);
            }
            clearState();
            return;
        }
        if (i != 3) {
            return;
        }
        DlnaMediaModel dlnaMediaModel3 = this.mImageMediaInfo;
        if (dlnaMediaModel3 != null) {
            delayToPlayImage(dlnaMediaModel3);
        } else {
            MediaControlBrocastFactory.sendPlayBrocast(this.mContext);
        }
        clearState();
    }

    @Override // com.geniusgithub.mediarender.center.IDMRAction
    public void onRenderPause(String str, String str2) {
        MediaControlBrocastFactory.sendPauseBrocast(this.mContext);
    }

    @Override // com.geniusgithub.mediarender.center.IDMRAction
    public void onRenderStop(String str, String str2) {
        delayToStop();
        MediaControlBrocastFactory.sendStopBorocast(this.mContext);
    }

    @Override // com.geniusgithub.mediarender.center.IDMRAction
    public void onRenderSeek(String str, String str2) {
        try {
            MediaControlBrocastFactory.sendSeekBrocast(this.mContext, DlnaUtils.parseSeekTime(str));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override // com.geniusgithub.mediarender.center.IDMRAction
    public void onRenderSetMute(String str, String str2) {
        if (ContentTree.VIDEO_ID.equals(str)) {
            CommonUtil.setVolumeMute(this.mContext);
        } else if (ContentTree.ROOT_ID.equals(str)) {
            CommonUtil.setVolumeUnmute(this.mContext);
        }
    }

    @Override // com.geniusgithub.mediarender.center.IDMRAction
    public void onRenderSetVolume(String str, String str2) {
        try {
            int iIntValue = Integer.valueOf(str).intValue();
            if (iIntValue < 101) {
                CommonUtil.setCurrentVolume(iIntValue, this.mContext);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void clearState() {
        this.mMusicMediaInfo = null;
        this.mVideoMediaInfo = null;
        this.mImageMediaInfo = null;
    }

    private void delayToPlayMusic(DlnaMediaModel dlnaMediaModel) {
        if (dlnaMediaModel != null) {
            clearDelayMsg();
            this.mHandler.sendMessageDelayed(this.mHandler.obtainMessage(1, dlnaMediaModel), 200L);
        }
    }

    private void delayToPlayVideo(DlnaMediaModel dlnaMediaModel) {
        if (dlnaMediaModel != null) {
            clearDelayMsg();
            this.mHandler.sendMessageDelayed(this.mHandler.obtainMessage(3, dlnaMediaModel), 200L);
        }
    }

    private void delayToPlayImage(DlnaMediaModel dlnaMediaModel) {
        if (dlnaMediaModel != null) {
            clearDelayMsg();
            this.mHandler.sendMessageDelayed(this.mHandler.obtainMessage(2, dlnaMediaModel), 200L);
        }
    }

    private void delayToStop() {
        clearDelayMsg();
        Handler handler = this.mHandler;
        handler.sendMessageDelayed(handler.obtainMessage(4), 200L);
    }

    private void clearDelayMsg() {
        clearDelayMsg(1);
        clearDelayMsg(2);
        clearDelayMsg(3);
        clearDelayMsg(4);
    }

    private void clearDelayMsg(int i) {
        this.mHandler.removeMessages(i);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void startPlayMusic(DlnaMediaModel dlnaMediaModel) {
        log.d("startPlayMusic");
        Intent intent = new Intent();
        intent.setClass(this.mContext, MusicActivity.class);
        DlnaMediaModelFactory.pushMediaModelToIntent(intent, dlnaMediaModel);
        intent.setFlags(805306368);
        this.mContext.startActivity(intent);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void startPlayVideo(DlnaMediaModel dlnaMediaModel) {
        log.d("startPlayVideo");
        Intent intent = new Intent();
        intent.setClass(this.mContext, VideoActivity.class);
        DlnaMediaModelFactory.pushMediaModelToIntent(intent, dlnaMediaModel);
        intent.setFlags(805306368);
        this.mContext.startActivity(intent);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void startPlayPicture(DlnaMediaModel dlnaMediaModel) {
        log.d("startPlayPicture");
        Intent intent = new Intent();
        intent.setClass(this.mContext, ImageActivity.class);
        DlnaMediaModelFactory.pushMediaModelToIntent(intent, dlnaMediaModel);
        intent.setFlags(805306368);
        this.mContext.startActivity(intent);
    }
}

