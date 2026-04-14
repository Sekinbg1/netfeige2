package com.netfeige.display.ui;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.audiofx.Visualizer;
import android.os.Bundle;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import com.geniusgithub.mediarender.music.VisualizerView;
import com.netfeige.R;
import com.netfeige.common.Global;
import com.netfeige.common.HostInformation;
import com.netfeige.common.Music;
import com.netfeige.common.Public_Tools;
import com.netfeige.common.VisualizerManager;
import com.netfeige.display.data.IpmsgApplication;
import com.netfeige.display.ui.dialog.LastAudioDialog;
import com.netfeige.display.ui.dialog.ShowAudioDeleteDialog;
import com.netfeige.dlna.ContentTree;
import com.netfeige.filemanager.FileManager;
import com.netfeige.kits.DataConfig;
import com.netfeige.service.MusicService;
import java.io.File;

/* JADX INFO: loaded from: classes.dex */
public class PlayMusicActivity extends Activity implements Visualizer.OnDataCaptureListener {
    public static Boolean s_bIsLoop = true;
    public static int s_nCurrentIndex = 0;
    private Button m_btnBack;
    private MyCompletionListner m_completionListner;
    private ImageButton m_imageBtnBackgroundPlay;
    private ImageButton m_imageBtnForward;
    private ImageButton m_imageBtnNext;
    private ImageButton m_imageBtnPlay;
    private IpmsgApplication m_ipmsgApp;
    private LastAudioDialog m_lastAudioDialog;
    private LinearLayout m_linearLBackgroundPlay;
    private LinearLayout m_linearLDelete;
    private LinearLayout m_linearLDelete2;
    private LinearLayout m_linearLPopupSend;
    private LinearLayout m_linearLSend;
    private LinearLayout m_linearLShare;
    private LinearLayout m_linearLShowTo;
    private int m_nPopX;
    private PopupWindow m_popupWindow;
    private PopupWindow m_popupWindow2;
    private MyProgressBroadCastReceiver m_receiver;
    private GestureDetector m_relativeLGestureDetector;
    private SeekBar m_seekBProgress;
    private ShowAudioDeleteDialog m_showAudioDeleteDialog;
    private StartPlayBroadcastReceiver m_startPlayReceiver;
    private String m_strFilePath;
    private TextView m_textVEndTime;
    private TextView m_textVFileName;
    private TextView m_textVStartTime;
    private Visualizer m_visualizer;
    private VisualizerManager m_visualizerManager;
    private VisualizerView m_visualizerView;
    private RelativeLayout m_relativeLForGesture = null;
    private Boolean m_bIsPlaying = false;
    private String m_strPath = null;
    private boolean m_bIsExecuted = false;
    private boolean m_bIsSameMusic = false;
    private int m_nStartNumber = 0;
    private AudioManager m_audioManager = null;
    private Button m_btnMore = null;
    private LayoutInflater m_inflater = null;
    private final int m_nPopUpX = 5;

    @Override // android.app.Activity
    protected void onCreate(Bundle bundle) {
        setContentView(R.layout.playmusic);
        this.m_ipmsgApp = (IpmsgApplication) getApplication();
        IpmsgApplication.g_bIsDestroy = false;
        Bundle extras = getIntent().getExtras();
        String string = extras.getString("filePath");
        this.m_strPath = string;
        this.m_strFilePath = string;
        s_nCurrentIndex = seekPosition(string);
        try {
            IpmsgApplication.g_arrPlayingList.get(s_nCurrentIndex).setTime(extras.getLong("time"));
            this.m_bIsSameMusic = this.m_strPath.equals(MusicService.s_nCurrentPlayingPath);
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
        } catch (Exception e2) {
            e2.printStackTrace();
        }
        ((TelephonyManager) getSystemService("phone")).listen(new MobliePhoneStateListener(), 32);
        this.m_completionListner = new MyCompletionListner();
        registerReceiver(this.m_completionListner, new IntentFilter("cn.com.completion"));
        this.m_startPlayReceiver = new StartPlayBroadcastReceiver();
        registerReceiver(this.m_startPlayReceiver, new IntentFilter("cn.com.played"));
        this.m_audioManager = (AudioManager) getSystemService("audio");
        this.m_inflater = (LayoutInflater) getSystemService("layout_inflater");
        initControl();
        if (Global.g_isInFileActivity) {
            this.m_imageBtnBackgroundPlay.setVisibility(4);
        }
        this.m_nPopX = Public_Tools.dip2px(this, 5.0f);
        initPopupWindow();
        initPopupWindow2();
        this.m_showAudioDeleteDialog = new ShowAudioDeleteDialog(this);
        this.m_lastAudioDialog = new LastAudioDialog(this);
        super.onCreate(bundle);
    }

