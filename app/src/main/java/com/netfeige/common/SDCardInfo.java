package com.netfeige.common;

/* JADX INFO: loaded from: classes.dex */
public class SDCardInfo {
    private boolean m_bMounted;
    private String m_strLabel;
    private String m_strMountPoint;

    public String getLabel() {
        return this.m_strLabel;
    }

    public void setLabel(String str) {
        this.m_strLabel = str;
    }

    public String getMountPoint() {
        return this.m_strMountPoint;
    }

    public void setMountPoint(String str) {
        this.m_strMountPoint = str;
    }

    public boolean isMounted() {
        return this.m_bMounted;
    }

    public void setMounted(boolean z) {
        this.m_bMounted = z;
    }

    public String toString() {
        return "SDCardInfo [label=" + this.m_strLabel + ", mountPoint=" + this.m_strMountPoint + ", mounted=" + this.m_bMounted + "]";
    }
}

