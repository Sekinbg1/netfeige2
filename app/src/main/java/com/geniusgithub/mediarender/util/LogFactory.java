package com.geniusgithub.mediarender.util;

/* JADX INFO: loaded from: classes.dex */
public class LogFactory {
    private static final String TAG = "MediaRender";
    private static CommonLog log;

    public static CommonLog createLog() {
        if (log == null) {
            log = new CommonLog();
        }
        log.setTag(TAG);
        return log;
    }

    public static CommonLog createLog(String str) {
        if (log == null) {
            log = new CommonLog();
        }
        if (str == null || str.length() < 1) {
            log.setTag(TAG);
        } else {
            log.setTag(str);
        }
        return log;
    }
}