    private void initControl() {
        Button button = (Button) findViewById(R.id.back_btn);
        this.m_btnBack = button;
        button.setOnClickListener(new BtnBackOnClickListener());
        TextView textView = (TextView) findViewById(R.id.palyorpause_text);
        this.m_textVFileName = textView;
        textView.setText(getFileName(this.m_strFilePath));
        TextView textView2 = (TextView) findViewById(R.id.playtime);
        this.m_textVStartTime = textView2;
        textView2.setText("00:00");
        this.m_textVEndTime = (TextView) findViewById(R.id.duration);
        this.m_seekBProgress = (SeekBar) findViewById(R.id.seekBar1);
        ImageButton imageButton = (ImageButton) findViewById(R.id.playBtn);
        this.m_imageBtnPlay = imageButton;
        imageButton.requestFocus();
        this.m_imageBtnForward = (ImageButton) findViewById(R.id.forwardBtn);
        this.m_imageBtnNext = (ImageButton) findViewById(R.id.nextBtn);
        ImageButton imageButton2 = (ImageButton) findViewById(R.id.backgroundplay);
        this.m_imageBtnBackgroundPlay = imageButton2;
        imageButton2.setOnClickListener(new ImageBtnBackgroundPlayOnClickListener());
        this.m_imageBtnPlay.setOnClickListener(new MusicOnClickListener());
        this.m_imageBtnForward.setOnClickListener(new MusicOnClickListener());
        this.m_imageBtnNext.setOnClickListener(new MusicOnClickListener());
        this.m_relativeLGestureDetector = new GestureDetector(this, new RelativeLGestureDetector());
        RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.relativeL_center);
        this.m_relativeLForGesture = relativeLayout;
        relativeLayout.setLongClickable(true);
        this.m_relativeLForGesture.setOnTouchListener(new RelativeLForGestureOnTouchListener());
        this.m_seekBProgress.setOnSeekBarChangeListener(new SeekBProgressOnSeekBarChangeListener());
        Button button2 = (Button) findViewById(R.id.more_btn);
        this.m_btnMore = button2;
        button2.setOnClickListener(new BtnMoreOnClickListener());
        this.m_visualizerView = (VisualizerView) findViewById(R.id.mp_freq_view2);
    }

    private class BtnMoreOnClickListener implements View.OnClickListener {
        private BtnMoreOnClickListener() {
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            View viewFindViewById = PlayMusicActivity.this.findViewById(R.id.linearl_top);
            if (Global.g_isInFileActivity) {
                PlayMusicActivity.this.m_popupWindow2.showAtLocation(viewFindViewById, 53, PlayMusicActivity.this.m_nPopX, Global.g_nStatusHeight + viewFindViewById.getHeight());
            } else {
                PlayMusicActivity.this.m_popupWindow.showAtLocation(viewFindViewById, 53, PlayMusicActivity.this.m_nPopX, Global.g_nStatusHeight + viewFindViewById.getHeight());
            }
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
                PlayMusicActivity.this.m_ipmsgApp.waitSendfiles.clear();
                PlayMusicActivity.this.m_ipmsgApp.waitSendfiles.add(IpmsgApplication.g_arrPlayingList.get(PlayMusicActivity.s_nCurrentIndex).getPath());
                PlayMusicActivity.this.m_ipmsgApp.ipmsgService.m_DataSource.m_Protocol.sendFile(null, PlayMusicActivity.this.m_ipmsgApp.waitSendfiles, PlayMusicActivity.this.getHostInfo(), PlayMusicActivity.this.m_ipmsgApp.ipmsgService, null, 0);
                Toast.makeText(PlayMusicActivity.this, R.string.notifysendout, 0).show();
                PlayMusicActivity.this.m_popupWindow2.dismiss();
                PlayMusicActivity.this.m_ipmsgApp.waitSendfiles.clear();
            } catch (NullPointerException e) {
                e.printStackTrace();
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
    }

    public HostInformation getHostInfo() {
        for (int i = 0; i < this.m_ipmsgApp.ipmsgService.userList.size(); i++) {
            try {
                if (this.m_ipmsgApp.ipmsgService.userList.get(i).strMacAddr.equals(ChatActivity.strMacAddr)) {
                    return this.m_ipmsgApp.ipmsgService.userList.get(i);
                }
            } catch (NullPointerException e) {
                e.printStackTrace();
                return null;
            } catch (Exception e2) {
                e2.printStackTrace();
                return null;
            }
        }
        return null;
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
        linearLayout2.setOnClickListener(new ImageBtnShowToOnClickListener());
        LinearLayout linearLayout3 = (LinearLayout) viewInflate.findViewById(R.id.linearl_print);
        this.m_linearLBackgroundPlay = linearLayout3;
        linearLayout3.setOnClickListener(new ImageBtnBackgroundPlayOnClickListener());
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
            PlayMusicActivity.this.m_popupWindow.dismiss();
            Intent intent = new Intent(PlayMusicActivity.this, (Class<?>) ChoiceUserActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("filePath", IpmsgApplication.g_arrPlayingList.get(PlayMusicActivity.s_nCurrentIndex).getPath());
            bundle.putString("activity", "PlayMediaActivity");
            intent.putExtras(bundle);
            PlayMusicActivity.this.startActivity(intent);
        }
    }

    private class BtnBackOnClickListener implements View.OnClickListener {
        private BtnBackOnClickListener() {
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            Global.g_bBackgroundPlay = false;
            PlayMusicActivity.this.onBackPressed();
        }
    }

    private class BtnDeleteOnClickListener implements View.OnClickListener {
        private BtnDeleteOnClickListener() {
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            if (PlayMusicActivity.this.m_popupWindow.isShowing()) {
                PlayMusicActivity.this.m_popupWindow.dismiss();
            }
            if (PlayMusicActivity.this.m_popupWindow2.isShowing()) {
                PlayMusicActivity.this.m_popupWindow2.dismiss();
            }
            PlayMusicActivity.this.m_showAudioDeleteDialog.show();
        }
    }

    private class ImageBtnShowToOnClickListener implements View.OnClickListener {
        private ImageBtnShowToOnClickListener() {
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            PlayMusicActivity.this.pauseMedia();
            PlayMusicActivity.this.m_popupWindow.dismiss();
            String path = IpmsgApplication.g_arrPlayingList.get(PlayMusicActivity.s_nCurrentIndex).getPath();
            Intent intent = new Intent(PlayMusicActivity.this, (Class<?>) ChoiceRemotePlayerActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("filePath", path);
            intent.putExtras(bundle);
            PlayMusicActivity.this.startActivity(intent);
        }
    }

    private class ImageBtnBackgroundPlayOnClickListener implements View.OnClickListener {
        private ImageBtnBackgroundPlayOnClickListener() {
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            PlayMusicActivity.this.m_popupWindow.dismiss();
            Global.g_bBackgroundPlay = true;
            PlayMusicActivity.this.onBackPressed();
        }
    }

    private class RelativeLForGestureOnTouchListener implements View.OnTouchListener {
        private RelativeLForGestureOnTouchListener() {
        }

        @Override // android.view.View.OnTouchListener
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (motionEvent.getAction() == 1) {
                PlayMusicActivity.this.m_bIsExecuted = false;
            }
            return PlayMusicActivity.this.m_relativeLGestureDetector.onTouchEvent(motionEvent);
        }
    }

    private class SeekBProgressOnSeekBarChangeListener implements SeekBar.OnSeekBarChangeListener {
        @Override // android.widget.SeekBar.OnSeekBarChangeListener
        public void onProgressChanged(SeekBar seekBar, int i, boolean z) {
        }

        private SeekBProgressOnSeekBarChangeListener() {
        }

        @Override // android.widget.SeekBar.OnSeekBarChangeListener
        public void onStopTrackingTouch(SeekBar seekBar) {
            PlayMusicActivity.this.m_bIsPlaying = true;
            PlayMusicActivity.this.m_imageBtnPlay.setImageResource(R.drawable.play_selecor);
            PlayMusicActivity.this.m_seekBProgress.setProgress(seekBar.getProgress());
            Intent intent = new Intent("cn.com.seekBar");
            intent.putExtra("seekBarPosition", seekBar.getProgress());
            PlayMusicActivity.this.sendBroadcast(intent);
        }

        @Override // android.widget.SeekBar.OnSeekBarChangeListener
        public void onStartTrackingTouch(SeekBar seekBar) {
            try {
                MusicService.s_mediaPlayer.pause();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
    }

    private class MobliePhoneStateListener extends PhoneStateListener {
        private MobliePhoneStateListener() {
        }

        @Override // android.telephony.PhoneStateListener
        public void onCallStateChanged(int i, String str) {
            if (i != 1) {
                return;
            }
            PlayMusicActivity.this.transmitIntent("play", "pause");
            PlayMusicActivity.this.m_bIsPlaying = false;
            PlayMusicActivity.this.m_imageBtnPlay.setImageResource(R.drawable.pause_selecor);
        }
    }

    @Override // android.app.Activity
    protected void onStart() {
        super.onStart();
        this.m_nStartNumber++;
        this.m_receiver = new MyProgressBroadCastReceiver();
        registerReceiver(this.m_receiver, new IntentFilter("cn.com.progress"));
        if (this.m_nStartNumber > 1) {
            if (this.m_strPath.equals(IpmsgApplication.g_arrPlayingList.get(MusicService.s_nCurrentListId).getPath())) {
                if (this.m_bIsPlaying.booleanValue()) {
                    playOrReplayMusic("replaying");
                    return;
                }
                return;
            }
            playOrReplayMusic("play");
            return;
        }
        if (this.m_bIsSameMusic) {
            playOrReplayMusic("replaying");
        } else {
            playOrReplayMusic("play");
        }
    }

    private void playOrReplayMusic(String str) {
        try {
            this.m_textVEndTime.setText(Public_Tools.toTime((int) IpmsgApplication.g_arrPlayingList.get(s_nCurrentIndex).getTime()));
            transmitIntent("play", str, "id", s_nCurrentIndex);
            if (MusicService.s_bIsPause) {
                this.m_imageBtnPlay.setImageResource(R.drawable.pause_selecor);
                this.m_bIsPlaying = false;
            } else {
                this.m_imageBtnPlay.setImageResource(R.drawable.play_selecor);
                this.m_bIsPlaying = true;
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
        } catch (Exception e2) {
            e2.printStackTrace();
        }
    }

    @Override // android.app.Activity
    protected void onResume() {
        this.m_ipmsgApp.currentActivity = this;
        if (this.m_ipmsgApp.g_bBackRuning) {
            this.m_ipmsgApp.g_bBackRuning = false;
            ((NotificationManager) getSystemService("notification")).cancel(IpmsgApplication.MAIN_NOTIFICATION_ID);
            if (this.m_ipmsgApp.g_strMsgNotification.equals(ContentTree.VIDEO_ID)) {
                ((NotificationManager) getSystemService("notification")).cancel(IpmsgApplication.MSG_NOTIFICATION_ID);
            }
        }
        super.onResume();
// Umeng removed:         // Umeng removed: MobclickAgent.onResume(this);
    }

    @Override // android.app.Activity
    protected void onPause() {
        super.onPause();
// Umeng removed:         // Umeng removed: MobclickAgent.onPause(this);
    }

    @Override // android.app.Activity
    protected void onStop() {
        try {
            this.m_strPath = IpmsgApplication.g_arrPlayingList.get(MusicService.s_nCurrentListId).getPath();
        } catch (IndexOutOfBoundsException unused) {
            this.m_strPath = "";
        } catch (Exception unused2) {
            this.m_strPath = "";
        }
        if (this.m_ipmsgApp.currentActivity == this) {
            this.m_ipmsgApp.g_bBackRuning = true;
            Public_Tools.showNotification(this.m_ipmsgApp, getString(R.string.app_name), DataConfig.getInstance(this).Read(0), R.drawable.ic_launcher);
        }
        super.onStop();
    }

    @Override // android.app.Activity
    protected void onDestroy() {
        IpmsgApplication.g_bIsDestroy = true;
        unregisterReceiver(this.m_receiver);
        unregisterReceiver(this.m_completionListner);
        unregisterReceiver(this.m_startPlayReceiver);
        VisualizerManager visualizerManager = this.m_visualizerManager;
        if (visualizerManager != null) {
            visualizerManager.releaseVisualizer();
        }
        super.onDestroy();
    }

    public class MyProgressBroadCastReceiver extends BroadcastReceiver {
        public MyProgressBroadCastReceiver() {
        }

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            try {
                int intExtra = intent.getIntExtra("position", 0);
                int intExtra2 = (intExtra * 100) / intent.getIntExtra("total", 0);
                PlayMusicActivity.this.m_textVStartTime.setText(Public_Tools.toTime(intExtra));
                PlayMusicActivity.this.m_seekBProgress.setProgress(intExtra2);
                PlayMusicActivity.this.m_seekBProgress.invalidate();
            } catch (ArithmeticException e) {
                e.printStackTrace();
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
    }

    private class MusicOnClickListener implements View.OnClickListener {
        private MusicOnClickListener() {
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            if (view == PlayMusicActivity.this.m_imageBtnForward) {
                PlayMusicActivity.this.forwardMusic();
                if (PlayMusicActivity.this.m_visualizerManager != null) {
                    PlayMusicActivity.this.m_visualizerManager.enableVisualizer(true);
                    return;
                }
                return;
            }
            if (view == PlayMusicActivity.this.m_imageBtnPlay) {
                if (PlayMusicActivity.this.m_bIsPlaying.booleanValue()) {
                    PlayMusicActivity.this.pauseMedia();
                    if (PlayMusicActivity.this.m_visualizerManager != null) {
                        PlayMusicActivity.this.m_visualizerManager.enableVisualizer(false);
                        return;
                    }
                    return;
                }
                PlayMusicActivity.this.transmitIntent("play", "playing", "id", PlayMusicActivity.s_nCurrentIndex);
                PlayMusicActivity.this.m_bIsPlaying = true;
                PlayMusicActivity.this.m_imageBtnPlay.setImageResource(R.drawable.play_selecor);
                if (PlayMusicActivity.this.m_visualizerManager != null) {
                    PlayMusicActivity.this.m_visualizerManager.enableVisualizer(true);
                    return;
                }
                return;
            }
            if (view == PlayMusicActivity.this.m_imageBtnNext) {
                PlayMusicActivity.this.nextMusic();
                if (PlayMusicActivity.this.m_visualizerManager != null) {
                    PlayMusicActivity.this.m_visualizerManager.enableVisualizer(true);
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void pauseMedia() {
        transmitIntent("play", "pause", "id", s_nCurrentIndex);
        this.m_bIsPlaying = false;
        this.m_imageBtnPlay.setBackgroundDrawable(null);
        this.m_imageBtnPlay.setImageResource(R.drawable.pause_selecor);
    }

    private class MyCompletionListner extends BroadcastReceiver {
        private MyCompletionListner() {
        }

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            PlayMusicActivity.this.nextMusic();
        }
    }

    @Override // android.app.Activity
    public void onBackPressed() {
        if (!Global.g_bBackgroundPlay) {
            MusicService.s_bIsPause = false;
            try {
                if (MusicService.s_mediaPlayer != null) {
                    MusicService.s_mediaPlayer.stop();
                    MusicService.s_mediaPlayer.release();
                    MusicService.s_mediaPlayer = null;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        Global.g_bOpenAudioInChatActivity = false;
        super.onBackPressed();
    }

    @Override // android.app.Activity, android.view.KeyEvent.Callback
    public boolean onKeyDown(int i, KeyEvent keyEvent) {
        if (keyEvent.getKeyCode() == 4) {
            if (MusicService.s_mediaPlayer != null) {
                MusicService.s_mediaPlayer.stop();
                MusicService.s_mediaPlayer.release();
                MusicService.s_mediaPlayer = null;
            }
            Global.g_bBackgroundPlay = false;
        }
        MusicService.s_bIsPause = false;
        Global.g_bOpenAudioInChatActivity = false;
        return super.onKeyDown(i, keyEvent);
    }

    public void stopMusicService2() {
        stopService(new Intent(this, (Class<?>) MusicService.class));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void nextMusic() {
        try {
            if (s_nCurrentIndex == IpmsgApplication.g_arrPlayingList.size() - 1) {
                s_nCurrentIndex = 0;
            } else {
                s_nCurrentIndex++;
            }
            String path = IpmsgApplication.g_arrMusicList.get(s_nCurrentIndex).getPath();
            this.m_strFilePath = path;
            this.m_textVFileName.setText(getFileName(path));
            Music music = IpmsgApplication.g_arrPlayingList.get(s_nCurrentIndex);
            if (music.getTime() == -1) {
                long duration = Public_Tools.getDuration(music.getPath());
                IpmsgApplication.g_arrPlayingList.get(s_nCurrentIndex).setTime(duration);
                nextMusicDealTime(music, duration);
                return;
            }
            nextMusicDealTime(music, music.getTime());
        } catch (ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
        } catch (Exception e2) {
            e2.printStackTrace();
        }
    }

    private void nextMusicDealTime(Music music, long j) {
        if (j != 0) {
            this.m_textVEndTime.setText(Public_Tools.toTime((int) j));
            this.m_imageBtnPlay.setImageResource(R.drawable.play_selecor);
            transmitIntent("play", "next", "id", s_nCurrentIndex);
            this.m_bIsPlaying = true;
            return;
        }
        nextMusic();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void forwardMusic() {
        try {
            if (s_nCurrentIndex == 0) {
                s_nCurrentIndex = IpmsgApplication.g_arrPlayingList.size() - 1;
            } else {
                s_nCurrentIndex--;
            }
            String path = IpmsgApplication.g_arrMusicList.get(s_nCurrentIndex).getPath();
            this.m_strFilePath = path;
            this.m_textVFileName.setText(getFileName(path));
            Music music = IpmsgApplication.g_arrPlayingList.get(s_nCurrentIndex);
            if (music.getTime() == -1) {
                long duration = Public_Tools.getDuration(music.getPath());
                IpmsgApplication.g_arrPlayingList.get(s_nCurrentIndex).setTime(duration);
                forwardMusicDealTime(music, duration);
                return;
            }
            forwardMusicDealTime(music, music.getTime());
        } catch (ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
        } catch (Exception e2) {
            e2.printStackTrace();
        }
    }

    private void forwardMusicDealTime(Music music, long j) {
        if (j != 0) {
            this.m_textVEndTime.setText(Public_Tools.toTime((int) j));
            this.m_imageBtnPlay.setImageResource(R.drawable.play_selecor);
            transmitIntent("play", "forward", "id", s_nCurrentIndex);
            this.m_bIsPlaying = true;
            return;
        }
        forwardMusic();
    }

    private boolean playCurrentMusic() {
        Music music = IpmsgApplication.g_arrPlayingList.get(s_nCurrentIndex);
        if (music.getTime() == -1) {
            return playNoDealTime(music);
        }
        return playDealTime(music);
    }

    private boolean playDealTime(Music music) {
        if (music.getTime() == 0) {
            return false;
        }
        this.m_textVEndTime.setText(Public_Tools.toTime((int) music.getTime()));
        this.m_imageBtnPlay.setImageResource(R.drawable.play_selecor);
        transmitIntent("play", "play", "id", s_nCurrentIndex);
        this.m_bIsPlaying = true;
        return true;
    }

    private boolean playNoDealTime(Music music) {
        long duration = Public_Tools.getDuration(music.getPath());
        IpmsgApplication.g_arrPlayingList.get(s_nCurrentIndex).setTime(duration);
        if (duration != 0) {
            this.m_textVEndTime.setText(Public_Tools.toTime((int) duration));
            this.m_imageBtnPlay.setImageResource(R.drawable.play_selecor);
            transmitIntent("play", "play", "id", s_nCurrentIndex);
            this.m_bIsPlaying = true;
            return true;
        }
        int size = IpmsgApplication.g_arrPlayingList.size();
        if (s_nCurrentIndex + 1 > size - 1) {
            if (!loopPlay(size)) {
                if (MusicService.s_mediaPlayer != null) {
                    MusicService.s_mediaPlayer.stop();
                    MusicService.s_mediaPlayer.release();
                    MusicService.s_mediaPlayer = null;
                }
                onBackPressed();
            }
        } else if (!loopPlay2(size) && !loopPlay(size)) {
            if (MusicService.s_mediaPlayer != null) {
                MusicService.s_mediaPlayer.stop();
                MusicService.s_mediaPlayer.release();
                MusicService.s_mediaPlayer = null;
            }
            onBackPressed();
        }
        return false;
    }

    private boolean playCurrentMusic2() {
        Music music = IpmsgApplication.g_arrPlayingList.get(s_nCurrentIndex);
        if (music.getTime() == -1) {
            long duration = Public_Tools.getDuration(music.getPath());
            IpmsgApplication.g_arrPlayingList.get(s_nCurrentIndex).setTime(duration);
            if (duration == 0) {
                return false;
            }
            this.m_textVEndTime.setText(Public_Tools.toTime((int) duration));
            this.m_imageBtnPlay.setImageResource(R.drawable.play_selecor);
            transmitIntent("play", "play", "id", s_nCurrentIndex);
            this.m_bIsPlaying = true;
            return true;
        }
        return playDealTime(music);
    }

    private boolean loopPlay2(int i) {
        int i2 = s_nCurrentIndex;
        while (true) {
            s_nCurrentIndex = i2 + 1;
            if (s_nCurrentIndex >= i) {
                return false;
            }
            if (playCurrentMusic2()) {
                return true;
            }
            i2 = s_nCurrentIndex;
        }
    }

    private class RelativeLGestureDetector extends GestureDetector.SimpleOnGestureListener {
        private RelativeLGestureDetector() {
        }

        @Override // android.view.GestureDetector.SimpleOnGestureListener, android.view.GestureDetector.OnGestureListener
        public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent2, float f, float f2) {
            if (Math.atan2(Math.abs(motionEvent2.getY() - motionEvent.getY()), Math.abs(motionEvent2.getX() - motionEvent.getX())) < 0.5d) {
                if (f <= 0.0f || PlayMusicActivity.this.m_bIsExecuted) {
                    if (f >= 0.0f || PlayMusicActivity.this.m_bIsExecuted) {
                        return false;
                    }
                    PlayMusicActivity.this.forwardMusic();
                    PlayMusicActivity.this.m_bIsExecuted = true;
                    return false;
                }
                PlayMusicActivity.this.nextMusic();
                PlayMusicActivity.this.m_bIsExecuted = true;
                return false;
            }
            if (f2 <= 0.0f || PlayMusicActivity.this.m_bIsExecuted) {
                if (f2 >= 0.0f || PlayMusicActivity.this.m_bIsExecuted) {
                    return false;
                }
                PlayMusicActivity.this.m_audioManager.adjustStreamVolume(3, -1, 1);
                PlayMusicActivity.this.m_bIsExecuted = true;
                return false;
            }
            PlayMusicActivity.this.m_audioManager.adjustStreamVolume(3, 1, 1);
            PlayMusicActivity.this.m_bIsExecuted = true;
            return false;
        }
    }

    public boolean deleteAudio(String str) {
        try {
            boolean zDeleteFile = FileManager.getIFileManager(this).deleteFile(new File(str));
            if (zDeleteFile) {
                updateList();
                int size = IpmsgApplication.g_arrPlayingList.size();
                if (size > 0) {
                    sizeD0(size);
                } else {
                    sizeX0();
                }
                return true;
            }
            toast(R.string.not_delete_notify);
            return zDeleteFile;
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
            return false;
        } catch (Exception e2) {
            e2.printStackTrace();
            return false;
        }
    }

    private void sizeX0() {
        if (MusicService.s_mediaPlayer != null) {
            MusicService.s_mediaPlayer.stop();
            MusicService.s_mediaPlayer.reset();
            MusicService.s_mediaPlayer.release();
        }
        this.m_textVStartTime.setText("");
        this.m_textVEndTime.setText("");
        this.m_lastAudioDialog.show();
    }

    private void sizeD0(int i) {
        if (s_nCurrentIndex == i) {
            if (loopPlay(i)) {
                return;
            }
            if (MusicService.s_mediaPlayer != null) {
                MusicService.s_mediaPlayer.stop();
                MusicService.s_mediaPlayer.release();
                MusicService.s_mediaPlayer = null;
            }
            onBackPressed();
            return;
        }
        playCurrentMusic();
    }

    private void updateList() {
        int positionOnPath;
        String path = IpmsgApplication.g_arrPlayingList.get(s_nCurrentIndex).getPath();
        if (Global.g_isInFileActivity) {
            positionOnPath = Public_Tools.getPositionOnPath(FileActivity.s_choiceListView.getFileAdapter().getFileList(), path);
        } else {
            positionOnPath = Public_Tools.getPositionOnPath(IpmsgActivity.s_fileListView.getFileAdapter().getFileList(), path);
        }
        if (positionOnPath != -1) {
            if (Global.g_isInFileActivity) {
                FileActivity.s_choiceListView.getFileAdapter().getFileList().remove(positionOnPath);
                FileActivity.s_choiceListView.getFileAdapter().notifyDataSetChanged();
            } else {
                IpmsgActivity.s_fileListView.getFileAdapter().getFileList().remove(positionOnPath);
                IpmsgActivity.s_fileListView.getFileAdapter().notifyDataSetChanged();
            }
        }
        IpmsgApplication.g_arrPlayingList.remove(s_nCurrentIndex);
        int musicPositionOnPath = Public_Tools.getMusicPositionOnPath(IpmsgApplication.g_arrMusicList, path);
        if (musicPositionOnPath != -1) {
            IpmsgApplication.g_arrMusicList.remove(musicPositionOnPath);
        }
    }

    private boolean loopPlay(int i) {
        s_nCurrentIndex = 0;
        while (s_nCurrentIndex < i) {
            if (playCurrentMusic2()) {
                return true;
            }
            s_nCurrentIndex++;
        }
        return false;
    }

    private void toast(int i) {
        Toast.makeText(this, i, 0).show();
    }

    private int seekPosition(String str) {
        for (int i = 0; i < IpmsgApplication.g_arrPlayingList.size(); i++) {
            if (str.equals(IpmsgApplication.g_arrPlayingList.get(i).getPath())) {
                return i;
            }
        }
        return -1;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void transmitIntent(String str, String str2, String str3, int i) {
        Intent intent = new Intent(this, (Class<?>) MusicService.class);
        intent.putExtra(str, str2);
        intent.putExtra(str3, i);
        startService(intent);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void transmitIntent(String str, String str2) {
        Intent intent = new Intent(this, (Class<?>) MusicService.class);
        intent.putExtra(str, str2);
        startService(intent);
    }

    private class PopUpWindowShareOnClickListener implements View.OnClickListener {
        private PopUpWindowShareOnClickListener() {
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            Public_Tools.share(PlayMusicActivity.this, IpmsgApplication.g_arrPlayingList.get(PlayMusicActivity.s_nCurrentIndex).getPath());
            PlayMusicActivity.this.m_popupWindow.dismiss();
        }
    }

    @Override // android.media.audiofx.Visualizer.OnDataCaptureListener
    public void onWaveFormDataCapture(Visualizer visualizer, byte[] bArr, int i) {
        this.m_visualizerView.updateVisualizer(bArr);
    }

    @Override // android.media.audiofx.Visualizer.OnDataCaptureListener
    public void onFftDataCapture(Visualizer visualizer, byte[] bArr, int i) {
        this.m_visualizerView.updateVisualizer(bArr);
    }

    private class StartPlayBroadcastReceiver extends BroadcastReceiver {
        private StartPlayBroadcastReceiver() {
        }

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            if (PlayMusicActivity.this.m_visualizerManager != null) {
                PlayMusicActivity.this.m_visualizerManager.reInitVisualizer(MusicService.s_mediaPlayer.getAudioSessionId());
                PlayMusicActivity.this.m_visualizerManager.enableVisualizer(true);
            } else {
                PlayMusicActivity.this.m_visualizerManager = new VisualizerManager(PlayMusicActivity.this.m_visualizer, PlayMusicActivity.this);
                PlayMusicActivity.this.m_visualizerManager.reInitVisualizer(MusicService.s_mediaPlayer.getAudioSessionId());
                PlayMusicActivity.this.m_visualizerManager.enableVisualizer(true);
            }
        }
    }

    private String getFileName(String str) {
        return new File(str).getName();
    }
}

