package com.geniusgithub.mediarender.player;

import android.content.Context;
import android.media.MediaPlayer;
import com.geniusgithub.mediarender.center.DlnaMediaModel;
import com.geniusgithub.mediarender.util.CommonLog;
import com.geniusgithub.mediarender.util.LogFactory;

/* JADX INFO: loaded from: classes.dex */
public abstract class AbstractMediaPlayEngine implements IBasePlayEngine, MediaPlayer.OnCompletionListener, MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener {
    private static final CommonLog log = LogFactory.createLog();
    protected Context mContext;
    protected DlnaMediaModel mMediaInfo;
    protected MediaPlayer mMediaPlayer;
    protected int mPlayState;
    protected PlayerEngineListener mPlayerEngineListener;

    protected abstract boolean prepareComplete(MediaPlayer mediaPlayer);

    protected abstract boolean prepareSelf();

    protected void defaultParam() {
        MediaPlayer mediaPlayer = new MediaPlayer();
        this.mMediaPlayer = mediaPlayer;
        mediaPlayer.setOnCompletionListener(this);
        this.mMediaPlayer.setOnPreparedListener(this);
        this.mMediaInfo = null;
        this.mPlayState = -1;
    }

    public AbstractMediaPlayEngine(Context context) {
        this.mContext = context;
        defaultParam();
    }

    public void setPlayerListener(PlayerEngineListener playerEngineListener) {
        this.mPlayerEngineListener = playerEngineListener;
    }

    @Override // com.geniusgithub.mediarender.player.IBasePlayEngine
    public void play() {
        int i = this.mPlayState;
        if (i != 2) {
            if (i != 3) {
                return;
            }
            prepareSelf();
        } else {
            this.mMediaPlayer.start();
            this.mPlayState = 1;
            performPlayListener(1);
        }
    }

    @Override // com.geniusgithub.mediarender.player.IBasePlayEngine
    public void pause() {
        if (this.mPlayState != 1) {
            return;
        }
        this.mMediaPlayer.pause();
        this.mPlayState = 2;
        performPlayListener(2);
    }

    @Override // com.geniusgithub.mediarender.player.IBasePlayEngine
    public void stop() {
        if (this.mPlayState != -1) {
            this.mMediaPlayer.reset();
            this.mPlayState = 3;
            performPlayListener(3);
        }
    }

    @Override // com.geniusgithub.mediarender.player.IBasePlayEngine
    public void skipTo(int i) {
        int i2 = this.mPlayState;
        if (i2 == 1 || i2 == 2) {
            this.mMediaPlayer.seekTo(reviceSeekValue(i));
        }
    }

    public void exit() {
        stop();
        this.mMediaPlayer.release();
        this.mMediaInfo = null;
        this.mPlayState = -1;
    }

    @Override // android.media.MediaPlayer.OnPreparedListener
    public void onPrepared(MediaPlayer mediaPlayer) {
        prepareComplete(mediaPlayer);
    }

    @Override // android.media.MediaPlayer.OnCompletionListener
    public void onCompletion(MediaPlayer mediaPlayer) {
        log.e("onCompletion...");
        PlayerEngineListener playerEngineListener = this.mPlayerEngineListener;
        if (playerEngineListener != null) {
            playerEngineListener.onTrackPlayComplete(this.mMediaInfo);
        }
    }

    public boolean isPlaying() {
        return this.mPlayState == 1;
    }

    public boolean isPause() {
        return this.mPlayState == 2;
    }

    public void playMedia(DlnaMediaModel dlnaMediaModel) {
        if (dlnaMediaModel != null) {
            this.mMediaInfo = dlnaMediaModel;
            prepareSelf();
        }
    }

    public int getCurPosition() {
        int i = this.mPlayState;
        if (i == 1 || i == 2) {
            return this.mMediaPlayer.getCurrentPosition();
        }
        return 0;
    }

    public int getDuration() {
        int i = this.mPlayState;
        if (i == 1 || i == 2 || i == 5) {
            return this.mMediaPlayer.getDuration();
        }
        return 0;
    }

    public int getPlayState() {
        return this.mPlayState;
    }

    protected void performPlayListener(int i) {
        PlayerEngineListener playerEngineListener = this.mPlayerEngineListener;
        if (playerEngineListener != null) {
            if (i == 0) {
                playerEngineListener.onTrackStreamError(this.mMediaInfo);
                return;
            }
            if (i == 1) {
                playerEngineListener.onTrackPlay(this.mMediaInfo);
                return;
            }
            if (i == 2) {
                playerEngineListener.onTrackPause(this.mMediaInfo);
            } else if (i == 3) {
                playerEngineListener.onTrackStop(this.mMediaInfo);
            } else {
                if (i != 4) {
                    return;
                }
                playerEngineListener.onTrackPrepareSync(this.mMediaInfo);
            }
        }
    }

    private int reviceSeekValue(int i) {
        if (i < 0) {
            i = 0;
        }
        return i > this.mMediaPlayer.getDuration() ? this.mMediaPlayer.getDuration() : i;
    }

    @Override // android.media.MediaPlayer.OnErrorListener
    public boolean onError(MediaPlayer mediaPlayer, int i, int i2) {
        log.e("onError --> what = " + i);
        return false;
    }
}

