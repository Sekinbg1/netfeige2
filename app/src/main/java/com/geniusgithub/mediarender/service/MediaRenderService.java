package com.geniusgithub.mediarender.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import com.geniusgithub.mediarender.center.DLNAGenaEventBrocastFactory;
import com.geniusgithub.mediarender.center.DMRCenter;
import com.geniusgithub.mediarender.center.DMRWorkThread;
import com.geniusgithub.mediarender.center.IBaseEngine;
import com.geniusgithub.mediarender.jni.PlatinumReflection;
import com.geniusgithub.mediarender.util.CommonLog;
import com.geniusgithub.mediarender.util.DlnaUtils;
import com.geniusgithub.mediarender.util.LogFactory;

/* JADX INFO: loaded from: classes.dex */
public class MediaRenderService extends Service implements IBaseEngine {
    private static final int DELAY_TIME = 1000;
    private static final int RESTART_ENGINE_MSG_ID = 2;
    public static final String RESTART_RENDER_ENGINE = "com.geniusgithub.restart.engine";
    private static final int START_ENGINE_MSG_ID = 1;
    public static final String START_RENDER_ENGINE = "com.geniusgithub.start.engine";
    private static final CommonLog log = LogFactory.createLog();
    private Handler mHandler;
    private PlatinumReflection.ActionReflectionListener mListener;
    private DLNAGenaEventBrocastFactory mMediaGenaBrocastFactory;
    private DMRWorkThread mWorkThread;

    @Override // android.app.Service
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override // android.app.Service
    public void onCreate() {
        super.onCreate();
        initRenderService();
        log.e("MediaRenderService onCreate");
    }

    @Override // android.app.Service
    public void onDestroy() {
        unInitRenderService();
        log.e("MediaRenderService onDestroy");
        super.onDestroy();
    }

    @Override // android.app.Service
    public int onStartCommand(Intent intent, int i, int i2) {
        String action;
        if (intent != null && (action = intent.getAction()) != null) {
            if (action.equalsIgnoreCase(START_RENDER_ENGINE)) {
                delayToSendStartMsg();
            } else if (action.equalsIgnoreCase(RESTART_RENDER_ENGINE)) {
                delayToSendRestartMsg();
            }
        }
        return super.onStartCommand(intent, i, i2);
    }

    private void initRenderService() {
        DMRCenter dMRCenter = new DMRCenter(this);
        this.mListener = dMRCenter;
        PlatinumReflection.setActionInvokeListener(dMRCenter);
        DLNAGenaEventBrocastFactory dLNAGenaEventBrocastFactory = new DLNAGenaEventBrocastFactory(this);
        this.mMediaGenaBrocastFactory = dLNAGenaEventBrocastFactory;
        dLNAGenaEventBrocastFactory.registerBrocast();
        this.mWorkThread = new DMRWorkThread(this);
        this.mHandler = new Handler() { // from class: com.geniusgithub.mediarender.service.MediaRenderService.1
            @Override // android.os.Handler
            public void handleMessage(Message message) {
                int i = message.what;
                if (i == 1) {
                    MediaRenderService.this.startEngine();
                } else {
                    if (i != 2) {
                        return;
                    }
                    MediaRenderService.this.restartEngine();
                }
            }
        };
    }

    private void unInitRenderService() {
        stopEngine();
        removeStartMsg();
        removeRestartMsg();
        this.mMediaGenaBrocastFactory.unRegisterBrocast();
    }

    private void delayToSendStartMsg() {
        removeStartMsg();
        this.mHandler.sendEmptyMessageDelayed(1, 1000L);
    }

    private void delayToSendRestartMsg() {
        removeStartMsg();
        removeRestartMsg();
        this.mHandler.sendEmptyMessageDelayed(2, 1000L);
    }

    private void removeStartMsg() {
        this.mHandler.removeMessages(1);
    }

    private void removeRestartMsg() {
        this.mHandler.removeMessages(2);
    }

    @Override // com.geniusgithub.mediarender.center.IBaseEngine
    public boolean startEngine() {
        awakeWorkThread();
        return true;
    }

    @Override // com.geniusgithub.mediarender.center.IBaseEngine
    public boolean stopEngine() {
        this.mWorkThread.setParam("", "");
        exitWorkThread();
        return true;
    }

    @Override // com.geniusgithub.mediarender.center.IBaseEngine
    public boolean restartEngine() {
        this.mWorkThread.setParam(DlnaUtils.getDevName(this), DlnaUtils.creat12BitUUID(this));
        if (this.mWorkThread.isAlive()) {
            this.mWorkThread.restartEngine();
            return true;
        }
        this.mWorkThread.start();
        return true;
    }

    private void awakeWorkThread() {
        this.mWorkThread.setParam(DlnaUtils.getDevName(this), DlnaUtils.creat12BitUUID(this));
        if (this.mWorkThread.isAlive()) {
            this.mWorkThread.awakeThread();
        } else {
            this.mWorkThread.start();
        }
    }

    private void exitWorkThread() {
        DMRWorkThread dMRWorkThread = this.mWorkThread;
        if (dMRWorkThread == null || !dMRWorkThread.isAlive()) {
            return;
        }
        this.mWorkThread.exit();
        long jCurrentTimeMillis = System.currentTimeMillis();
        while (this.mWorkThread.isAlive()) {
            try {
                Thread.sleep(100L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        long jCurrentTimeMillis2 = System.currentTimeMillis();
        log.e("exitWorkThread cost time:" + (jCurrentTimeMillis2 - jCurrentTimeMillis));
        this.mWorkThread = null;
    }
}

