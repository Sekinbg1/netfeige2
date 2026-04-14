package com.netfeige.common;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import java.util.Vector;

/* JADX INFO: loaded from: classes.dex */
public class FileInfo {
    private static long ID;
    private Bitmap bitmap;
    private Drawable drawable;
    public String id;
    public boolean isCheck;
    public boolean isFloder;
    private long lastTime;
    private long mediaId;
    public String name;
    public FileInfo parent;
    public String path;
    public long size;
    public Vector<FileInfo> subFiles;

    public FileInfo() {
        this.id = "";
        this.mediaId = -1L;
        this.name = "";
        this.path = "";
        this.size = 0L;
        this.lastTime = 0L;
        this.isFloder = false;
        this.isCheck = false;
        this.bitmap = null;
        this.drawable = null;
        this.parent = null;
        this.subFiles = null;
        this.id = Long.toHexString(ID);
        ID++;
    }

    public FileInfo(FileInfo fileInfo) {
        this.id = "";
        this.mediaId = -1L;
        this.name = "";
        this.path = "";
        this.size = 0L;
        this.lastTime = 0L;
        this.isFloder = false;
        this.isCheck = false;
        this.bitmap = null;
        this.drawable = null;
        this.parent = null;
        this.subFiles = null;
        this.parent = fileInfo;
        this.id = Long.toHexString(ID);
        ID++;
    }

    public long getMediaId() {
        return this.mediaId;
    }

    public void setMediaId(long j) {
        this.mediaId = j;
    }

    public String getName() {
        return this.name;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String str) {
        this.id = str;
    }

    public void setName(String str) {
        this.name = str;
    }

    public String getPath() {
        return this.path;
    }

    public void setPath(String str) {
        this.path = str;
    }

    public long getSize() {
        return this.size;
    }

    public void setSize(long j) {
        this.size = j;
    }

    public boolean isFloder() {
        return this.isFloder;
    }

    public void setFloder(boolean z) {
        this.isFloder = z;
    }

    public Vector<FileInfo> getSubFiles() {
        return this.subFiles;
    }

    public void setSubFiles(Vector<FileInfo> vector) {
        this.subFiles = vector;
    }

    public boolean isCheck() {
        return this.isCheck;
    }

    public void setCheck(boolean z) {
        this.isCheck = z;
    }

    public long getLastTime() {
        return this.lastTime;
    }

    public void setLastTime(long j) {
        this.lastTime = j;
    }

    public Bitmap getBitmap() {
        return this.bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public Drawable getDrawable() {
        return this.drawable;
    }

    public void setDrawable(Drawable drawable) {
        this.drawable = drawable;
    }
}

