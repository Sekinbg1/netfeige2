package com.netfeige.common;

/* JADX INFO: loaded from: classes.dex */
public class StorageDevice {
    private boolean m_IsChecked;
    private long m_lUsableSpace;
    private String m_strDeviceName;
    private String m_strMountPoint;

    public StorageDevice() {
        this.m_strDeviceName = "";
        this.m_strMountPoint = "";
        this.m_IsChecked = false;
        this.m_lUsableSpace = 0L;
    }

    public StorageDevice(String str, String str2, boolean z) {
        this.m_strDeviceName = "";
        this.m_strMountPoint = "";
        this.m_IsChecked = false;
        this.m_lUsableSpace = 0L;
        this.m_strDeviceName = str;
        this.m_strMountPoint = str2;
        this.m_IsChecked = z;
    }

    public String getStrDeviceName() {
        return this.m_strDeviceName;
    }

    public void setStrDeviceName(String str) {
        this.m_strDeviceName = str;
    }

    public String getStrMountPoint() {
        return this.m_strMountPoint;
    }

    public void setStrMountPoint(String str) {
        this.m_strMountPoint = str;
    }

    public boolean isChecked() {
        return this.m_IsChecked;
    }

    public void setIsChecked(boolean z) {
        this.m_IsChecked = z;
    }

    public long getlUsableSpace() {
        return this.m_lUsableSpace;
    }

    public void setlUsableSpace(long j) {
        this.m_lUsableSpace = j;
    }
}

