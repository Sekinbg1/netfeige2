package com.geniusgithub.mediarender.player;

import android.content.Context;
import android.os.Handler;
import java.util.Timer;
import java.util.TimerTask;

/* JADX INFO: loaded from: classes.dex */
public abstract class AbstractTimer {
    private static final int TIMER_INTERVAL = 1000;
    protected Context mContext;
    protected Handler mHandler;
    protected MyTimeTask mTimeTask;
    protected int msgID;
    protected int mTimeInterval = 1000;
    private Timer mTimer = new Timer();

    public AbstractTimer(Context context) {
        this.mContext = context;
    }

    public void setHandler(Handler handler, int i) {
        this.mHandler = handler;
        this.msgID = i;
    }

    public void setTimeInterval(int i) {
        this.mTimeInterval = i;
    }

    public void startTimer() {
        if (this.mTimeTask == null) {
            MyTimeTask myTimeTask = new MyTimeTask();
            this.mTimeTask = myTimeTask;
            this.mTimer.schedule(myTimeTask, 0L, this.mTimeInterval);
        }
    }

    public void stopTimer() {
        MyTimeTask myTimeTask = this.mTimeTask;
        if (myTimeTask != null) {
            myTimeTask.cancel();
            this.mTimeTask = null;
        }
    }

    class MyTimeTask extends TimerTask {
        MyTimeTask() {
        }

        @Override // java.util.TimerTask, java.lang.Runnable
        public void run() {
            if (AbstractTimer.this.mHandler != null) {
                AbstractTimer.this.mHandler.obtainMessage(AbstractTimer.this.msgID).sendToTarget();
            }
        }
    }
}

