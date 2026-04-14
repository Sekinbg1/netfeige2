package com.netfeige.display.ui;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import com.geniusgithub.mediarender.music.VisualizerView;
import com.netfeige.R;
import com.netfeige.common.Global;
import com.netfeige.common.Public_MsgID;
import com.netfeige.display.data.IpmsgApplication;
import com.netfeige.dlna.DlnaHelper;
import java.io.File;
import org.teleal.cling.model.action.ActionInvocation;
import org.teleal.cling.model.message.UpnpResponse;
import org.teleal.cling.model.meta.Service;
import org.teleal.cling.support.avtransport.callback.GetPositionInfo;
import org.teleal.cling.support.avtransport.callback.Pause;
import org.teleal.cling.support.avtransport.callback.Play;
import org.teleal.cling.support.avtransport.callback.Seek;
import org.teleal.cling.support.avtransport.callback.SetAVTransportURI;
import org.teleal.cling.support.model.PositionInfo;

/* JADX INFO: loaded from: classes.dex */
public class AudioControlActivity extends Activity {
    private Thread m_Thread;
    private Button m_btnBack;
    private ImageButton m_imageBtnBackgroundPlay;
    private ImageButton m_imageBtnForward;
    private ImageButton m_imageBtnNext;
    private ImageButton m_imageBtnPlay;
    private IpmsgApplication m_ipmsgApp;
    private int m_nCurrentIndex;
    private GestureDetector m_relativeLGestureDetector;
    private SeekBar m_seekBProgress;
    private String m_strCurrentTime;
    private String m_strFilePath;
    private TextView m_textVEndTime;
    private TextView m_textVFileName;
    private TextView m_textVStartTime;
    private VisualizerView m_visualizerView;
    private RelativeLayout m_relativeLForGesture = null;
    private Button m_btnMore = null;
    private boolean m_bIsExecuted = false;
    private int m_nNum = 0;
    private int m_nNum1 = 0;
    private int m_nNum2 = 0;
    private int m_nNum3 = 0;
    private int m_nNum4 = 0;
    private boolean m_bRunThread = true;
    private boolean m_bUpdate = true;
    private boolean m_bPlaying = true;
    private String m_strMediaDuration = null;
    private Handler recHandler = new Handler() { // from class: com.netfeige.display.ui.AudioControlActivity.6
        @Override // android.os.Handler
        public void handleMessage(Message message) {
            if (message.what == 0) {
                AudioControlActivity.this.m_textVEndTime.setText(AudioControlActivity.this.m_strMediaDuration);
                AudioControlActivity.this.m_textVStartTime.setText(AudioControlActivity.this.m_strCurrentTime);
                if (AudioControlActivity.this.m_strMediaDuration != null && !AudioControlActivity.this.m_strMediaDuration.equals("0:00:00")) {
                    AudioControlActivity audioControlActivity = AudioControlActivity.this;
                    int iTimeToInt = audioControlActivity.timeToInt(audioControlActivity.m_strCurrentTime);
                    AudioControlActivity audioControlActivity2 = AudioControlActivity.this;
                    int iTimeToInt2 = audioControlActivity2.timeToInt(audioControlActivity2.m_strMediaDuration);
                    if (iTimeToInt != -1 && iTimeToInt2 != -1 && iTimeToInt2 != 0) {
                        int i = (iTimeToInt * 100) / iTimeToInt2;
                        AudioControlActivity.this.m_seekBProgress.setProgress(i);
                        AudioControlActivity.this.m_seekBProgress.invalidate();
                        if (i == 99) {
                            int size = IpmsgApplication.g_arrMusicList.size();
                            if (size <= 0 || AudioControlActivity.this.m_nCurrentIndex != size - 1) {
                                AudioControlActivity.this.nextMusic();
                            } else {
                                AudioControlActivity.this.PauseMedia(ChoiceRemotePlayerActivity.g_serviceAVTransport);
                                AudioControlActivity.this.m_imageBtnPlay.setImageResource(R.drawable.pause_selecor);
                                AudioControlActivity.this.m_bPlaying = false;
                                AudioControlActivity.this.m_bUpdate = false;
                            }
                        }
                    }
                }
            }
            super.handleMessage(message);
        }
    };

    static /* synthetic */ int access$108(AudioControlActivity audioControlActivity) {
        int i = audioControlActivity.m_nNum1;
        audioControlActivity.m_nNum1 = i + 1;
        return i;
    }

