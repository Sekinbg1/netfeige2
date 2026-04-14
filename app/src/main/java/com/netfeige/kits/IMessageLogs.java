package com.netfeige.kits;

import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public interface IMessageLogs {

    public enum MESSAGELOGTYPE {
        TYPE_TEXT,
        TYPE_FILE
    }

    List<MESSAGELOGITEM> readBackMessageLog();

    int readCurrentPage();

    List<MESSAGELOGITEM> readFirstMessageLog(String str, int i);

    List<MESSAGELOGITEM> readForwardMessageLog();

    int readPageCount();

    boolean writeMessageLog(MESSAGELOGITEM messagelogitem);

    public static class MESSAGELOGITEM {
        public MESSAGELOGTYPE LogType;
        public String pszContent;
        public String uIpAddr;
        public int uTime;

        public MESSAGELOGITEM() {
        }

        public MESSAGELOGITEM(String str, int i, MESSAGELOGTYPE messagelogtype, String str2) {
            this.uIpAddr = str;
            this.uTime = i;
            this.LogType = messagelogtype;
            this.pszContent = str2;
        }
    }
}

