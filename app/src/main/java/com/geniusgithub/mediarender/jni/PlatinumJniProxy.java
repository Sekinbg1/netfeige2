package com.geniusgithub.mediarender.jni;

import java.io.UnsupportedEncodingException;

/* JADX INFO: loaded from: classes.dex */
public class PlatinumJniProxy {
    public static native boolean responseGenaEvent(int i, byte[] bArr, byte[] bArr2);

    public static native int startMediaRender(byte[] bArr, byte[] bArr2);

    public static native int stopMediaRender();

    static {
        System.loadLibrary("platinum-jni");
    }

    public static int startMediaRender(String str, String str2) {
        if (str == null) {
            str = "";
        }
        if (str2 == null) {
            str2 = "";
        }
        try {
            return startMediaRender(str.getBytes("utf-8"), str2.getBytes("utf-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public static boolean responseGenaEvent(int i, String str, String str2) {
        if (str == null) {
            str = "";
        }
        if (str2 == null) {
            str2 = "";
        }
        try {
            return responseGenaEvent(i, str.getBytes("utf-8"), str2.getBytes("utf-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return false;
        }
    }
}

