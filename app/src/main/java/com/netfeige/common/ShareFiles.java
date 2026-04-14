package com.netfeige.common;

/* JADX INFO: loaded from: classes.dex */
public class ShareFiles {
    private boolean m_bIsCheck = false;
    private int m_iID;
    private int m_iType;
    private long m_lSize;
    private long m_lTime;
    private String m_strMACList;
    private String m_strName;
    private String m_strPath;

    public ShareFiles(int i, long j, int i2, long j2, String str, String str2, String str3) {
        this.m_iID = i;
        this.m_lTime = j;
        this.m_iType = i2;
        this.m_lSize = j2;
        this.m_strName = str;
        this.m_strPath = str2;
        this.m_strMACList = str3;
    }

    public int getM_iID() {
        return this.m_iID;
    }

    public void setM_iID(int i) {
        this.m_iID = i;
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

    public String getM_strName() {
        return this.m_strName;
    }

    public void setM_strName(String str) {
        this.m_strName = str;
    }

    public String getM_strPath() {
        return this.m_strPath;
    }

    public void setM_strPath(String str) {
        this.m_strPath = str;
    }

    public String getM_strMACList() {
        return this.m_strMACList;
    }

    public void setM_strMACList(String str) {
        this.m_strMACList = str;
    }

    public boolean isM_bIsCheck() {
        return this.m_bIsCheck;
    }

    public void setM_bIsCheck(boolean z) {
        this.m_bIsCheck = z;
    }
}

