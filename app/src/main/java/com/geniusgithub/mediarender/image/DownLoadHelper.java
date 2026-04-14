package com.geniusgithub.mediarender.image;

import com.geniusgithub.mediarender.util.CommonLog;
import com.geniusgithub.mediarender.util.LogFactory;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/* JADX INFO: loaded from: classes.dex */
public class DownLoadHelper {
    private static final int THREAD_COUNT = 1;
    private static final CommonLog log = LogFactory.createLog();
    private ExecutorService mExecutorService;

    public interface IDownLoadCallback {
        void downLoadResult(boolean z, String str);
    }

    public void init() {
        if (this.mExecutorService == null) {
            this.mExecutorService = Executors.newFixedThreadPool(1);
        }
    }

    public void unInit() {
        ExecutorService executorService = this.mExecutorService;
        if (executorService != null) {
            executorService.shutdown();
            this.mExecutorService.shutdownNow();
            this.mExecutorService = null;
        }
    }

    public boolean syncDownLoadFile(String str, String str2, IDownLoadCallback iDownLoadCallback) {
        if (this.mExecutorService == null) {
            return false;
        }
        log.d("syncDownLoadFile  requestUrl = " + str);
        this.mExecutorService.execute(new FileDownTask(str, str2, iDownLoadCallback));
        return true;
    }
}

