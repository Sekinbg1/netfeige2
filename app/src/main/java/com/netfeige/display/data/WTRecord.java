package com.netfeige.display.data;

/* JADX INFO: loaded from: classes.dex */
public class WTRecord {
    private String WTName;
    private WTOperateEnum m_WTStatusEnum;

    public WTRecord(String str, WTOperateEnum wTOperateEnum) {
        this.WTName = str;
        this.m_WTStatusEnum = wTOperateEnum;
    }

    public String getWTName() {
        return this.WTName;
    }

    public void setWTName(String str) {
        this.WTName = str;
    }

    public WTOperateEnum getM_WTStatusEnum() {
        return this.m_WTStatusEnum;
    }

    public void setM_WTStatusEnum(WTOperateEnum wTOperateEnum) {
        this.m_WTStatusEnum = wTOperateEnum;
    }
}

