package org.teleal.common.util;

/* JADX INFO: loaded from: classes.dex */
public class Exceptions {
    public static Throwable unwrap(Throwable th) throws IllegalArgumentException {
        if (th == null) {
            throw new IllegalArgumentException("Cannot unwrap null throwable");
        }
        Throwable th2 = th;
        while (th != null) {
            th2 = th;
            th = th.getCause();
        }
        return th2;
    }
}

