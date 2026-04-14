package com.geniusgithub.mediarender.video;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import com.geniusgithub.mediarender.center.DLNAGenaEventBrocastFactory;
import com.geniusgithub.mediarender.center.DlnaMediaModel;
import com.geniusgithub.mediarender.center.DlnaMediaModelFactory;
import com.geniusgithub.mediarender.center.MediaControlBrocastFactory;
import com.geniusgithub.mediarender.player.AbstractTimer;
import com.geniusgithub.mediarender.player.CheckDelayTimer;
import com.geniusgithub.mediarender.player.PlayerEngineListener;
import com.geniusgithub.mediarender.player.SingleSecondTimer;
import com.geniusgithub.mediarender.player.VideoPlayEngineImpl;
import com.geniusgithub.mediarender.util.CommonLog;
import com.geniusgithub.mediarender.util.CommonUtil;
import com.geniusgithub.mediarender.util.DlnaUtils;
import com.geniusgithub.mediarender.util.LogFactory;
import com.netfeige.R;

/* JADX INFO: loaded from: classes.dex */
public class VideoActivity extends Activity implements MediaControlBrocastFactory.IMediaControlListener, MediaPlayer.OnBufferingUpdateListener, MediaPlayer.OnSeekCompleteListener, MediaPlayer.OnErrorListener {
    private static final int CHECK_DELAY = 5;
    private static final int EXIT_ACTIVITY = 3;
    private static final int EXIT_DELAY_TIME = 2000;
    private static final int HIDE_DELAY_TIME = 3000;
    private static final int HIDE_TOOL = 2;
    private static final int REFRESH_CURPOS = 1;
    private static final int REFRESH_SPEED = 4;
    private static final CommonLog log = LogFactory.createLog();
    private CheckDelayTimer mCheckDelayTimer;
    private Context mContext;
    private Handler mHandler;
    private MediaControlBrocastFactory mMediaControlBorcastFactory;
    private AbstractTimer mNetWorkTimer;
    private VideoPlayEngineListener mPlayEngineListener;
    private AbstractTimer mPlayPosTimer;
    private VideoPlayEngineImpl mPlayerEngineImpl;
    private UIManager mUIManager;
    private DlnaMediaModel mMediaInfo = new DlnaMediaModel();
    private boolean isSurfaceCreate = false;
    private boolean isDestroy = false;
    private boolean isSeekComplete = false;

