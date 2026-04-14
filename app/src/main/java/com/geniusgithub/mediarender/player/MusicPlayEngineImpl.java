package com.geniusgithub.mediarender.player;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.audiofx.Visualizer;
import com.geniusgithub.mediarender.util.CommonLog;
import com.geniusgithub.mediarender.util.LogFactory;

/* JADX INFO: loaded from: classes.dex */
public class MusicPlayEngineImpl extends AbstractMediaPlayEngine {
    private final CommonLog log;
    private MediaPlayer.OnBufferingUpdateListener mBufferingUpdateListener;
    private Visualizer.OnDataCaptureListener mDataCaptureListener;
    private MediaPlayer.OnSeekCompleteListener mSeekCompleteListener;
    private Visualizer mVisualizer;

    public MusicPlayEngineImpl(Context context) {
        super(context);
        this.log = LogFactory.createLog();
    }

    public void setOnBuffUpdateListener(MediaPlayer.OnBufferingUpdateListener onBufferingUpdateListener) {
        this.mBufferingUpdateListener = onBufferingUpdateListener;
    }

    public void setOnSeekCompleteListener(MediaPlayer.OnSeekCompleteListener onSeekCompleteListener) {
        this.mSeekCompleteListener = onSeekCompleteListener;
    }

    public void setDataCaptureListener(Visualizer.OnDataCaptureListener onDataCaptureListener) {
        this.mDataCaptureListener = onDataCaptureListener;
    }

    public boolean reInitVisualizer(int i) {
        releaseVisualizer();
        int maxCaptureRate = Visualizer.getMaxCaptureRate();
        Visualizer visualizer = new Visualizer(i);
        this.mVisualizer = visualizer;
        visualizer.setCaptureSize(256);
        Visualizer.OnDataCaptureListener onDataCaptureListener = this.mDataCaptureListener;
        if (onDataCaptureListener != null) {
            this.mVisualizer.setDataCaptureListener(onDataCaptureListener, maxCaptureRate / 2, false, true);
        }
        return true;
    }

    public void releaseVisualizer() {
        Visualizer visualizer = this.mVisualizer;
        if (visualizer != null) {
            visualizer.setEnabled(false);
            this.mVisualizer.release();
            this.mVisualizer = null;
        }
    }

    public void enableVisualizer(boolean z) {
        Visualizer visualizer = this.mVisualizer;
        if (visualizer != null) {
            visualizer.setEnabled(z);
        }
    }

    @Override // com.geniusgithub.mediarender.player.AbstractMediaPlayEngine, com.geniusgithub.mediarender.player.IBasePlayEngine
    public void play() {
        super.play();
        enableVisualizer(true);
    }

    @Override // com.geniusgithub.mediarender.player.AbstractMediaPlayEngine, com.geniusgithub.mediarender.player.IBasePlayEngine
    public void pause() {
        super.pause();
        enableVisualizer(false);
    }

    @Override // com.geniusgithub.mediarender.player.AbstractMediaPlayEngine, com.geniusgithub.mediarender.player.IBasePlayEngine
    public void stop() {
        super.stop();
        enableVisualizer(false);
    }

    @Override // com.geniusgithub.mediarender.player.AbstractMediaPlayEngine
    public void exit() {
        super.exit();
        releaseVisualizer();
    }

    @Override // com.geniusgithub.mediarender.player.AbstractMediaPlayEngine
    protected boolean prepareSelf() {
        this.mMediaPlayer.reset();
        try {
            this.mMediaPlayer.setDataSource(this.mMediaInfo.getUrl());
            this.mMediaPlayer.setAudioStreamType(3);
            if (this.mBufferingUpdateListener != null) {
                this.mMediaPlayer.setOnBufferingUpdateListener(this.mBufferingUpdateListener);
            }
            this.mMediaPlayer.prepareAsync();
            this.log.e("mMediaPlayer.prepareAsync path = " + this.mMediaInfo.getUrl());
            this.mPlayState = 4;
            performPlayListener(this.mPlayState);
            enableVisualizer(true);
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
        this.log.e("prepareComplete");
        this.mPlayState = 5;
        if (this.mPlayerEngineListener != null) {
            this.mPlayerEngineListener.onTrackPrepareComplete(this.mMediaInfo);
        }
        this.mMediaPlayer.start();
        this.mPlayState = 1;
        performPlayListener(this.mPlayState);
        reInitVisualizer(this.mMediaPlayer.getAudioSessionId());
        enableVisualizer(true);
        return true;
    }
}

