package com.netfeige.common;

/* JADX INFO: loaded from: classes.dex */
public class FolderInfo {
    private String m_strCategory;
    private String m_strPath;

    public FolderInfo(String str, String str2) {
        this.m_strCategory = str;
        this.m_strPath = str2;
    }

    public String getCategory() {
        return this.m_strCategory;
    }

    public void setCategory(String str) {
        this.m_strCategory = str;
    }

    public String getPath() {
        return this.m_strPath;
    }

    public void setPath(String str) {
        this.m_strPath = str;
    }
}

