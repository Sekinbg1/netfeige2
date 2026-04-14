package com.geniusgithub.mediarender.music;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.media.audiofx.Visualizer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import com.geniusgithub.mediarender.center.DLNAGenaEventBrocastFactory;
import com.geniusgithub.mediarender.center.DlnaMediaModel;
import com.geniusgithub.mediarender.center.DlnaMediaModelFactory;
import com.geniusgithub.mediarender.center.MediaControlBrocastFactory;
import com.geniusgithub.mediarender.player.AbstractTimer;
import com.geniusgithub.mediarender.player.CheckDelayTimer;
import com.geniusgithub.mediarender.player.MusicPlayEngineImpl;
import com.geniusgithub.mediarender.player.PlayerEngineListener;
import com.geniusgithub.mediarender.player.SingleSecondTimer;
import com.geniusgithub.mediarender.util.CommonLog;
import com.geniusgithub.mediarender.util.CommonUtil;
import com.geniusgithub.mediarender.util.DlnaUtils;
import com.geniusgithub.mediarender.util.LogFactory;
import com.netfeige.R;

/* JADX INFO: loaded from: classes.dex */
public class MusicActivity extends Activity implements MediaControlBrocastFactory.IMediaControlListener, MediaPlayer.OnBufferingUpdateListener, MediaPlayer.OnSeekCompleteListener, MediaPlayer.OnErrorListener {
    private static final int CHECK_DELAY = 5;
    private static final int EXIT_ACTIVITY = 3;
    private static final int EXIT_DELAY_TIME = 3000;
    private static final int LOAD_DRAWABLE_COMPLETE = 6;
    private static final int REFRESH_CURPOS = 1;
    private static final int REFRESH_SPEED = 4;
    private static final CommonLog log = LogFactory.createLog();
    private CheckDelayTimer mCheckDelayTimer;
    private Context mContext;
    private Handler mHandler;
    private MediaControlBrocastFactory mMediaControlBorcastFactory;
    private AbstractTimer mNetWorkTimer;
    private MusicPlayEngineListener mPlayEngineListener;
    private AbstractTimer mPlayPosTimer;
    private MusicPlayEngineImpl mPlayerEngineImpl;
    private UIManager mUIManager;
    private DlnaMediaModel mMediaInfo = new DlnaMediaModel();
    private boolean isDestroy = false;
    private boolean isSeekComplete = false;

