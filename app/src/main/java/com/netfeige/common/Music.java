package com.netfeige.common;

/* JADX INFO: loaded from: classes.dex */
public class Music {
    private long m_lLastTime;
    private long m_lTime;
    private int m_nPosition;
    private String m_strName;
    private String m_strPath;

    public Music(int i, String str, String str2, long j) {
        this.m_lLastTime = 0L;
        this.m_nPosition = i;
        this.m_strName = str;
        this.m_strPath = str2;
        this.m_lTime = j;
    }

    public Music(int i, String str, String str2, long j, long j2) {
        this.m_lLastTime = 0L;
        this.m_nPosition = i;
        this.m_strName = str;
        this.m_strPath = str2;
        this.m_lTime = j;
        this.m_lLastTime = j2;
    }

    public int getPosition() {
        return this.m_nPosition;
    }

    public void setPosition(int i) {
        this.m_nPosition = i;
    }

    public String getName() {
        return this.m_strName;
    }

    public void setName(String str) {
        this.m_strName = str;
    }

    public String getPath() {
        return this.m_strPath;
    }

    public void setPath(String str) {
        this.m_strPath = str;
    }

    public long getTime() {
        return this.m_lTime;
    }

    public void setTime(long j) {
        this.m_lTime = j;
    }

    public long getLastTime() {
        return this.m_lLastTime;
    }

    public void setLastTime(long j) {
        this.m_lLastTime = j;
    }
}