    static /* synthetic */ int access$1308(AudioControlActivity audioControlActivity) {
        int i = audioControlActivity.m_nNum3;
        audioControlActivity.m_nNum3 = i + 1;
        return i;
    }

    static /* synthetic */ int access$408(AudioControlActivity audioControlActivity) {
        int i = audioControlActivity.m_nNum;
        audioControlActivity.m_nNum = i + 1;
        return i;
    }

    static /* synthetic */ int access$608(AudioControlActivity audioControlActivity) {
        int i = audioControlActivity.m_nNum2;
        audioControlActivity.m_nNum2 = i + 1;
        return i;
    }

    static /* synthetic */ int access$808(AudioControlActivity audioControlActivity) {
        int i = audioControlActivity.m_nNum4;
        audioControlActivity.m_nNum4 = i + 1;
        return i;
    }

    @Override // android.app.Activity
    protected void onCreate(Bundle bundle) {
        setContentView(R.layout.playmusic);
        this.m_ipmsgApp = (IpmsgApplication) getApplication();
        String string = getIntent().getExtras().getString("filePath");
        this.m_strFilePath = string;
        this.m_nCurrentIndex = seekPosition(string);
        this.m_Thread = new getPositionInfoThread();
        initControl();
        this.m_textVFileName.setText(getFileName(this.m_strFilePath));
        DlnaHelper.stopPlay(ChoiceRemotePlayerActivity.g_serviceAVTransport, this.m_ipmsgApp);
        setPlayUri(ChoiceRemotePlayerActivity.g_serviceAVTransport, DlnaHelper.getHttpUri(this.m_strFilePath));
        super.onCreate(bundle);
    }

    @Override // android.app.Activity
    protected void onResume() {
        super.onResume();
// Umeng removed:         // Umeng removed: MobclickAgent.onResume(this);
    }

