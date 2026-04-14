package com.netfeige.display.ui;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import com.netfeige.R;
import com.netfeige.common.Global;
import com.netfeige.common.Music;
import com.netfeige.common.Public_Tools;
import com.netfeige.display.data.FileAdapter;
import com.netfeige.display.data.IpmsgApplication;
import com.netfeige.display.ui.dialog.LastAudioDialog;
import com.netfeige.display.ui.dialog.ShowAudioDeleteDialog;
import com.netfeige.dlna.ContentTree;
import com.netfeige.enums.MediaOperateState;
import com.netfeige.filemanager.FileManager;
import com.netfeige.kits.DataConfig;
import com.netfeige.protocol.IProtocol;
import java.io.File;
import java.util.Formatter;
import java.util.Locale;
import java.util.Vector;

/* JADX INFO: loaded from: classes.dex */
public class PlayVideoActivity extends Activity implements MediaPlayer.OnCompletionListener, MediaPlayer.OnPreparedListener, MediaPlayer.OnVideoSizeChangedListener, SurfaceHolder.Callback {
    private static final int FADE_OUT = 1;
    private static final int QUICK_BACK = 3;
    private static final int QUICK_FORWARD = 4;
    private static final int SHOW_PROGRESS = 2;
    private static final int sDefaultTimeout = 1000;
    public static Boolean s_bIsLoop = true;
    public static int s_nCurrentIndex = 0;
    private Button m_btnBack;
    private float m_fScaleRate;
    StringBuilder m_formatBuilder;
    Formatter m_formatter;
    private int m_iScreenHeight;
    private int m_iScreenWidth;
    private int m_iVideoHeight;
    private int m_iVideoWidth;
    private ImageButton m_imgBtnNext;
    private ImageButton m_imgBtnPlay;
    private ImageButton m_imgBtnPrev;
    private ImageButton m_imgBtnQuickBack;
    private ImageButton m_imgBtnQuickForward;
    private LastAudioDialog m_lastAudioDialog;
    private LinearLayout m_linearLBackgroundPlay;
    private LinearLayout m_linearLBottom;
    private LinearLayout m_linearLDelete;
    private LinearLayout m_linearLDelete2;
    private LinearLayout m_linearLPopupSend;
    private LinearLayout m_linearLSend;
    private LinearLayout m_linearLShare;
    private LinearLayout m_linearLShowTo;
    private LinearLayout m_linearLTop;
    private MediaPlayer m_mediaPlayer;
    private IpmsgApplication m_myApp;
    private int m_nPopX;
    private PopupWindow m_popupWindow;
    private PopupWindow m_popupWindow2;
    private GestureDetector m_relativeLGestureDetector;
    private SeekBar m_seekBar;
    private ShowAudioDeleteDialog m_showAudioDeleteDialog;
    private String m_strFilePath;
    private SurfaceHolder m_surfaceHolder;
    private SurfaceView m_surfaceVVideo;
    private TextView m_textDurationTime;
    private TextView m_textFileName;
    private TextView m_textThroughTime;
    private boolean m_isVideoSizeKnown = false;
    private boolean m_showing = false;
    private MediaOperateState m_mediaStateCurrent = MediaOperateState.STATE_IDLE;
    private AudioManager m_audioManager = null;
    private Button m_btnMore = null;
    private LayoutInflater m_inflater = null;
    private final int m_nPopUpX = 5;
    private int m_iThroughTime = 0;
    private View.OnClickListener m_pauseListener = new View.OnClickListener() { // from class: com.netfeige.display.ui.PlayVideoActivity.9
        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            PlayVideoActivity.this.doPauseResume(true);
        }
    };
    private Handler m_handler = new Handler() { // from class: com.netfeige.display.ui.PlayVideoActivity.10
        @Override // android.os.Handler
        public void handleMessage(Message message) {
            int i = message.what;
            if (i == 1) {
                PlayVideoActivity.this.hide();
                return;
            }
            if (i == 2) {
                PlayVideoActivity.this.setProgress();
                if (PlayVideoActivity.this.m_mediaPlayer.isPlaying()) {
                    sendMessageDelayed(obtainMessage(2), 1000L);
                    return;
                }
                return;
            }
            if (i == 3) {
                PlayVideoActivity.this.modifySeekBar(-30);
                sendEmptyMessageDelayed(3, 100L);
            } else {
                if (i != 4) {
                    return;
                }
                PlayVideoActivity.this.modifySeekBar(30);
                sendEmptyMessageDelayed(4, 100L);
            }
        }
    };

    @Override // android.view.SurfaceHolder.Callback
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i2, int i3) {
    }

    @Override // android.app.Activity
    protected void onCreate(Bundle bundle) {
        setContentView(R.layout.activity_video);
        this.m_myApp = (IpmsgApplication) getApplication();
        s_nCurrentIndex = getIntent().getExtras().getInt("currentIndex");
        if (bundle != null) {
            s_nCurrentIndex = bundle.getInt("currentIndex");
            this.m_iThroughTime = bundle.getInt("throughTime");
        }
        int i = s_nCurrentIndex;
        if (i < 0 || i >= IpmsgApplication.g_playingVideoList.size() - 1) {
            s_nCurrentIndex = 0;
        } else if (s_nCurrentIndex >= IpmsgApplication.g_playingVideoList.size()) {
            s_nCurrentIndex = IpmsgApplication.g_playingVideoList.size() - 1;
        }
        this.m_strFilePath = IpmsgApplication.g_playingVideoList.get(s_nCurrentIndex).getPath();
        this.m_inflater = (LayoutInflater) getSystemService("layout_inflater");
        initControl();
        this.m_nPopX = Public_Tools.dip2px(this, 5.0f);
        initPopupWindow();
        initPopupWindow2();
        this.m_showAudioDeleteDialog = new ShowAudioDeleteDialog(this);
        this.m_lastAudioDialog = new LastAudioDialog(this);
        super.onCreate(bundle);
    }

    @Override // android.app.Activity
    protected void onResume() {
        show(1000);
        this.m_myApp.currentActivity = this;
        if (this.m_myApp.g_bBackRuning) {
            this.m_myApp.g_bBackRuning = false;
            ((NotificationManager) getSystemService("notification")).cancel(IpmsgApplication.MAIN_NOTIFICATION_ID);
            if (this.m_myApp.g_strMsgNotification.equals(ContentTree.VIDEO_ID)) {
                ((NotificationManager) getSystemService("notification")).cancel(IpmsgApplication.MSG_NOTIFICATION_ID);
            }
        }
        super.onResume();
// Umeng removed:         // Umeng removed: MobclickAgent.onResume(this);
    }

    private void initControl() {
        this.m_iScreenWidth = getWindow().getWindowManager().getDefaultDisplay().getWidth();
        this.m_iScreenHeight = getWindow().getWindowManager().getDefaultDisplay().getHeight();
        Button button = (Button) findViewById(R.id.back_btn_activity_video);
        this.m_btnBack = button;
        button.setOnClickListener(new View.OnClickListener() { // from class: com.netfeige.display.ui.PlayVideoActivity.1
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                PlayVideoActivity.this.onBackPressed();
            }
        });
        Button button2 = (Button) findViewById(R.id.more_btn_activity_video);
        this.m_btnMore = button2;
        button2.setOnClickListener(new View.OnClickListener() { // from class: com.netfeige.display.ui.PlayVideoActivity.2
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                PlayVideoActivity.this.openCustomMenu();
            }
        });
        TextView textView = (TextView) findViewById(R.id.title_text_activity_video);
        this.m_textFileName = textView;
        textView.setText(Public_Tools.getFileName(this.m_strFilePath));
        TextView textView2 = (TextView) findViewById(R.id.through_time_text_activity_video);
        this.m_textThroughTime = textView2;
        textView2.setText("00:00");
        this.m_textDurationTime = (TextView) findViewById(R.id.duration_time_text_activity_video);
        this.m_formatBuilder = new StringBuilder();
        this.m_formatter = new Formatter(this.m_formatBuilder, Locale.getDefault());
        SeekBar seekBar = (SeekBar) findViewById(R.id.seek_activity_video);
        this.m_seekBar = seekBar;
        seekBar.setMax(1000);
        this.m_seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListenerImpl());
        ImageButton imageButton = (ImageButton) findViewById(R.id.play_btn_activity_video);
        this.m_imgBtnPlay = imageButton;
        imageButton.setOnClickListener(this.m_pauseListener);
        ImageButton imageButton2 = (ImageButton) findViewById(R.id.previous_btn_activity_video);
        this.m_imgBtnPrev = imageButton2;
        imageButton2.setOnClickListener(new View.OnClickListener() { // from class: com.netfeige.display.ui.PlayVideoActivity.3
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                PlayVideoActivity.this.prevVideo();
            }
        });
        ImageButton imageButton3 = (ImageButton) findViewById(R.id.next_btn_activity_video);
        this.m_imgBtnNext = imageButton3;
        imageButton3.setOnClickListener(new View.OnClickListener() { // from class: com.netfeige.display.ui.PlayVideoActivity.4
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                PlayVideoActivity.this.nextVideo();
            }
        });
        ImageButton imageButton4 = (ImageButton) findViewById(R.id.quick_back_btn_activity_video);
        this.m_imgBtnQuickBack = imageButton4;
        imageButton4.setOnKeyListener(new View.OnKeyListener() { // from class: com.netfeige.display.ui.PlayVideoActivity.5
            private boolean isDown = false;

            @Override // android.view.View.OnKeyListener
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (i != 23) {
                    return false;
                }
                this.isDown = PlayVideoActivity.this.quickBackOrForward(keyEvent.getAction(), this.isDown, 3);
                return true;
            }
        });
        this.m_imgBtnQuickBack.setOnTouchListener(new View.OnTouchListener() { // from class: com.netfeige.display.ui.PlayVideoActivity.6
            @Override // android.view.View.OnTouchListener
            public boolean onTouch(View view, MotionEvent motionEvent) {
                PlayVideoActivity.this.quickBackOrForward(motionEvent.getAction(), false, 3);
                return true;
            }
        });
        ImageButton imageButton5 = (ImageButton) findViewById(R.id.quick_forward_btn_activity_video);
        this.m_imgBtnQuickForward = imageButton5;
        imageButton5.setOnKeyListener(new View.OnKeyListener() { // from class: com.netfeige.display.ui.PlayVideoActivity.7
            private boolean isDown = false;

            @Override // android.view.View.OnKeyListener
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (i != 23) {
                    return false;
                }
                this.isDown = PlayVideoActivity.this.quickBackOrForward(keyEvent.getAction(), this.isDown, 4);
                return true;
            }
        });
        this.m_imgBtnQuickForward.setOnTouchListener(new View.OnTouchListener() { // from class: com.netfeige.display.ui.PlayVideoActivity.8
            @Override // android.view.View.OnTouchListener
            public boolean onTouch(View view, MotionEvent motionEvent) {
                PlayVideoActivity.this.quickBackOrForward(motionEvent.getAction(), false, 4);
                return true;
            }
        });
        SurfaceView surfaceView = (SurfaceView) findViewById(R.id.video_surface_activity_video);
        this.m_surfaceVVideo = surfaceView;
        surfaceView.requestFocus();
        this.m_surfaceVVideo.setOnClickListener(this.m_pauseListener);
        this.m_surfaceVVideo.getHolder().addCallback(this);
        this.m_linearLTop = (LinearLayout) findViewById(R.id.top_llayout_activity_video);
        this.m_linearLBottom = (LinearLayout) findViewById(R.id.bottom_llayout_activity_video);
    }

    @Override // android.app.Activity
    protected void onPause() {
        if (this.m_mediaStateCurrent == MediaOperateState.STATE_PLAYING) {
            doPauseResume(true);
        }
        super.onPause();
// Umeng removed:         // Umeng removed: MobclickAgent.onPause(this);
    }

    @Override // android.app.Activity
    protected void onStop() {
        if (this.m_myApp.currentActivity == this) {
            this.m_myApp.g_bBackRuning = true;
            Public_Tools.showNotification(this.m_myApp, getString(R.string.app_name), DataConfig.getInstance(this).Read(0), R.drawable.ic_launcher);
        }
        super.onStop();
    }

    @Override // android.app.Activity
    protected void onDestroy() {
        this.m_handler.removeMessages(2);
        releaseMediaPlayer();
        super.onDestroy();
    }

    @Override // android.app.Activity, android.view.KeyEvent.Callback
    public boolean onKeyDown(int i, KeyEvent keyEvent) {
        if (keyEvent.getKeyCode() == 82) {
            openCustomMenu();
        }
        return super.onKeyDown(i, keyEvent);
    }

    @Override // android.media.MediaPlayer.OnCompletionListener
    public void onCompletion(MediaPlayer mediaPlayer) {
        nextVideo();
    }

    @Override // android.media.MediaPlayer.OnVideoSizeChangedListener
    public void onVideoSizeChanged(MediaPlayer mediaPlayer, int i, int i2) {
        this.m_isVideoSizeKnown = true;
        this.m_iVideoWidth = mediaPlayer.getVideoWidth();
        int videoHeight = mediaPlayer.getVideoHeight();
        this.m_iVideoHeight = videoHeight;
        this.m_fScaleRate = Math.min(this.m_iScreenWidth / this.m_iVideoWidth, this.m_iScreenHeight / videoHeight);
        startVideoPlay();
    }

    @Override // android.media.MediaPlayer.OnPreparedListener
    public void onPrepared(MediaPlayer mediaPlayer) {
        this.m_mediaStateCurrent = MediaOperateState.STATE_PREPARED;
        startVideoPlay();
    }

    @Override // android.view.SurfaceHolder.Callback
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        this.m_surfaceHolder = surfaceHolder;
        openVideo();
    }

    @Override // android.view.SurfaceHolder.Callback
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        this.m_surfaceHolder = null;
    }

    private void openVideo() {
        SurfaceHolder surfaceHolder = this.m_surfaceHolder;
        if (surfaceHolder == null) {
            return;
        }
        try {
            if (this.m_mediaPlayer == null) {
                MediaPlayer mediaPlayer = new MediaPlayer();
                this.m_mediaPlayer = mediaPlayer;
                mediaPlayer.setDataSource(this.m_strFilePath);
                this.m_mediaPlayer.setDisplay(this.m_surfaceHolder);
                this.m_mediaPlayer.prepare();
                this.m_mediaPlayer.seekTo(this.m_iThroughTime);
                this.m_mediaPlayer.setOnPreparedListener(this);
                this.m_mediaPlayer.setOnVideoSizeChangedListener(this);
                this.m_mediaPlayer.setOnCompletionListener(this);
                this.m_mediaPlayer.setAudioStreamType(3);
                this.m_mediaStateCurrent = MediaOperateState.STATE_PREPARING;
            } else {
                this.m_mediaPlayer.setDisplay(surfaceHolder);
                this.m_mediaStateCurrent = MediaOperateState.STATE_PREPARED;
                startVideoPlay();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startVideoPlay() {
        if (this.m_isVideoSizeKnown && this.m_mediaStateCurrent == MediaOperateState.STATE_PREPARED) {
            SurfaceHolder surfaceHolder = this.m_surfaceHolder;
            float f = this.m_iVideoWidth;
            float f2 = this.m_fScaleRate;
            surfaceHolder.setFixedSize((int) (f * f2), (int) (this.m_iVideoHeight * f2));
            this.m_mediaPlayer.start();
            this.m_handler.sendEmptyMessage(2);
            this.m_mediaStateCurrent = MediaOperateState.STATE_PLAYING;
            updatePausePlay();
        }
    }

    private void releaseMediaPlayer() {
        MediaPlayer mediaPlayer = this.m_mediaPlayer;
        if (mediaPlayer != null) {
            mediaPlayer.release();
            this.m_mediaPlayer = null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void doPauseResume(boolean z) {
        if (this.m_mediaPlayer.isPlaying()) {
            this.m_mediaPlayer.pause();
            this.m_mediaStateCurrent = MediaOperateState.STATE_PAUSED;
            show(0);
            this.m_handler.removeMessages(2);
        } else {
            this.m_mediaPlayer.start();
            this.m_handler.sendEmptyMessage(2);
            this.m_mediaStateCurrent = MediaOperateState.STATE_PLAYING;
            hide();
        }
        if (z) {
            updatePausePlay();
        }
    }

    private void updatePausePlay() {
        if (this.m_mediaPlayer.isPlaying()) {
            this.m_imgBtnPlay.setVisibility(8);
            this.m_surfaceVVideo.requestFocus();
        } else {
            this.m_imgBtnPlay.setVisibility(0);
            this.m_imgBtnPlay.requestFocus();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int setProgress() {
        MediaPlayer mediaPlayer = this.m_mediaPlayer;
        if (mediaPlayer == null) {
            return 0;
        }
        int currentPosition = mediaPlayer.getCurrentPosition();
        int duration = this.m_mediaPlayer.getDuration();
        SeekBar seekBar = this.m_seekBar;
        if (seekBar != null && duration > 0) {
            seekBar.setProgress((int) ((((long) currentPosition) * 1000) / ((long) duration)));
        }
        TextView textView = this.m_textDurationTime;
        if (textView != null) {
            textView.setText(stringForTime(duration));
        }
        TextView textView2 = this.m_textThroughTime;
        if (textView2 != null) {
            textView2.setText(stringForTime(currentPosition));
        }
        return currentPosition;
    }

    private String stringForTime(int i) {
        int i2 = i / 1000;
        int i3 = i2 % 60;
        int i4 = (i2 / 60) % 60;
        int i5 = i2 / 3600;
        this.m_formatBuilder.setLength(0);
        return i5 > 0 ? this.m_formatter.format("%d:%02d:%02d", Integer.valueOf(i5), Integer.valueOf(i4), Integer.valueOf(i3)).toString() : this.m_formatter.format("%02d:%02d", Integer.valueOf(i4), Integer.valueOf(i3)).toString();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void prevVideo() {
        int i = s_nCurrentIndex;
        if (i == 0) {
            s_nCurrentIndex = IpmsgApplication.g_playingVideoList.size() - 1;
        } else {
            s_nCurrentIndex = i - 1;
        }
        switchVideo(true);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void nextVideo() {
        if (s_nCurrentIndex >= IpmsgApplication.g_playingVideoList.size() - 1) {
            s_nCurrentIndex = 0;
        } else {
            s_nCurrentIndex++;
        }
        switchVideo(false);
    }

    private void switchVideo(boolean z) {
        Music music = IpmsgApplication.g_playingVideoList.get(s_nCurrentIndex);
        if (music.getTime() == -1) {
            music.setTime(Public_Tools.getDuration(music.getPath()));
        }
        if (music.getTime() == 0) {
            if (z) {
                prevVideo();
                return;
            } else {
                nextVideo();
                return;
            }
        }
        try {
            String path = music.getPath();
            this.m_strFilePath = path;
            this.m_textFileName.setText(Public_Tools.getFileName(path));
            this.m_textDurationTime.setText(stringForTime((int) music.getTime()));
            this.m_mediaPlayer.reset();
            this.m_mediaPlayer.setDataSource(this.m_strFilePath);
            this.m_mediaPlayer.prepare();
            this.m_mediaStateCurrent = MediaOperateState.STATE_PLAYING;
            hide();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initPopupWindow() {
        View viewInflate = this.m_inflater.inflate(R.layout.popupaudiopreview, (ViewGroup) null);
        PopupWindow popupWindow = new PopupWindow(viewInflate, -2, -2);
        this.m_popupWindow = popupWindow;
        popupWindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.popup4));
        this.m_popupWindow.setFocusable(true);
        this.m_popupWindow.setOutsideTouchable(true);
        this.m_popupWindow.update();
        LinearLayout linearLayout = (LinearLayout) viewInflate.findViewById(R.id.linearl_send);
        this.m_linearLPopupSend = linearLayout;
        linearLayout.setOnClickListener(new PopUpWindowSendOnClickListener());
        LinearLayout linearLayout2 = (LinearLayout) viewInflate.findViewById(R.id.linearl_showto);
        this.m_linearLShowTo = linearLayout2;
        linearLayout2.setOnClickListener(new BtnPlayToOnClickListener());
        LinearLayout linearLayout3 = (LinearLayout) viewInflate.findViewById(R.id.linearl_print);
        this.m_linearLBackgroundPlay = linearLayout3;
        linearLayout3.setVisibility(8);
        LinearLayout linearLayout4 = (LinearLayout) viewInflate.findViewById(R.id.linearl_share);
        this.m_linearLShare = linearLayout4;
        linearLayout4.setOnClickListener(new PopUpWindowShareOnClickListener());
        LinearLayout linearLayout5 = (LinearLayout) viewInflate.findViewById(R.id.linearl_delete);
        this.m_linearLDelete = linearLayout5;
        linearLayout5.setOnClickListener(new BtnDeleteOnClickListener());
    }

    private class PopUpWindowSendOnClickListener implements View.OnClickListener {
        private PopUpWindowSendOnClickListener() {
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            PlayVideoActivity.this.m_popupWindow.dismiss();
            Intent intent = new Intent(PlayVideoActivity.this, (Class<?>) ChoiceUserActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("filePath", IpmsgApplication.g_playingVideoList.get(PlayVideoActivity.s_nCurrentIndex).getPath());
            bundle.putString("activity", "PlayMediaActivity");
            intent.putExtras(bundle);
            PlayVideoActivity.this.startActivity(intent);
        }
    }

    private class BtnPlayToOnClickListener implements View.OnClickListener {
        private BtnPlayToOnClickListener() {
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            PlayVideoActivity.this.m_popupWindow.dismiss();
            Intent intent = new Intent(PlayVideoActivity.this, (Class<?>) ChoiceRemotePlayerActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("filePath", IpmsgApplication.g_playingVideoList.get(PlayVideoActivity.s_nCurrentIndex).getPath());
            intent.putExtras(bundle);
            PlayVideoActivity.this.startActivity(intent);
        }
    }

    private class PopUpWindowShareOnClickListener implements View.OnClickListener {
        private PopUpWindowShareOnClickListener() {
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            Public_Tools.share(PlayVideoActivity.this, IpmsgApplication.g_playingVideoList.get(PlayVideoActivity.s_nCurrentIndex).getPath());
            PlayVideoActivity.this.m_popupWindow.dismiss();
        }
    }

    private class BtnDeleteOnClickListener implements View.OnClickListener {
        private BtnDeleteOnClickListener() {
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            if (PlayVideoActivity.this.m_popupWindow.isShowing()) {
                PlayVideoActivity.this.m_popupWindow.dismiss();
            }
            if (PlayVideoActivity.this.m_popupWindow2.isShowing()) {
                PlayVideoActivity.this.m_popupWindow2.dismiss();
            }
            PlayVideoActivity.this.m_showAudioDeleteDialog.show();
        }
    }

    private void initPopupWindow2() {
        View viewInflate = this.m_inflater.inflate(R.layout.popupaudiopreview2, (ViewGroup) null);
        PopupWindow popupWindow = new PopupWindow(viewInflate, -2, -2);
        this.m_popupWindow2 = popupWindow;
        popupWindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.popup2));
        this.m_popupWindow2.setFocusable(true);
        this.m_popupWindow2.setOutsideTouchable(true);
        this.m_popupWindow2.update();
        LinearLayout linearLayout = (LinearLayout) viewInflate.findViewById(R.id.linearl_delete);
        this.m_linearLDelete2 = linearLayout;
        linearLayout.setOnClickListener(new BtnDeleteOnClickListener());
        LinearLayout linearLayout2 = (LinearLayout) viewInflate.findViewById(R.id.linearl_send);
        this.m_linearLSend = linearLayout2;
        linearLayout2.setOnClickListener(new LinearSendClickListener());
    }

    private class LinearSendClickListener implements View.OnClickListener {
        private LinearSendClickListener() {
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            try {
                PlayVideoActivity.this.m_myApp.waitSendfiles.clear();
                PlayVideoActivity.this.m_myApp.waitSendfiles.add(IpmsgApplication.g_playingVideoList.get(PlayVideoActivity.s_nCurrentIndex).getPath());
                IProtocol iProtocol = PlayVideoActivity.this.m_myApp.ipmsgService.m_DataSource.m_Protocol;
                Vector<String> vector = PlayVideoActivity.this.m_myApp.waitSendfiles;
                IpmsgApplication unused = PlayVideoActivity.this.m_myApp;
                iProtocol.sendFile(null, vector, IpmsgApplication.g_currentHostInfo, PlayVideoActivity.this.m_myApp.ipmsgService, null, 0);
                Toast.makeText(PlayVideoActivity.this, R.string.notifysendout, 0).show();
                PlayVideoActivity.this.m_popupWindow2.dismiss();
                PlayVideoActivity.this.m_myApp.waitSendfiles.clear();
            } catch (NullPointerException e) {
                e.printStackTrace();
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
    }

    public void openCustomMenu() {
        if (this.m_mediaStateCurrent == MediaOperateState.STATE_PLAYING) {
            doPauseResume(true);
        }
        View viewFindViewById = findViewById(R.id.top_llayout_activity_video);
        if (Global.g_isInFileActivity) {
            this.m_popupWindow2.showAtLocation(viewFindViewById, 53, this.m_nPopX, Global.g_nStatusHeight + viewFindViewById.getHeight());
        } else {
            this.m_popupWindow.showAtLocation(viewFindViewById, 53, this.m_nPopX, Global.g_nStatusHeight + viewFindViewById.getHeight());
        }
    }

    private class OnSeekBarChangeListenerImpl implements SeekBar.OnSeekBarChangeListener {
        @Override // android.widget.SeekBar.OnSeekBarChangeListener
        public void onProgressChanged(SeekBar seekBar, int i, boolean z) {
        }

        private OnSeekBarChangeListenerImpl() {
        }

        @Override // android.widget.SeekBar.OnSeekBarChangeListener
        public void onStopTrackingTouch(SeekBar seekBar) {
            PlayVideoActivity.this.m_mediaPlayer.seekTo((seekBar.getProgress() * PlayVideoActivity.this.m_mediaPlayer.getDuration()) / 1000);
        }

        @Override // android.widget.SeekBar.OnSeekBarChangeListener
        public void onStartTrackingTouch(SeekBar seekBar) {
            try {
                if (PlayVideoActivity.this.m_mediaPlayer.isPlaying()) {
                    PlayVideoActivity.this.doPauseResume(true);
                }
            } catch (IllegalStateException e) {
                e.printStackTrace();
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean quickBackOrForward(int i, boolean z, int i2) {
        if (i != 0) {
            if (i != 1) {
                return z;
            }
            this.m_handler.removeMessages(i2);
            this.m_mediaPlayer.seekTo((this.m_seekBar.getProgress() * this.m_mediaPlayer.getDuration()) / 1000);
            return false;
        }
        if (z) {
            return z;
        }
        if (this.m_mediaPlayer.isPlaying()) {
            doPauseResume(false);
        }
        this.m_handler.sendEmptyMessage(i2);
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void modifySeekBar(int i) {
        int progress = this.m_seekBar.getProgress() + i;
        if (progress < 0) {
            progress = 0;
        } else if (progress > 1000) {
            progress = 1000;
        }
        this.m_seekBar.setProgress(progress);
    }

    public boolean deleteVideo() {
        String path = IpmsgApplication.g_playingVideoList.get(s_nCurrentIndex).getPath();
        try {
            boolean zDeleteFile = FileManager.getIFileManager(this).deleteFile(new File(path));
            if (zDeleteFile) {
                updateList(path);
                if (IpmsgApplication.g_playingVideoList.size() > 0) {
                    nextVideo();
                } else {
                    this.m_lastAudioDialog.show();
                }
                return true;
            }
            Public_Tools.showToast(this, getString(R.string.not_delete_notify), 1);
            return zDeleteFile;
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
            return false;
        } catch (Exception e2) {
            e2.printStackTrace();
            return false;
        }
    }

    private void updateList(String str) {
        FileAdapter fileAdapter;
        if (Global.g_isInFileActivity) {
            fileAdapter = FileActivity.s_choiceListView.getFileAdapter();
        } else {
            fileAdapter = IpmsgActivity.s_fileListView.getFileAdapter();
        }
        int positionOnPath = Public_Tools.getPositionOnPath(fileAdapter.getFileList(), str);
        if (positionOnPath != -1) {
            fileAdapter.getFileList().remove(positionOnPath);
            fileAdapter.notifyDataSetChanged();
        }
        IpmsgApplication.g_playingVideoList.remove(s_nCurrentIndex);
        int musicPositionOnPath = Public_Tools.getMusicPositionOnPath(IpmsgApplication.g_arrVideoList, str);
        if (musicPositionOnPath != -1) {
            IpmsgApplication.g_arrVideoList.remove(musicPositionOnPath);
        }
    }

    public void show(int i) {
        this.m_showing = true;
        this.m_linearLTop.setVisibility(0);
        this.m_linearLBottom.setVisibility(0);
        Message messageObtainMessage = this.m_handler.obtainMessage(1);
        this.m_handler.removeMessages(1);
        if (i != 0) {
            this.m_handler.sendMessageDelayed(messageObtainMessage, i);
        }
    }

    public void hide() {
        try {
            this.m_showing = false;
            this.m_linearLTop.setVisibility(8);
            this.m_linearLBottom.setVisibility(8);
            if (this.m_popupWindow.isShowing()) {
                this.m_popupWindow.dismiss();
            }
            if (this.m_popupWindow2.isShowing()) {
                this.m_popupWindow2.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override // android.app.Activity
    protected void onSaveInstanceState(Bundle bundle) {
        bundle.putInt("currentIndex", s_nCurrentIndex);
        bundle.putInt("throughTime", this.m_mediaPlayer.getCurrentPosition());
        super.onSaveInstanceState(bundle);
    }
}