    @Override // android.app.Activity
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        log.e("onCreate");
        getWindow().addFlags(128);
        setContentView(R.layout.music_player_layout);
        setupsView();
        initData();
        refreshIntent(getIntent());
    }

    @Override // android.app.Activity
    protected void onNewIntent(Intent intent) {
        log.e("onNewIntent");
        refreshIntent(intent);
        super.onNewIntent(intent);
    }

    @Override // android.app.Activity
    protected void onStop() {
        super.onStop();
    }

    @Override // android.app.Activity
    protected void onDestroy() {
        log.e("onDestroy");
        this.isDestroy = true;
        this.mUIManager.unInit();
        this.mCheckDelayTimer.stopTimer();
        this.mNetWorkTimer.stopTimer();
        this.mMediaControlBorcastFactory.unregister();
        this.mPlayPosTimer.stopTimer();
        this.mPlayerEngineImpl.exit();
        super.onDestroy();
    }

    public void setupsView() {
        this.mContext = this;
        this.mUIManager = new UIManager();
    }

    public void initData() {
        this.mPlayPosTimer = new SingleSecondTimer(this);
        Handler handler = new Handler() { // from class: com.geniusgithub.mediarender.music.MusicActivity.1
            @Override // android.os.Handler
            public void handleMessage(Message message) {
                int i = message.what;
                if (i == 1) {
                    MusicActivity.this.refreshCurPos();
                    return;
                }
                if (i == 3) {
                    MusicActivity.this.finish();
                    return;
                }
                if (i == 4) {
                    MusicActivity.this.refreshSpeed();
                    return;
                }
                if (i == 5) {
                    MusicActivity.this.checkDelay();
                } else {
                    if (i != 6) {
                        return;
                    }
                    Object obj = message.obj;
                    MusicActivity.this.onLoadDrawableComplete(obj != null ? (Drawable) obj : null);
                }
            }
        };
        this.mHandler = handler;
        this.mPlayPosTimer.setHandler(handler, 1);
        SingleSecondTimer singleSecondTimer = new SingleSecondTimer(this);
        this.mNetWorkTimer = singleSecondTimer;
        singleSecondTimer.setHandler(this.mHandler, 4);
        CheckDelayTimer checkDelayTimer = new CheckDelayTimer(this);
        this.mCheckDelayTimer = checkDelayTimer;
        checkDelayTimer.setHandler(this.mHandler, 5);
        MusicPlayEngineImpl musicPlayEngineImpl = new MusicPlayEngineImpl(this);
        this.mPlayerEngineImpl = musicPlayEngineImpl;
        musicPlayEngineImpl.setOnBuffUpdateListener(this);
        this.mPlayerEngineImpl.setOnSeekCompleteListener(this);
        this.mPlayerEngineImpl.setDataCaptureListener(this.mUIManager);
        MusicPlayEngineListener musicPlayEngineListener = new MusicPlayEngineListener();
        this.mPlayEngineListener = musicPlayEngineListener;
        this.mPlayerEngineImpl.setPlayerListener(musicPlayEngineListener);
        MediaControlBrocastFactory mediaControlBrocastFactory = new MediaControlBrocastFactory(this.mContext);
        this.mMediaControlBorcastFactory = mediaControlBrocastFactory;
        mediaControlBrocastFactory.register(this);
        this.mNetWorkTimer.startTimer();
        this.mCheckDelayTimer.startTimer();
    }

    private void refreshIntent(Intent intent) {
        log.e("refreshIntent");
        removeExitMessage();
        if (intent != null) {
            this.mMediaInfo = DlnaMediaModelFactory.createFromIntent(intent);
        }
        this.mUIManager.updateMediaInfoView(this.mMediaInfo);
        this.mPlayerEngineImpl.playMedia(this.mMediaInfo);
        LoaderHelper.syncDownLoadDrawable(this.mMediaInfo.getAlbumUri(), this.mHandler, 6);
        this.mUIManager.showPrepareLoadView(true);
        this.mUIManager.showLoadView(false);
        this.mUIManager.showControlView(false);
    }

    private void removeExitMessage() {
        this.mHandler.removeMessages(3);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void delayToExit() {
        log.e("delayToExit");
        removeExitMessage();
        this.mHandler.sendEmptyMessageDelayed(3, 3000L);
    }

    public void play() {
        this.mPlayerEngineImpl.play();
    }

    public void pause() {
        this.mPlayerEngineImpl.pause();
    }

    public void stop() {
        this.mPlayerEngineImpl.stop();
    }

    public void refreshCurPos() {
        int curPosition = this.mPlayerEngineImpl.getCurPosition();
        this.mUIManager.setSeekbarProgress(curPosition);
        DLNAGenaEventBrocastFactory.sendSeekEvent(this.mContext, curPosition);
    }

    public void refreshSpeed() {
        if (this.mUIManager.isLoadViewShow()) {
            this.mUIManager.setSpeed(CommonUtil.getSysNetworkDownloadSpeed());
        }
    }

    public void checkDelay() {
        int curPosition = this.mPlayerEngineImpl.getCurPosition();
        if (this.mCheckDelayTimer.isDelay(curPosition)) {
            this.mUIManager.showLoadView(true);
        } else {
            this.mUIManager.showLoadView(false);
        }
        this.mCheckDelayTimer.setPos(curPosition);
    }

    public void onLoadDrawableComplete(Drawable drawable) {
        if (this.isDestroy || drawable == null) {
            return;
        }
        this.mUIManager.updateAlbumPIC(drawable);
    }

    public void seek(int i) {
        this.isSeekComplete = false;
        this.mPlayerEngineImpl.skipTo(i);
        this.mUIManager.setSeekbarProgress(i);
    }

    private class MusicPlayEngineListener implements PlayerEngineListener {
        private MusicPlayEngineListener() {
        }

        @Override // com.geniusgithub.mediarender.player.PlayerEngineListener
        public void onTrackPlay(DlnaMediaModel dlnaMediaModel) {
            MusicActivity.this.mPlayPosTimer.startTimer();
            DLNAGenaEventBrocastFactory.sendPlayStateEvent(MusicActivity.this.mContext);
            MusicActivity.this.mUIManager.showPlay(false);
            MusicActivity.this.mUIManager.showPrepareLoadView(false);
            MusicActivity.this.mUIManager.showControlView(true);
        }

        @Override // com.geniusgithub.mediarender.player.PlayerEngineListener
        public void onTrackStop(DlnaMediaModel dlnaMediaModel) {
            MusicActivity.this.mPlayPosTimer.stopTimer();
            DLNAGenaEventBrocastFactory.sendStopStateEvent(MusicActivity.this.mContext);
            MusicActivity.this.mUIManager.showPlay(true);
            MusicActivity.this.mUIManager.updateMediaInfoView(MusicActivity.this.mMediaInfo);
            MusicActivity.this.mUIManager.showLoadView(false);
            MusicActivity.this.isSeekComplete = true;
            MusicActivity.this.delayToExit();
        }

        @Override // com.geniusgithub.mediarender.player.PlayerEngineListener
        public void onTrackPause(DlnaMediaModel dlnaMediaModel) {
            MusicActivity.this.mPlayPosTimer.stopTimer();
            DLNAGenaEventBrocastFactory.sendPauseStateEvent(MusicActivity.this.mContext);
            MusicActivity.this.mUIManager.showPlay(true);
        }

        @Override // com.geniusgithub.mediarender.player.PlayerEngineListener
        public void onTrackPrepareSync(DlnaMediaModel dlnaMediaModel) {
            MusicActivity.this.mPlayPosTimer.stopTimer();
            DLNAGenaEventBrocastFactory.sendTranstionEvent(MusicActivity.this.mContext);
        }

        @Override // com.geniusgithub.mediarender.player.PlayerEngineListener
        public void onTrackPrepareComplete(DlnaMediaModel dlnaMediaModel) {
            MusicActivity.this.mPlayPosTimer.stopTimer();
            int duration = MusicActivity.this.mPlayerEngineImpl.getDuration();
            DLNAGenaEventBrocastFactory.sendDurationEvent(MusicActivity.this.mContext, duration);
            MusicActivity.this.mUIManager.setSeekbarMax(duration);
            MusicActivity.this.mUIManager.setTotalTime(duration);
        }

        @Override // com.geniusgithub.mediarender.player.PlayerEngineListener
        public void onTrackStreamError(DlnaMediaModel dlnaMediaModel) {
            MusicActivity.log.e("onTrackStreamError");
            MusicActivity.this.mPlayPosTimer.stopTimer();
            MusicActivity.this.mPlayerEngineImpl.stop();
            MusicActivity.this.mUIManager.showPlayErrorTip();
        }

        @Override // com.geniusgithub.mediarender.player.PlayerEngineListener
        public void onTrackPlayComplete(DlnaMediaModel dlnaMediaModel) {
            MusicActivity.log.e("onTrackPlayComplete");
            MusicActivity.this.mPlayerEngineImpl.stop();
        }
    }

    @Override // android.media.MediaPlayer.OnBufferingUpdateListener
    public void onBufferingUpdate(MediaPlayer mediaPlayer, int i) {
        this.mUIManager.setSeekbarSecondProgress((this.mPlayerEngineImpl.getDuration() * i) / 100);
    }

    @Override // android.media.MediaPlayer.OnSeekCompleteListener
    public void onSeekComplete(MediaPlayer mediaPlayer) {
        this.isSeekComplete = true;
        log.e("onSeekComplete ...");
    }

    @Override // android.media.MediaPlayer.OnErrorListener
    public boolean onError(MediaPlayer mediaPlayer, int i, int i2) {
        this.mUIManager.showPlayErrorTip();
        log.e("onError what = " + i + ", extra = " + i2);
        return false;
    }

    @Override // com.geniusgithub.mediarender.center.MediaControlBrocastFactory.IMediaControlListener
    public void onPlayCommand() {
        play();
    }

    @Override // com.geniusgithub.mediarender.center.MediaControlBrocastFactory.IMediaControlListener
    public void onPauseCommand() {
        pause();
    }

    @Override // com.geniusgithub.mediarender.center.MediaControlBrocastFactory.IMediaControlListener
    public void onStopCommand() {
        stop();
    }

    @Override // com.geniusgithub.mediarender.center.MediaControlBrocastFactory.IMediaControlListener
    public void onSeekCommand(int i) {
        log.e("onSeekCmd time = " + i);
        seek(i);
    }

    class UIManager implements View.OnClickListener, SeekBar.OnSeekBarChangeListener, Visualizer.OnDataCaptureListener {
        private boolean isSeekbarTouch = false;
        public AlphaAnimation mAlphaHideTransformation;
        public ImageButton mBtnPause;
        public ImageButton mBtnPlay;
        public View mControlView;
        public TranslateAnimation mHideDownTransformation;
        public ImageView mIVAlbum;
        public View mLoadView;
        public View mPrepareView;
        public SeekBar mSeekBar;
        public TextView mTVAlbum;
        public TextView mTVArtist;
        public TextView mTVCurTime;
        public TextView mTVLoadSpeed;
        public TextView mTVPrepareSpeed;
        public TextView mTVSongName;
        public TextView mTVTotalTime;
        public VisualizerView mVisualizerView;

        public void unInit() {
        }

        public UIManager() {
            initView();
        }

        public void initView() {
            this.mPrepareView = MusicActivity.this.findViewById(R.id.prepare_panel);
            this.mTVPrepareSpeed = (TextView) MusicActivity.this.findViewById(R.id.tv_prepare_speed);
            this.mLoadView = MusicActivity.this.findViewById(R.id.loading_panel);
            this.mTVLoadSpeed = (TextView) MusicActivity.this.findViewById(R.id.tv_speed);
            this.mControlView = MusicActivity.this.findViewById(R.id.control_panel);
            this.mTVSongName = (TextView) MusicActivity.this.findViewById(R.id.tv_title);
            this.mTVArtist = (TextView) MusicActivity.this.findViewById(R.id.tv_artist);
            this.mTVAlbum = (TextView) MusicActivity.this.findViewById(R.id.tv_album);
            this.mBtnPlay = (ImageButton) MusicActivity.this.findViewById(R.id.btn_play);
            this.mBtnPause = (ImageButton) MusicActivity.this.findViewById(R.id.btn_pause);
            this.mBtnPlay.setOnClickListener(this);
            this.mBtnPause.setOnClickListener(this);
            this.mSeekBar = (SeekBar) MusicActivity.this.findViewById(R.id.playback_seeker);
            this.mTVCurTime = (TextView) MusicActivity.this.findViewById(R.id.tv_curTime);
            this.mTVTotalTime = (TextView) MusicActivity.this.findViewById(R.id.tv_totalTime);
            this.mVisualizerView = (VisualizerView) MusicActivity.this.findViewById(R.id.mp_freq_view);
            this.mIVAlbum = (ImageView) MusicActivity.this.findViewById(R.id.iv_album);
            setSeekbarListener(this);
            TranslateAnimation translateAnimation = new TranslateAnimation(0.0f, 0.0f, 0.0f, 200.0f);
            this.mHideDownTransformation = translateAnimation;
            translateAnimation.setDuration(1000L);
            AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f, 0.0f);
            this.mAlphaHideTransformation = alphaAnimation;
            alphaAnimation.setDuration(1000L);
            updateAlbumPIC(MusicActivity.this.getResources().getDrawable(R.drawable.mp_music_default));
        }

        public void updateAlbumPIC(Drawable drawable) {
            Bitmap bitmapCreateRotateReflectedMap = ImageUtils.createRotateReflectedMap(MusicActivity.this.mContext, drawable);
            if (bitmapCreateRotateReflectedMap != null) {
                this.mIVAlbum.setImageBitmap(bitmapCreateRotateReflectedMap);
            }
        }

        public void showPrepareLoadView(boolean z) {
            if (z) {
                this.mPrepareView.setVisibility(0);
            } else {
                this.mPrepareView.setVisibility(8);
            }
        }

        public void showControlView(boolean z) {
            if (z) {
                this.mControlView.setVisibility(0);
            } else {
                this.mControlView.setVisibility(8);
            }
        }

        public void showLoadView(boolean z) {
            if (z) {
                this.mLoadView.setVisibility(0);
            } else if (this.mLoadView.isShown()) {
                this.mLoadView.startAnimation(this.mAlphaHideTransformation);
                this.mLoadView.setVisibility(8);
            }
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btn_pause /* 2131165279 */:
                    MusicActivity.this.pause();
                    break;
                case R.id.btn_play /* 2131165280 */:
                    MusicActivity.this.play();
                    break;
            }
        }

        @Override // android.widget.SeekBar.OnSeekBarChangeListener
        public void onProgressChanged(SeekBar seekBar, int i, boolean z) {
            MusicActivity.this.mUIManager.setcurTime(i);
        }

        @Override // android.widget.SeekBar.OnSeekBarChangeListener
        public void onStartTrackingTouch(SeekBar seekBar) {
            this.isSeekbarTouch = true;
        }

        @Override // android.widget.SeekBar.OnSeekBarChangeListener
        public void onStopTrackingTouch(SeekBar seekBar) {
            this.isSeekbarTouch = false;
            MusicActivity.this.seek(seekBar.getProgress());
        }

        public void showPlay(boolean z) {
            if (z) {
                this.mBtnPlay.setVisibility(0);
                this.mBtnPause.setVisibility(4);
            } else {
                this.mBtnPlay.setVisibility(4);
                this.mBtnPause.setVisibility(0);
            }
        }

        public void togglePlayPause() {
            if (this.mBtnPlay.isShown()) {
                MusicActivity.this.play();
            } else {
                MusicActivity.this.pause();
            }
        }

        public void setSeekbarProgress(int i) {
            if (this.isSeekbarTouch) {
                return;
            }
            this.mSeekBar.setProgress(i);
        }

        public void setSeekbarSecondProgress(int i) {
            this.mSeekBar.setSecondaryProgress(i);
        }

        public void setSeekbarMax(int i) {
            this.mSeekBar.setMax(i);
        }

        public void setcurTime(int i) {
            this.mTVCurTime.setText(DlnaUtils.formateTime(i));
        }

        public void setTotalTime(int i) {
            this.mTVTotalTime.setText(DlnaUtils.formateTime(i));
        }

        public void updateMediaInfoView(DlnaMediaModel dlnaMediaModel) {
            setcurTime(0);
            setTotalTime(0);
            setSeekbarMax(100);
            setSeekbarProgress(0);
            this.mTVSongName.setText(dlnaMediaModel.getTitle());
            this.mTVArtist.setText(dlnaMediaModel.getArtist());
            this.mTVAlbum.setText(dlnaMediaModel.getAlbum());
        }

        public void setSpeed(float f) {
            String str = ((int) f) + "KB/" + MusicActivity.this.getResources().getString(R.string.second);
            this.mTVPrepareSpeed.setText(str);
            this.mTVLoadSpeed.setText(str);
        }

        public void setSeekbarListener(SeekBar.OnSeekBarChangeListener onSeekBarChangeListener) {
            this.mSeekBar.setOnSeekBarChangeListener(onSeekBarChangeListener);
        }

        public boolean isControlViewShow() {
            return this.mControlView.getVisibility() == 0;
        }

        public boolean isLoadViewShow() {
            return this.mLoadView.getVisibility() == 0 || this.mPrepareView.getVisibility() == 0;
        }

        public void showPlayErrorTip() {
            Toast.makeText(MusicActivity.this, R.string.toast_musicplay_fail, 0).show();
        }

        @Override // android.media.audiofx.Visualizer.OnDataCaptureListener
        public void onFftDataCapture(Visualizer visualizer, byte[] bArr, int i) {
            this.mVisualizerView.updateVisualizer(bArr);
        }

        @Override // android.media.audiofx.Visualizer.OnDataCaptureListener
        public void onWaveFormDataCapture(Visualizer visualizer, byte[] bArr, int i) {
            this.mVisualizerView.updateVisualizer(bArr);
        }
    }
}

