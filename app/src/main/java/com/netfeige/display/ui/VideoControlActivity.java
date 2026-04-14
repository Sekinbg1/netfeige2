package com.netfeige.display.ui;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
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
public class VideoControlActivity extends Activity {
    private Thread m_Thread;
    private Button m_btnBack;
    private ImageButton m_btnPlayOrPause;
    private IpmsgApplication m_ipmsgApp;
    private SeekBar m_seekBControl;
    private String m_strCurrentTime;
    private String m_strFilePath;
    private TextView m_textVCurrentTime;
    private TextView m_textVCurrentVideoName;
    private TextView m_textVDurationTime;
    private String m_strMediaDuration = null;
    private int m_nNum = 0;
    private int m_nNum1 = 0;
    private int m_nNum2 = 0;
    private int m_nNum3 = 0;
    private int m_nNum4 = 0;
    private boolean m_bRunThread = true;
    private boolean m_bUpdate = true;
    private boolean m_bPlaying = true;
    private Handler recHandler = new Handler() { // from class: com.netfeige.display.ui.VideoControlActivity.6
        @Override // android.os.Handler
        public void handleMessage(Message message) {
            if (message.what == 0) {
                VideoControlActivity.this.m_textVDurationTime.setText(VideoControlActivity.this.m_strMediaDuration);
                VideoControlActivity.this.m_textVCurrentTime.setText(VideoControlActivity.this.m_strCurrentTime);
                if (VideoControlActivity.this.m_strMediaDuration != null && !VideoControlActivity.this.m_strMediaDuration.equals("0:00:00")) {
                    VideoControlActivity videoControlActivity = VideoControlActivity.this;
                    int iTimeToInt = videoControlActivity.timeToInt(videoControlActivity.m_strCurrentTime);
                    VideoControlActivity videoControlActivity2 = VideoControlActivity.this;
                    int iTimeToInt2 = videoControlActivity2.timeToInt(videoControlActivity2.m_strMediaDuration);
                    if (iTimeToInt != -1 && iTimeToInt2 != -1 && iTimeToInt2 != 0) {
                        VideoControlActivity.this.m_seekBControl.setProgress((iTimeToInt * 100) / iTimeToInt2);
                        VideoControlActivity.this.m_seekBControl.invalidate();
                    }
                    VideoControlActivity.this.m_strMediaDuration.equals(VideoControlActivity.this.m_strCurrentTime);
                }
            }
            super.handleMessage(message);
        }
    };

    static /* synthetic */ int access$108(VideoControlActivity videoControlActivity) {
        int i = videoControlActivity.m_nNum1;
        videoControlActivity.m_nNum1 = i + 1;
        return i;
    }

    static /* synthetic */ int access$1208(VideoControlActivity videoControlActivity) {
        int i = videoControlActivity.m_nNum3;
        videoControlActivity.m_nNum3 = i + 1;
        return i;
    }

    static /* synthetic */ int access$408(VideoControlActivity videoControlActivity) {
        int i = videoControlActivity.m_nNum;
        videoControlActivity.m_nNum = i + 1;
        return i;
    }

    static /* synthetic */ int access$508(VideoControlActivity videoControlActivity) {
        int i = videoControlActivity.m_nNum2;
        videoControlActivity.m_nNum2 = i + 1;
        return i;
    }

    static /* synthetic */ int access$708(VideoControlActivity videoControlActivity) {
        int i = videoControlActivity.m_nNum4;
        videoControlActivity.m_nNum4 = i + 1;
        return i;
    }

