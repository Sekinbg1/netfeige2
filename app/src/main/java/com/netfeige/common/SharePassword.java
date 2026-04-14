package com.netfeige.common;

/* JADX INFO: loaded from: classes.dex */
public class SharePassword {
    private int m_iID;
    private String m_strMac;
    private String m_strPassword;

    public SharePassword(int i, String str, String str2) {
        this.m_iID = i;
        this.m_strMac = str;
        this.m_strPassword = str2;
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

    public String getM_strPassword() {
        return this.m_strPassword;
    }

    public void setM_strPassword(String str) {
        this.m_strPassword = str;
    }
}

