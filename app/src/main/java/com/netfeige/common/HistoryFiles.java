package com.netfeige.common;

/* JADX INFO: loaded from: classes.dex */
public class HistoryFiles {
    private int m_iID;
    private int m_iTransStatus;
    private int m_iType;
    private long m_lSize;
    private long m_lTime;
    private String m_strDiscussID;
    private String m_strFileFullPath;
    private String m_strFileName;
    private String m_strMac;

    public HistoryFiles(int i, String str, String str2, int i2, long j, int i3, long j2, String str3, String str4) {
        this.m_iID = i;
        this.m_strMac = str;
        this.m_strDiscussID = str2;
        this.m_iTransStatus = i2;
        this.m_lTime = j;
        this.m_iType = i3;
        this.m_lSize = j2;
        this.m_strFileName = str3;
        this.m_strFileFullPath = str4;
    }

    public int getM_iID() {
        return this.m_iID;
    }

    public void setM_iID(int i) {
        this.m_iID = i;
    }

    public String getM_strMac() {
        return this.m_strMac;
    }

    public void setM_strMac(String str) {
        this.m_strMac = str;
    }

    public String getM_strDiscussID() {
        return this.m_strDiscussID;
    }

    public void setM_strDiscussID(String str) {
        this.m_strDiscussID = str;
    }

    public int getM_iTransStatus() {
        return this.m_iTransStatus;
    }

    public void setM_iTransStatus(int i) {
        this.m_iTransStatus = i;
    }

    public long getM_lTime() {
        return this.m_lTime;
    }

    public void setM_lTime(long j) {
        this.m_lTime = j;
    }

    public int getM_iType() {
        return this.m_iType;
    }

    public void setM_iType(int i) {
        this.m_iType = i;
    }

    public long getM_lSize() {
        return this.m_lSize;
    }

    public void setM_lSize(long j) {
        this.m_lSize = j;
    }

    public String getM_strFileName() {
        return this.m_strFileName;
    }

    public void setM_strFileName(String str) {
        this.m_strFileName = str;
    }

    public String getM_strFileFullPath() {
        return this.m_strFileFullPath;
    }

    public void setM_strFileFullPath(String str) {
        this.m_strFileFullPath = str;
    }
}

