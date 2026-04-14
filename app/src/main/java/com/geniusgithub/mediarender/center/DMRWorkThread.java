package com.geniusgithub.mediarender.center;

import android.content.Context;
import com.geniusgithub.mediarender.jni.PlatinumJniProxy;
import com.geniusgithub.mediarender.util.CommonLog;
import com.geniusgithub.mediarender.util.CommonUtil;
import com.geniusgithub.mediarender.util.LogFactory;
import com.netfeige.display.data.IpmsgApplication;

/* JADX INFO: loaded from: classes.dex */
public class DMRWorkThread extends Thread implements IBaseEngine {
    private static final int CHECK_INTERVAL = 30000;
    private static final CommonLog log = LogFactory.createLog();
    private Context mContext;
    private boolean mStartSuccess = false;
    private boolean mExitFlag = false;
    private String mFriendName = "";
    private String mUUID = "";
    private IpmsgApplication mApplication = IpmsgApplication.getInstance();

    public DMRWorkThread(Context context) {
        this.mContext = null;
        this.mContext = context;
    }

    public void setFlag(boolean z) {
        this.mStartSuccess = z;
    }

    public void setParam(String str, String str2) {
        this.mFriendName = str;
        this.mUUID = str2;
        this.mApplication.updateDevInfo(str, str2);
    }

    public void awakeThread() {
        synchronized (this) {
            notifyAll();
        }
    }

    public void exit() {
        this.mExitFlag = true;
        awakeThread();
    }

    @Override // java.lang.Thread, java.lang.Runnable
    public void run() {
        log.e("DMRWorkThread run...");
        while (true) {
            if (this.mExitFlag) {
                stopEngine();
                break;
            }
            refreshNotify();
            synchronized (this) {
                try {
                    wait(30000L);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (this.mExitFlag) {
                stopEngine();
                break;
            }
        }
        log.e("DMRWorkThread over...");
    }

    public void refreshNotify() {
        if (CommonUtil.checkNetworkState(this.mContext) && !this.mStartSuccess) {
            stopEngine();
            try {
                Thread.sleep(200L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (startEngine()) {
                this.mStartSuccess = true;
            }
        }
    }

    @Override // com.geniusgithub.mediarender.center.IBaseEngine
    public boolean startEngine() {
        if (this.mFriendName.length() == 0) {
            return false;
        }
        boolean z = PlatinumJniProxy.startMediaRender(this.mFriendName, this.mUUID) == 0;
        this.mApplication.setDevStatus(z);
        return z;
    }

    @Override // com.geniusgithub.mediarender.center.IBaseEngine
    public boolean stopEngine() {
        PlatinumJniProxy.stopMediaRender();
        this.mApplication.setDevStatus(false);
        return true;
    }

    @Override // com.geniusgithub.mediarender.center.IBaseEngine
    public boolean restartEngine() {
        setFlag(false);
        awakeThread();
        return true;
    }
}