    @Override // android.app.Activity
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        log.e("onCreate");
        getWindow().addFlags(128);
        setContentView(R.layout.video_player_layout);
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
        onDestroy();
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
        Handler handler = new Handler() { // from class: com.geniusgithub.mediarender.video.VideoActivity.1
            @Override // android.os.Handler
            public void handleMessage(Message message) {
                int i = message.what;
                if (i == 1) {
                    VideoActivity.this.refreshCurPos();
                    return;
                }
                if (i == 2) {
                    if (VideoActivity.this.mPlayerEngineImpl.isPause()) {
                        return;
                    }
                    VideoActivity.this.mUIManager.showControlView(false);
                } else if (i == 3) {
                    VideoActivity.this.finish();
                } else if (i == 4) {
                    VideoActivity.this.refreshSpeed();
                } else {
                    if (i != 5) {
                        return;
                    }
                    VideoActivity.this.checkDelay();
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
        VideoPlayEngineImpl videoPlayEngineImpl = new VideoPlayEngineImpl(this, this.mUIManager.holder);
        this.mPlayerEngineImpl = videoPlayEngineImpl;
        videoPlayEngineImpl.setOnBuffUpdateListener(this);
        this.mPlayerEngineImpl.setOnSeekCompleteListener(this);
        VideoPlayEngineListener videoPlayEngineListener = new VideoPlayEngineListener();
        this.mPlayEngineListener = videoPlayEngineListener;
        this.mPlayerEngineImpl.setPlayerListener(videoPlayEngineListener);
        MediaControlBrocastFactory mediaControlBrocastFactory = new MediaControlBrocastFactory(this.mContext);
        this.mMediaControlBorcastFactory = mediaControlBrocastFactory;
        mediaControlBrocastFactory.register(this);
        this.mNetWorkTimer.startTimer();
        this.mCheckDelayTimer.startTimer();
    }

    private void refreshIntent(Intent intent) {
        removeExitMessage();
        if (intent != null) {
            this.mMediaInfo = DlnaMediaModelFactory.createFromIntent(intent);
        }
        this.mUIManager.updateMediaInfoView(this.mMediaInfo);
        if (this.isSurfaceCreate) {
            this.mPlayerEngineImpl.playMedia(this.mMediaInfo);
        } else {
            delayToPlayMedia(this.mMediaInfo);
        }
        this.mUIManager.showPrepareLoadView(true);
        this.mUIManager.showLoadView(false);
        this.mUIManager.showControlView(false);
    }

    @Override // android.app.Activity, android.view.Window.Callback
    public boolean dispatchTouchEvent(MotionEvent motionEvent) {
        int action = motionEvent.getAction();
        int actionIndex = motionEvent.getActionIndex();
        motionEvent.getActionMasked();
        if (actionIndex == 0 && action == 1) {
            if (!this.mUIManager.isControlViewShow()) {
                this.mUIManager.showControlView(true);
                return true;
            }
            delayToHideControlPanel();
        }
        return super.dispatchTouchEvent(motionEvent);
    }

    private void delayToPlayMedia(final DlnaMediaModel dlnaMediaModel) {
        this.mHandler.postDelayed(new Runnable() { // from class: com.geniusgithub.mediarender.video.VideoActivity.2
            @Override // java.lang.Runnable
            public void run() {
                if (!VideoActivity.this.isDestroy) {
                    VideoActivity.this.mPlayerEngineImpl.playMedia(dlnaMediaModel);
                } else {
                    VideoActivity.log.e("activity destroy...so don't playMedia...");
                }
            }
        }, 1000L);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void removeHideMessage() {
        this.mHandler.removeMessages(2);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void delayToHideControlPanel() {
        removeHideMessage();
        this.mHandler.sendEmptyMessageDelayed(2, 3000L);
    }

    private void removeExitMessage() {
        this.mHandler.removeMessages(3);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void delayToExit() {
        removeExitMessage();
        this.mHandler.sendEmptyMessageDelayed(3, 2000L);
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

    public void seek(int i) {
        this.isSeekComplete = false;
        this.mPlayerEngineImpl.skipTo(i);
        this.mUIManager.setSeekbarProgress(i);
    }

    private class VideoPlayEngineListener implements PlayerEngineListener {
        private VideoPlayEngineListener() {
        }

        @Override // com.geniusgithub.mediarender.player.PlayerEngineListener
        public void onTrackPlay(DlnaMediaModel dlnaMediaModel) {
            VideoActivity.this.mPlayPosTimer.startTimer();
            DLNAGenaEventBrocastFactory.sendPlayStateEvent(VideoActivity.this.mContext);
            VideoActivity.this.mUIManager.showPlay(false);
            VideoActivity.this.mUIManager.showControlView(true);
        }

        @Override // com.geniusgithub.mediarender.player.PlayerEngineListener
        public void onTrackStop(DlnaMediaModel dlnaMediaModel) {
            VideoActivity.this.mPlayPosTimer.stopTimer();
            DLNAGenaEventBrocastFactory.sendStopStateEvent(VideoActivity.this.mContext);
            VideoActivity.this.mUIManager.showPlay(true);
            VideoActivity.this.mUIManager.updateMediaInfoView(VideoActivity.this.mMediaInfo);
            VideoActivity.this.mUIManager.showControlView(true);
            VideoActivity.this.mUIManager.showLoadView(false);
            VideoActivity.this.isSeekComplete = true;
            VideoActivity.this.delayToExit();
        }

        @Override // com.geniusgithub.mediarender.player.PlayerEngineListener
        public void onTrackPause(DlnaMediaModel dlnaMediaModel) {
            VideoActivity.this.mPlayPosTimer.stopTimer();
            DLNAGenaEventBrocastFactory.sendPauseStateEvent(VideoActivity.this.mContext);
            VideoActivity.this.mUIManager.showPlay(true);
            VideoActivity.this.mUIManager.showControlView();
        }

        @Override // com.geniusgithub.mediarender.player.PlayerEngineListener
        public void onTrackPrepareSync(DlnaMediaModel dlnaMediaModel) {
            VideoActivity.this.mPlayPosTimer.stopTimer();
            DLNAGenaEventBrocastFactory.sendTranstionEvent(VideoActivity.this.mContext);
        }

        @Override // com.geniusgithub.mediarender.player.PlayerEngineListener
        public void onTrackPrepareComplete(DlnaMediaModel dlnaMediaModel) {
            VideoActivity.this.mPlayPosTimer.stopTimer();
            int duration = VideoActivity.this.mPlayerEngineImpl.getDuration();
            DLNAGenaEventBrocastFactory.sendDurationEvent(VideoActivity.this.mContext, duration);
            VideoActivity.this.mUIManager.setSeekbarMax(duration);
            VideoActivity.this.mUIManager.setTotalTime(duration);
        }

        @Override // com.geniusgithub.mediarender.player.PlayerEngineListener
        public void onTrackStreamError(DlnaMediaModel dlnaMediaModel) {
            VideoActivity.log.e("onTrackStreamError");
            VideoActivity.this.mPlayPosTimer.stopTimer();
            VideoActivity.this.mPlayerEngineImpl.stop();
            VideoActivity.this.mUIManager.showPlayErrorTip();
        }

        @Override // com.geniusgithub.mediarender.player.PlayerEngineListener
        public void onTrackPlayComplete(DlnaMediaModel dlnaMediaModel) {
            VideoActivity.log.e("onTrackPlayComplete");
            VideoActivity.this.mPlayerEngineImpl.stop();
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
        this.mUIManager.showControlView(true);
        seek(i);
    }

    class UIManager implements View.OnClickListener, SurfaceHolder.Callback, SeekBar.OnSeekBarChangeListener {
        private SurfaceHolder holder = null;
        private boolean isSeekbarTouch = false;
        private AlphaAnimation mAlphaHideTransformation;
        public ImageButton mBtnPause;
        public ImageButton mBtnPlay;
        public View mControlView;
        public View mDownToolView;
        private TranslateAnimation mHideDownTransformation;
        private TranslateAnimation mHideUpTransformation;
        public View mLoadView;
        public View mPrepareView;
        public SeekBar mSeekBar;
        private SurfaceView mSurfaceView;
        public TextView mTVCurTime;
        public TextView mTVLoadSpeed;
        public TextView mTVPrepareSpeed;
        public TextView mTVTitle;
        public TextView mTVTotalTime;
        public View mUpToolView;

        @Override // android.view.SurfaceHolder.Callback
        public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i2, int i3) {
        }

        public void unInit() {
        }

        public UIManager() {
            initView();
        }

        public void initView() {
            this.mPrepareView = VideoActivity.this.findViewById(R.id.prepare_panel);
            this.mTVPrepareSpeed = (TextView) VideoActivity.this.findViewById(R.id.tv_prepare_speed);
            this.mLoadView = VideoActivity.this.findViewById(R.id.loading_panel);
            this.mTVLoadSpeed = (TextView) VideoActivity.this.findViewById(R.id.tv_speed);
            this.mControlView = VideoActivity.this.findViewById(R.id.control_panel);
            this.mUpToolView = VideoActivity.this.findViewById(R.id.up_toolview);
            this.mDownToolView = VideoActivity.this.findViewById(R.id.down_toolview);
            this.mTVTitle = (TextView) VideoActivity.this.findViewById(R.id.tv_title);
            this.mBtnPlay = (ImageButton) VideoActivity.this.findViewById(R.id.btn_play);
            this.mBtnPause = (ImageButton) VideoActivity.this.findViewById(R.id.btn_pause);
            this.mBtnPlay.setOnClickListener(this);
            this.mBtnPause.setOnClickListener(this);
            this.mSeekBar = (SeekBar) VideoActivity.this.findViewById(R.id.playback_seeker);
            this.mTVCurTime = (TextView) VideoActivity.this.findViewById(R.id.tv_curTime);
            this.mTVTotalTime = (TextView) VideoActivity.this.findViewById(R.id.tv_totalTime);
            setSeekbarListener(this);
            SurfaceView surfaceView = (SurfaceView) VideoActivity.this.findViewById(R.id.surfaceView);
            this.mSurfaceView = surfaceView;
            SurfaceHolder holder = surfaceView.getHolder();
            this.holder = holder;
            holder.addCallback(this);
            this.holder.setType(3);
            TranslateAnimation translateAnimation = new TranslateAnimation(0.0f, 0.0f, 0.0f, 200.0f);
            this.mHideDownTransformation = translateAnimation;
            translateAnimation.setDuration(1000L);
            AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f, 0.0f);
            this.mAlphaHideTransformation = alphaAnimation;
            alphaAnimation.setDuration(1000L);
            TranslateAnimation translateAnimation2 = new TranslateAnimation(0.0f, 0.0f, 0.0f, -124.0f);
            this.mHideUpTransformation = translateAnimation2;
            translateAnimation2.setDuration(1000L);
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
                this.mUpToolView.setVisibility(0);
                this.mDownToolView.setVisibility(0);
                this.mPrepareView.setVisibility(8);
                VideoActivity.this.delayToHideControlPanel();
                return;
            }
            if (this.mDownToolView.isShown()) {
                this.mDownToolView.startAnimation(this.mHideDownTransformation);
                this.mUpToolView.startAnimation(this.mHideUpTransformation);
                this.mUpToolView.setVisibility(8);
                this.mDownToolView.setVisibility(8);
            }
        }

        public void showControlView() {
            VideoActivity.this.removeHideMessage();
            this.mUpToolView.setVisibility(0);
            this.mDownToolView.setVisibility(0);
        }

        public void showLoadView(boolean z) {
            if (z) {
                this.mLoadView.setVisibility(0);
            } else if (this.mLoadView.isShown()) {
                this.mLoadView.startAnimation(this.mAlphaHideTransformation);
                this.mLoadView.setVisibility(8);
            }
        }

        @Override // android.view.SurfaceHolder.Callback
        public void surfaceCreated(SurfaceHolder surfaceHolder) {
            VideoActivity.this.isSurfaceCreate = true;
        }

        @Override // android.view.SurfaceHolder.Callback
        public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
            VideoActivity.this.isSurfaceCreate = false;
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btn_pause /* 2131165279 */:
                    VideoActivity.this.pause();
                    break;
                case R.id.btn_play /* 2131165280 */:
                    VideoActivity.this.play();
                    break;
            }
        }

        @Override // android.widget.SeekBar.OnSeekBarChangeListener
        public void onProgressChanged(SeekBar seekBar, int i, boolean z) {
            VideoActivity.this.mUIManager.setcurTime(i);
        }

        @Override // android.widget.SeekBar.OnSeekBarChangeListener
        public void onStartTrackingTouch(SeekBar seekBar) {
            this.isSeekbarTouch = true;
        }

        @Override // android.widget.SeekBar.OnSeekBarChangeListener
        public void onStopTrackingTouch(SeekBar seekBar) {
            this.isSeekbarTouch = false;
            VideoActivity.this.seek(seekBar.getProgress());
            VideoActivity.this.mUIManager.showControlView(true);
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
                VideoActivity.this.play();
            } else {
                VideoActivity.this.pause();
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
            this.mTVTitle.setText(dlnaMediaModel.getTitle());
        }

        public void setSpeed(float f) {
            String str = ((int) f) + "KB/" + VideoActivity.this.getResources().getString(R.string.second);
            this.mTVPrepareSpeed.setText(str);
            this.mTVLoadSpeed.setText(str);
        }

        public void setSeekbarListener(SeekBar.OnSeekBarChangeListener onSeekBarChangeListener) {
            this.mSeekBar.setOnSeekBarChangeListener(onSeekBarChangeListener);
        }

        public boolean isControlViewShow() {
            return this.mDownToolView.getVisibility() == 0;
        }

        public boolean isLoadViewShow() {
            return this.mLoadView.getVisibility() == 0 || this.mPrepareView.getVisibility() == 0;
        }

        public void showPlayErrorTip() {
            Toast.makeText(VideoActivity.this, R.string.toast_videoplay_fail, 0).show();
        }
    }
}

