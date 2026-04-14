package com.netfeige.common;

import android.graphics.Bitmap;

/* JADX INFO: loaded from: classes.dex */
public class ImagePreview {
    private Bitmap m_imageBitmap;
    private long m_lLastTime;
    private int m_nFileListPos;
    private String m_strImageName;
    private String m_strImagePath;

    public ImagePreview(int i, String str, String str2, Bitmap bitmap) {
        this.m_lLastTime = 0L;
        this.m_nFileListPos = i;
        this.m_strImageName = str;
        this.m_strImagePath = str2;
        this.m_imageBitmap = bitmap;
    }

    public ImagePreview(int i, String str, String str2, Bitmap bitmap, long j) {
        this.m_lLastTime = 0L;
        this.m_nFileListPos = i;
        this.m_strImageName = str;
        this.m_strImagePath = str2;
        this.m_imageBitmap = bitmap;
        this.m_lLastTime = j;
    }

    public int getM_nFileListPos() {
        return this.m_nFileListPos;
    }

    public void setM_nFileListPos(int i) {
        this.m_nFileListPos = i;
    }

    public String getM_strImageName() {
        return this.m_strImageName;
    }

    public void setM_strImageName(String str) {
        this.m_strImageName = str;
    }

    public String getM_strImagePath() {
        return this.m_strImagePath;
    }

    public void setM_strImagePath(String str) {
        this.m_strImagePath = str;
    }

    public Bitmap getM_imageBitmap() {
        return this.m_imageBitmap;
    }

    public void setM_imageBitmap(Bitmap bitmap) {
        this.m_imageBitmap = bitmap;
    }

    public long getM_lLastTime() {
        return this.m_lLastTime;
    }

    public void setM_lLastTime(long j) {
        this.m_lLastTime = j;
    }
}