    @Override // android.app.Activity
    protected void onPause() {
        super.onPause();
// Umeng removed:         // Umeng removed: MobclickAgent.onPause(this);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setPlayUri(final Service service, final String str) {
        try {
            this.m_ipmsgApp.getControlPoint().execute(new SetAVTransportURI(service, str, "NO METADATA") { // from class: com.netfeige.display.ui.AudioControlActivity.1
                @Override // org.teleal.cling.support.avtransport.callback.SetAVTransportURI, org.teleal.cling.controlpoint.ActionCallback
                public void success(ActionInvocation actionInvocation) {
                    AudioControlActivity.this.m_nNum1 = 0;
                    AudioControlActivity.this.playMedia(service);
                    super.success(actionInvocation);
                }

                @Override // org.teleal.cling.controlpoint.ActionCallback
                public void failure(ActionInvocation actionInvocation, UpnpResponse upnpResponse, String str2) {
                    if (AudioControlActivity.this.m_nNum1 < Global.g_nNumber) {
                        AudioControlActivity.this.setPlayUri(service, str);
                        AudioControlActivity.access$108(AudioControlActivity.this);
                    } else {
                        AudioControlActivity.this.m_nNum1 = 0;
                    }
                }
            });
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (Exception e2) {
            e2.printStackTrace();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void playMedia(final Service service) {
        try {
            this.m_ipmsgApp.getControlPoint().execute(new Play(service) { // from class: com.netfeige.display.ui.AudioControlActivity.2
                @Override // org.teleal.cling.support.avtransport.callback.Play, org.teleal.cling.controlpoint.ActionCallback
                public void success(ActionInvocation actionInvocation) {
                    AudioControlActivity.this.m_nNum = 0;
                    try {
                        if (!AudioControlActivity.this.m_Thread.isAlive()) {
                            AudioControlActivity.this.m_Thread.start();
                        }
                    } catch (IllegalThreadStateException unused) {
                        Log.v("erro", "Thread already started !");
                    }
                    super.success(actionInvocation);
                }

                @Override // org.teleal.cling.controlpoint.ActionCallback
                public void failure(ActionInvocation actionInvocation, UpnpResponse upnpResponse, String str) {
                    if (AudioControlActivity.this.m_nNum < Global.g_nNumber) {
                        AudioControlActivity.this.playMedia(service);
                        AudioControlActivity.access$408(AudioControlActivity.this);
                    } else {
                        AudioControlActivity.this.m_nNum = 0;
                    }
                }
            });
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (Exception e2) {
            e2.printStackTrace();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void PauseMedia(final Service service) {
        if (service == null) {
            return;
        }
        try {
            this.m_ipmsgApp.getControlPoint().execute(new Pause(service) { // from class: com.netfeige.display.ui.AudioControlActivity.3
                @Override // org.teleal.cling.support.avtransport.callback.Pause, org.teleal.cling.controlpoint.ActionCallback
                public void success(ActionInvocation actionInvocation) {
                    AudioControlActivity.this.m_nNum2 = 0;
                    super.success(actionInvocation);
                }

                @Override // org.teleal.cling.controlpoint.ActionCallback
                public void failure(ActionInvocation actionInvocation, UpnpResponse upnpResponse, String str) {
                    if (AudioControlActivity.this.m_nNum2 < Global.g_nNumber) {
                        AudioControlActivity.this.PauseMedia(service);
                        AudioControlActivity.access$608(AudioControlActivity.this);
                    } else {
                        AudioControlActivity.this.m_nNum2 = 0;
                    }
                }
            });
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (Exception e2) {
            e2.printStackTrace();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void SeekMedia(final Service service, final String str) {
        if (service == null) {
            return;
        }
        try {
            this.m_ipmsgApp.getControlPoint().execute(new Seek(service, str) { // from class: com.netfeige.display.ui.AudioControlActivity.4
                @Override // org.teleal.cling.support.avtransport.callback.Seek, org.teleal.cling.controlpoint.ActionCallback
                public void success(ActionInvocation actionInvocation) {
                    AudioControlActivity.this.m_nNum4 = 0;
                    System.out.println("seek test:SeekMedia 成功");
                    super.success(actionInvocation);
                }

                @Override // org.teleal.cling.controlpoint.ActionCallback
                public void failure(ActionInvocation actionInvocation, UpnpResponse upnpResponse, String str2) {
                    System.out.println("seek test:SeekMedia  " + AudioControlActivity.this.m_nNum4 + "失败");
                    if (AudioControlActivity.this.m_nNum4 < Global.g_nNumber) {
                        AudioControlActivity.this.SeekMedia(service, str);
                        AudioControlActivity.access$808(AudioControlActivity.this);
                    } else {
                        System.out.println("seek test:SeekMedia 最终失败了");
                        AudioControlActivity.this.m_nNum4 = 0;
                    }
                }
            });
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (Exception e2) {
            e2.printStackTrace();
        }
    }

    private class getPositionInfoThread extends Thread {
        private getPositionInfoThread() {
        }

        @Override // java.lang.Thread, java.lang.Runnable
        public void run() {
            while (AudioControlActivity.this.m_bRunThread) {
                if (AudioControlActivity.this.m_bUpdate) {
                    AudioControlActivity.this.getPositionInfo(ChoiceRemotePlayerActivity.g_serviceAVTransport);
                }
                try {
                    Thread.sleep(1000L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void getPositionInfo(final Service service) {
        try {
            this.m_ipmsgApp.getControlPoint().execute(new GetPositionInfo(service) { // from class: com.netfeige.display.ui.AudioControlActivity.5
                @Override // org.teleal.cling.controlpoint.ActionCallback
                public void failure(ActionInvocation actionInvocation, UpnpResponse upnpResponse, String str) {
                    if (AudioControlActivity.this.m_nNum3 < Global.g_nNumber) {
                        AudioControlActivity.this.getPositionInfo(service);
                        AudioControlActivity.access$1308(AudioControlActivity.this);
                    } else {
                        AudioControlActivity.this.m_nNum3 = 0;
                    }
                }

                @Override // org.teleal.cling.support.avtransport.callback.GetPositionInfo
                public void received(ActionInvocation actionInvocation, PositionInfo positionInfo) {
                    AudioControlActivity.this.m_nNum3 = 0;
                    if (positionInfo != null) {
                        AudioControlActivity.this.m_strMediaDuration = positionInfo.getTrackDuration();
                        AudioControlActivity.this.m_strCurrentTime = positionInfo.getRelTime();
                        AudioControlActivity.this.recHandler.sendEmptyMessage(0);
                    }
                }
            });
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (Exception e2) {
            e2.printStackTrace();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int timeToInt(String str) {
        if (str == null) {
            return -1;
        }
        try {
            String[] strArrSplit = str.trim().split(Public_MsgID.PRO_SPACE);
            return (Integer.valueOf(strArrSplit[0]).intValue() * 3600) + (Integer.valueOf(strArrSplit[1]).intValue() * 60) + Integer.valueOf(strArrSplit[2]).intValue();
        } catch (Exception unused) {
            return -1;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public String intToTime(int i) {
        int iTimeToInt = timeToInt(this.m_strMediaDuration);
        if (iTimeToInt == -1) {
            return null;
        }
        int i2 = (iTimeToInt * i) / 100;
        int i3 = i2 / 3600;
        int i4 = i2 % 3600;
        return String.format("%02d:%02d:%02d", Integer.valueOf(i3), Integer.valueOf(i4 / 60), Integer.valueOf(i4 % 60));
    }

    private void initControl() {
        Button button = (Button) findViewById(R.id.back_btn);
        this.m_btnBack = button;
        button.setOnClickListener(new BtnBackOnClickListener());
        this.m_textVFileName = (TextView) findViewById(R.id.palyorpause_text);
        VisualizerView visualizerView = (VisualizerView) findViewById(R.id.mp_freq_view2);
        this.m_visualizerView = visualizerView;
        visualizerView.setVisibility(4);
        this.m_textVStartTime = (TextView) findViewById(R.id.playtime);
        this.m_textVEndTime = (TextView) findViewById(R.id.duration);
        this.m_seekBProgress = (SeekBar) findViewById(R.id.seekBar1);
        this.m_imageBtnPlay = (ImageButton) findViewById(R.id.playBtn);
        this.m_imageBtnForward = (ImageButton) findViewById(R.id.forwardBtn);
        this.m_imageBtnNext = (ImageButton) findViewById(R.id.nextBtn);
        ImageButton imageButton = (ImageButton) findViewById(R.id.backgroundplay);
        this.m_imageBtnBackgroundPlay = imageButton;
        imageButton.setVisibility(4);
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
        button2.setVisibility(4);
    }

    private class BtnBackOnClickListener implements View.OnClickListener {
        private BtnBackOnClickListener() {
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            AudioControlActivity.this.m_bRunThread = false;
            AudioControlActivity.this.finish();
            AudioControlActivity.this.onBackPressed();
        }
    }

    private class MusicOnClickListener implements View.OnClickListener {
        private MusicOnClickListener() {
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            if (view == AudioControlActivity.this.m_imageBtnForward) {
                AudioControlActivity.this.forwardMusic();
                return;
            }
            if (view == AudioControlActivity.this.m_imageBtnPlay) {
                if (AudioControlActivity.this.m_bPlaying) {
                    AudioControlActivity.this.PauseMedia(ChoiceRemotePlayerActivity.g_serviceAVTransport);
                    AudioControlActivity.this.m_imageBtnPlay.setBackgroundDrawable(null);
                    AudioControlActivity.this.m_imageBtnPlay.setImageResource(R.drawable.pause_selecor);
                    AudioControlActivity.this.m_bPlaying = false;
                    AudioControlActivity.this.m_bUpdate = false;
                    return;
                }
                AudioControlActivity.this.playMedia(ChoiceRemotePlayerActivity.g_serviceAVTransport);
                AudioControlActivity.this.playSet();
                return;
            }
            if (view == AudioControlActivity.this.m_imageBtnNext) {
                AudioControlActivity.this.nextMusic();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void playSet() {
        this.m_imageBtnPlay.setImageResource(R.drawable.play_selecor);
        this.m_bUpdate = true;
        this.m_bPlaying = true;
    }

    private class RelativeLGestureDetector extends GestureDetector.SimpleOnGestureListener {
        private RelativeLGestureDetector() {
        }

        @Override // android.view.GestureDetector.SimpleOnGestureListener, android.view.GestureDetector.OnGestureListener
        public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent2, float f, float f2) {
            if (Math.atan2(Math.abs(motionEvent2.getY() - motionEvent.getY()), Math.abs(motionEvent2.getX() - motionEvent.getX())) < 0.5d) {
                if (f <= 0.0f || AudioControlActivity.this.m_bIsExecuted) {
                    if (f >= 0.0f || AudioControlActivity.this.m_bIsExecuted) {
                        return false;
                    }
                    AudioControlActivity.this.forwardMusic();
                    AudioControlActivity.this.m_bIsExecuted = true;
                    return false;
                }
                AudioControlActivity.this.nextMusic();
                AudioControlActivity.this.m_bIsExecuted = true;
                return false;
            }
            if (f2 <= 0.0f || AudioControlActivity.this.m_bIsExecuted) {
                if (f2 >= 0.0f || AudioControlActivity.this.m_bIsExecuted) {
                    return false;
                }
                AudioControlActivity.this.m_bIsExecuted = true;
                return false;
            }
            AudioControlActivity.this.m_bIsExecuted = true;
            return false;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void forwardMusic() {
        int i = this.m_nCurrentIndex;
        if (i == 0) {
            Toast.makeText(this, R.string.firstaudio, 0).show();
            return;
        }
        this.m_nCurrentIndex = i - 1;
        String path = IpmsgApplication.g_arrMusicList.get(this.m_nCurrentIndex).getPath();
        this.m_strFilePath = path;
        this.m_textVFileName.setText(getFileName(path));
        DlnaHelper.stopPlay(ChoiceRemotePlayerActivity.g_serviceAVTransport, this.m_ipmsgApp);
        setPlayUri(ChoiceRemotePlayerActivity.g_serviceAVTransport, DlnaHelper.getHttpUri(this.m_strFilePath));
        if (this.m_bPlaying) {
            return;
        }
        playSet();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void nextMusic() {
        int size = IpmsgApplication.g_arrMusicList.size();
        if (size > 0 && this.m_nCurrentIndex == size - 1) {
            Toast.makeText(this, R.string.endaudio, 0).show();
            return;
        }
        this.m_nCurrentIndex++;
        String path = IpmsgApplication.g_arrMusicList.get(this.m_nCurrentIndex).getPath();
        this.m_strFilePath = path;
        this.m_textVFileName.setText(getFileName(path));
        DlnaHelper.stopPlay(ChoiceRemotePlayerActivity.g_serviceAVTransport, this.m_ipmsgApp);
        setPlayUri(ChoiceRemotePlayerActivity.g_serviceAVTransport, DlnaHelper.getHttpUri(this.m_strFilePath));
        if (this.m_bPlaying) {
            return;
        }
        playSet();
    }

    private String getFileName(String str) {
        return new File(str).getName();
    }

    private class RelativeLForGestureOnTouchListener implements View.OnTouchListener {
        private RelativeLForGestureOnTouchListener() {
        }

        @Override // android.view.View.OnTouchListener
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (motionEvent.getAction() == 1) {
                AudioControlActivity.this.m_bIsExecuted = false;
            }
            return AudioControlActivity.this.m_relativeLGestureDetector.onTouchEvent(motionEvent);
        }
    }

    private class SeekBProgressOnSeekBarChangeListener implements SeekBar.OnSeekBarChangeListener {
        @Override // android.widget.SeekBar.OnSeekBarChangeListener
        public void onProgressChanged(SeekBar seekBar, int i, boolean z) {
        }

        @Override // android.widget.SeekBar.OnSeekBarChangeListener
        public void onStartTrackingTouch(SeekBar seekBar) {
        }

        private SeekBProgressOnSeekBarChangeListener() {
        }

        @Override // android.widget.SeekBar.OnSeekBarChangeListener
        public void onStopTrackingTouch(SeekBar seekBar) {
            AudioControlActivity.this.m_seekBProgress.setProgress(seekBar.getProgress());
            String strIntToTime = AudioControlActivity.this.intToTime(seekBar.getProgress());
            if (strIntToTime != null) {
                System.out.println("seek test:开始SeekMedia");
                AudioControlActivity.this.SeekMedia(ChoiceRemotePlayerActivity.g_serviceAVTransport, strIntToTime);
            }
        }
    }

    @Override // android.app.Activity, android.view.KeyEvent.Callback
    public boolean onKeyDown(int i, KeyEvent keyEvent) {
        if (keyEvent.getKeyCode() == 4) {
            this.m_bRunThread = false;
            stopRemotePlay();
            finish();
        }
        return super.onKeyDown(i, keyEvent);
    }

    @Override // android.app.Activity
    protected void onDestroy() {
        this.m_bRunThread = false;
        super.onDestroy();
    }

    @Override // android.app.Activity
    public void onBackPressed() {
        stopRemotePlay();
        super.onBackPressed();
    }

    private void stopRemotePlay() {
        if (ChoiceRemotePlayerActivity.g_serviceAVTransport != null) {
            DlnaHelper.stopPlay(ChoiceRemotePlayerActivity.g_serviceAVTransport, this.m_ipmsgApp);
        }
    }

    private int seekPosition(String str) {
        for (int i = 0; i < IpmsgApplication.g_arrMusicList.size(); i++) {
            if (str.equals(IpmsgApplication.g_arrMusicList.get(i).getPath())) {
                return i;
            }
        }
        return -1;
    }
}

