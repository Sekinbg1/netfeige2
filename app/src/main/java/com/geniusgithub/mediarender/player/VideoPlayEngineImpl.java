package com.geniusgithub.mediarender.player;

import android.content.Context;
import android.media.MediaPlayer;
import android.view.SurfaceHolder;
import com.geniusgithub.mediarender.util.CommonLog;
import com.geniusgithub.mediarender.util.CommonUtil;
import com.geniusgithub.mediarender.util.LogFactory;

/* JADX INFO: loaded from: classes.dex */
public class VideoPlayEngineImpl extends AbstractMediaPlayEngine {
    private final CommonLog log;
    private MediaPlayer.OnBufferingUpdateListener mBufferingUpdateListener;
    private SurfaceHolder mHolder;
    private MediaPlayer.OnErrorListener mOnErrorListener;
    private MediaPlayer.OnSeekCompleteListener mSeekCompleteListener;

    public VideoPlayEngineImpl(Context context, SurfaceHolder surfaceHolder) {
        super(context);
        this.log = LogFactory.createLog();
        this.mHolder = null;
        setHolder(surfaceHolder);
    }

    public void setHolder(SurfaceHolder surfaceHolder) {
        this.mHolder = surfaceHolder;
    }

    public void setOnBuffUpdateListener(MediaPlayer.OnBufferingUpdateListener onBufferingUpdateListener) {
        this.mBufferingUpdateListener = onBufferingUpdateListener;
    }

    public void setOnSeekCompleteListener(MediaPlayer.OnSeekCompleteListener onSeekCompleteListener) {
        this.mSeekCompleteListener = onSeekCompleteListener;
    }

    public void setOnErrorListener(MediaPlayer.OnErrorListener onErrorListener) {
        this.mOnErrorListener = onErrorListener;
    }

    @Override // com.geniusgithub.mediarender.player.AbstractMediaPlayEngine
    protected boolean prepareSelf() {
        this.mMediaPlayer.reset();
        try {
            this.mMediaPlayer.setDataSource(this.mMediaInfo.getUrl());
            this.mMediaPlayer.setAudioStreamType(3);
            if (this.mHolder != null) {
                this.mMediaPlayer.setDisplay(this.mHolder);
            }
            if (this.mBufferingUpdateListener != null) {
                this.mMediaPlayer.setOnBufferingUpdateListener(this.mBufferingUpdateListener);
            }
            if (this.mSeekCompleteListener != null) {
                this.mMediaPlayer.setOnSeekCompleteListener(this.mSeekCompleteListener);
            }
            if (this.mOnErrorListener != null) {
                this.mMediaPlayer.setOnErrorListener(this.mOnErrorListener);
            }
            this.mMediaPlayer.prepareAsync();
            this.log.e("mMediaPlayer.prepareAsync path = " + this.mMediaInfo.getUrl());
            this.mPlayState = 4;
            performPlayListener(this.mPlayState);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            this.mPlayState = 0;
            performPlayListener(this.mPlayState);
            return false;
        }
    }

    @Override // com.geniusgithub.mediarender.player.AbstractMediaPlayEngine
    protected boolean prepareComplete(MediaPlayer mediaPlayer) {
        this.mPlayState = 5;
        if (this.mPlayerEngineListener != null) {
            this.mPlayerEngineListener.onTrackPrepareComplete(this.mMediaInfo);
        }
        if (this.mHolder != null) {
            CommonUtil.ViewSize fitSize = CommonUtil.getFitSize(this.mContext, mediaPlayer);
            this.mHolder.setFixedSize(fitSize.width, fitSize.height);
        }
        this.mMediaPlayer.start();
        this.mPlayState = 1;
        performPlayListener(this.mPlayState);
        return true;
    }
}

