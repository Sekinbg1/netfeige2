package com.geniusgithub.mediarender.music;

import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import com.geniusgithub.mediarender.util.CommonLog;
import com.geniusgithub.mediarender.util.LogFactory;

/* JADX INFO: loaded from: classes.dex */
public class LoaderHelper {
    private static final CommonLog log = LogFactory.createLog();

    public interface ITaskCompleteCallback {
        void onTaskComplete(Drawable drawable);
    }

    public static boolean syncDownLoadDrawable(String str, final Handler handler, final int i) {
        syncDownLoadDrawable(str, new ITaskCompleteCallback() { // from class: com.geniusgithub.mediarender.music.LoaderHelper.1
            @Override // com.geniusgithub.mediarender.music.LoaderHelper.ITaskCompleteCallback
            public void onTaskComplete(Drawable drawable) {
                Handler handler2 = handler;
                if (handler2 != null) {
                    Message messageObtainMessage = handler2.obtainMessage(i);
                    if (drawable != null) {
                        messageObtainMessage.obj = drawable;
                    } else {
                        messageObtainMessage.obj = null;
                    }
                    messageObtainMessage.sendToTarget();
                }
            }
        });
        return true;
    }

    public static boolean syncDownLoadDrawable(String str, ITaskCompleteCallback iTaskCompleteCallback) {
        new InnerThread(str, iTaskCompleteCallback).start();
        return true;
    }

    public static class InnerThread extends Thread {
        private ITaskCompleteCallback mCallback;
        private String mUri;

        public InnerThread(String str, ITaskCompleteCallback iTaskCompleteCallback) {
            this.mCallback = iTaskCompleteCallback;
            this.mUri = str;
        }

        @Override // java.lang.Thread, java.lang.Runnable
        public void run() {
            Drawable drawableRequestDrawableByUri = NetUtils.requestDrawableByUri(this.mUri);
            ITaskCompleteCallback iTaskCompleteCallback = this.mCallback;
            if (iTaskCompleteCallback != null) {
                iTaskCompleteCallback.onTaskComplete(drawableRequestDrawableByUri);
            }
        }
    }
}