    @Override // android.app.Activity
    protected void onCreate(Bundle bundle) {
        setContentView(R.layout.videocontrol);
        this.m_ipmsgApp = (IpmsgApplication) getApplication();
        this.m_strFilePath = getIntent().getExtras().getString("filePath");
        getPositionInfoThread getpositioninfothread = new getPositionInfoThread();
        this.m_Thread = getpositioninfothread;
        try {
            if (!getpositioninfothread.isAlive()) {
                this.m_Thread.start();
            }
        } catch (IllegalThreadStateException unused) {
            Log.v("IllegalThreadStateException", "Thread already started!");
        }
        initControl();
        DlnaHelper.stopPlay(ChoiceRemotePlayerActivity.g_serviceAVTransport, this.m_ipmsgApp);
        setPlayUri(ChoiceRemotePlayerActivity.g_serviceAVTransport, DlnaHelper.getHttpUri(this.m_strFilePath));
        super.onCreate(bundle);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setPlayUri(final Service service, final String str) {
        try {
            this.m_ipmsgApp.getControlPoint().execute(new SetAVTransportURI(service, str, "NO METADATA") { // from class: com.netfeige.display.ui.VideoControlActivity.1
                @Override // org.teleal.cling.support.avtransport.callback.SetAVTransportURI, org.teleal.cling.controlpoint.ActionCallback
                public void success(ActionInvocation actionInvocation) {
                    VideoControlActivity.this.m_nNum1 = 0;
                    VideoControlActivity.this.playMedia(service);
                    super.success(actionInvocation);
                }

                @Override // org.teleal.cling.controlpoint.ActionCallback
                public void failure(ActionInvocation actionInvocation, UpnpResponse upnpResponse, String str2) {
                    if (VideoControlActivity.this.m_nNum1 < Global.g_nNumber) {
                        VideoControlActivity.this.setPlayUri(service, str);
                        VideoControlActivity.access$108(VideoControlActivity.this);
                    } else {
                        VideoControlActivity.this.m_nNum1 = 0;
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
            this.m_ipmsgApp.getControlPoint().execute(new Play(service) { // from class: com.netfeige.display.ui.VideoControlActivity.2
                @Override // org.teleal.cling.support.avtransport.callback.Play, org.teleal.cling.controlpoint.ActionCallback
                public void success(ActionInvocation actionInvocation) {
                    VideoControlActivity.this.m_nNum = 0;
                    super.success(actionInvocation);
                }

                @Override // org.teleal.cling.controlpoint.ActionCallback
                public void failure(ActionInvocation actionInvocation, UpnpResponse upnpResponse, String str) {
                    if (VideoControlActivity.this.m_nNum < Global.g_nNumber) {
                        VideoControlActivity.this.playMedia(service);
                        VideoControlActivity.access$408(VideoControlActivity.this);
                    } else {
                        VideoControlActivity.this.m_nNum = 0;
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
            this.m_ipmsgApp.getControlPoint().execute(new Pause(service) { // from class: com.netfeige.display.ui.VideoControlActivity.3
                @Override // org.teleal.cling.support.avtransport.callback.Pause, org.teleal.cling.controlpoint.ActionCallback
                public void success(ActionInvocation actionInvocation) {
                    VideoControlActivity.this.m_nNum2 = 0;
                    super.success(actionInvocation);
                }

                @Override // org.teleal.cling.controlpoint.ActionCallback
                public void failure(ActionInvocation actionInvocation, UpnpResponse upnpResponse, String str) {
                    if (VideoControlActivity.this.m_nNum2 < Global.g_nNumber) {
                        VideoControlActivity.this.PauseMedia(service);
                        VideoControlActivity.access$508(VideoControlActivity.this);
                    } else {
                        VideoControlActivity.this.m_nNum2 = 0;
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
            this.m_ipmsgApp.getControlPoint().execute(new Seek(service, str) { // from class: com.netfeige.display.ui.VideoControlActivity.4
                @Override // org.teleal.cling.support.avtransport.callback.Seek, org.teleal.cling.controlpoint.ActionCallback
                public void success(ActionInvocation actionInvocation) {
                    VideoControlActivity.this.m_nNum4 = 0;
                    super.success(actionInvocation);
                }

                @Override // org.teleal.cling.controlpoint.ActionCallback
                public void failure(ActionInvocation actionInvocation, UpnpResponse upnpResponse, String str2) {
                    if (VideoControlActivity.this.m_nNum4 < Global.g_nNumber) {
                        VideoControlActivity.this.SeekMedia(service, str);
                        VideoControlActivity.access$708(VideoControlActivity.this);
                    } else {
                        VideoControlActivity.this.m_nNum4 = 0;
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
            while (VideoControlActivity.this.m_bRunThread) {
                if (VideoControlActivity.this.m_bUpdate) {
                    VideoControlActivity.this.getPositionInfo(ChoiceRemotePlayerActivity.g_serviceAVTransport);
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
            this.m_ipmsgApp.getControlPoint().execute(new GetPositionInfo(service) { // from class: com.netfeige.display.ui.VideoControlActivity.5
                @Override // org.teleal.cling.controlpoint.ActionCallback
                public void failure(ActionInvocation actionInvocation, UpnpResponse upnpResponse, String str) {
                    if (VideoControlActivity.this.m_nNum3 < Global.g_nNumber) {
                        VideoControlActivity.this.getPositionInfo(service);
                        VideoControlActivity.access$1208(VideoControlActivity.this);
                    } else {
                        VideoControlActivity.this.m_nNum3 = 0;
                    }
                }

                @Override // org.teleal.cling.support.avtransport.callback.GetPositionInfo
                public void received(ActionInvocation actionInvocation, PositionInfo positionInfo) {
                    VideoControlActivity.this.m_nNum3 = 0;
                    if (positionInfo != null) {
                        VideoControlActivity.this.m_strMediaDuration = positionInfo.getTrackDuration();
                        VideoControlActivity.this.m_strCurrentTime = positionInfo.getRelTime();
                        VideoControlActivity.this.recHandler.sendEmptyMessage(0);
                    }
                }
            });
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (Exception e2) {
            e2.printStackTrace();
        }
    }

    @Override // android.app.Activity
    protected void onDestroy() {
        this.m_bRunThread = false;
        super.onDestroy();
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

    private void initControl() {
        TextView textView = (TextView) findViewById(R.id.textV_videoname);
        this.m_textVCurrentVideoName = textView;
        textView.setText(new File(this.m_strFilePath).getName());
        Button button = (Button) findViewById(R.id.back_btn_video);
        this.m_btnBack = button;
        button.setOnClickListener(new BackOnClickListener());
        this.m_textVCurrentTime = (TextView) findViewById(R.id.textV_currenttime);
        TextView textView2 = (TextView) findViewById(R.id.textV_duringtime);
        this.m_textVDurationTime = textView2;
        textView2.setText(this.m_strMediaDuration);
        ImageButton imageButton = (ImageButton) findViewById(R.id.btn_playorpause);
        this.m_btnPlayOrPause = imageButton;
        imageButton.setOnClickListener(new PlayOrPauseOnClickListener());
        SeekBar seekBar = (SeekBar) findViewById(R.id.seekB_control);
        this.m_seekBControl = seekBar;
        seekBar.setOnSeekBarChangeListener(new SeekBarOnChangeListener());
    }

    private class SeekBarOnChangeListener implements SeekBar.OnSeekBarChangeListener {
        @Override // android.widget.SeekBar.OnSeekBarChangeListener
        public void onProgressChanged(SeekBar seekBar, int i, boolean z) {
        }

        @Override // android.widget.SeekBar.OnSeekBarChangeListener
        public void onStartTrackingTouch(SeekBar seekBar) {
        }

        private SeekBarOnChangeListener() {
        }

        @Override // android.widget.SeekBar.OnSeekBarChangeListener
        public void onStopTrackingTouch(SeekBar seekBar) {
            VideoControlActivity.this.m_seekBControl.setProgress(seekBar.getProgress());
            String strIntToTime = VideoControlActivity.this.intToTime(seekBar.getProgress());
            if (strIntToTime != null) {
                VideoControlActivity.this.SeekMedia(ChoiceRemotePlayerActivity.g_serviceAVTransport, strIntToTime);
            }
        }
    }

    private class BackOnClickListener implements View.OnClickListener {
        private BackOnClickListener() {
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            VideoControlActivity.this.m_bRunThread = false;
            VideoControlActivity.this.finish();
            VideoControlActivity.this.onBackPressed();
        }
    }

    private class PlayOrPauseOnClickListener implements View.OnClickListener {
        private PlayOrPauseOnClickListener() {
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            if (VideoControlActivity.this.m_bPlaying) {
                VideoControlActivity.this.PauseMedia(ChoiceRemotePlayerActivity.g_serviceAVTransport);
                VideoControlActivity.this.m_btnPlayOrPause.setBackgroundResource(R.drawable.dlnaplay);
                VideoControlActivity.this.m_bPlaying = false;
                VideoControlActivity.this.m_bUpdate = false;
                return;
            }
            VideoControlActivity.this.playMedia(ChoiceRemotePlayerActivity.g_serviceAVTransport);
            VideoControlActivity.this.m_btnPlayOrPause.setBackgroundResource(R.drawable.dlnapause);
            VideoControlActivity.this.m_bUpdate = true;
            VideoControlActivity.this.m_bPlaying = true;
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

    /* JADX INFO: Access modifiers changed from: private */
    public int timeToInt(String str) {
        if (str == null) {
            return 0;
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

    private void stopRemotePlay() {
        if (ChoiceRemotePlayerActivity.g_serviceAVTransport != null) {
            DlnaHelper.stopPlay(ChoiceRemotePlayerActivity.g_serviceAVTransport, this.m_ipmsgApp);
        }
    }

    @Override // android.app.Activity
    public void onBackPressed() {
        stopRemotePlay();
        super.onBackPressed();
    }
}

