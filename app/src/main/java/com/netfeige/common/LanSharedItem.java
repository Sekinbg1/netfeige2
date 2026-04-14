package com.netfeige.common;

/* JADX INFO: loaded from: classes.dex */
public class LanSharedItem {
    private boolean m_bEncrypt;
    private boolean m_bIsCheck = false;
    private int m_iID;
    private int m_iType;
    private long m_lSize;
    private long m_lTime;
    private String m_strFrom;
    private String m_strFromMac;
    private String m_strName;

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

    public String getM_strFrom() {
        return this.m_strFrom;
    }

    public void setM_strFrom(String str) {
        this.m_strFrom = str;
    }

    public String getM_strFromMac() {
        return this.m_strFromMac;
    }

    public void setM_strFromMac(String str) {
        this.m_strFromMac = str;
    }

    public boolean isM_bEncrypt() {
        return this.m_bEncrypt;
    }

    public void setM_bEncrypt(boolean z) {
        this.m_bEncrypt = z;
    }

    public boolean isM_bIsCheck() {
        return this.m_bIsCheck;
    }

    public void setM_bIsCheck(boolean z) {
        this.m_bIsCheck = z;
    }
}

