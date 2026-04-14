package com.netfeige.common;

/* JADX INFO: loaded from: classes.dex */
public class DiscussExeStatus {
    private boolean mBIsJoin = true;
    private boolean mBIsNotified = false;
    private long mLExeTime;
    private String mStrDestMac;
    private String mStrId;
    private String mStrRecvMac;

    public String getStrId() {
        return this.mStrId;
    }

    public void setStrId(String str) {
        this.mStrId = str;
    }

    public String getStrDestMac() {
        return this.mStrDestMac;
    }

    public void setStrDestMac(String str) {
        this.mStrDestMac = str;
    }

    public String getStrRecvMac() {
        return this.mStrRecvMac;
    }

    public void setStrRecvMac(String str) {
        this.mStrRecvMac = str;
    }

    public long getLExeTime() {
        return this.mLExeTime;
    }

    public void setLExeTime(long j) {
        this.mLExeTime = j;
    }

    public boolean isBIsJoin() {
        return this.mBIsJoin;
    }

    public void setBIsJoin(boolean z) {
        this.mBIsJoin = z;
    }

    public boolean isBIsNotified() {
        return this.mBIsNotified;
    }

    public void setBIsNotified(boolean z) {
        this.mBIsNotified = z;
    }
}

