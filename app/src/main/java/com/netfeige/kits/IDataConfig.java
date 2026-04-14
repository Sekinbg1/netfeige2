package com.netfeige.kits;

import android.content.SharedPreferences;

/* JADX INFO: loaded from: classes.dex */
public interface IDataConfig {
    public static final int BROWSE_METHOD = 29;
    public static final int CHAT_GUIDE = 31;
    public static final int CHECK_COMPRESS = 28;
    public static final int CHECK_UPDATE = 3;
    public static final int DEVICE_GUID = 38;
    public static final int FEIGEDOWNLOAD = 35;
    public static final int FEIGE_GUIDE = 34;
    public static final int FILE_AUTORECV = 2;
    public static final int FILE_DELPROMPT = 7;
    public static final int HEADIAMGE = 36;
    public static final int MAIN_GUIDE = 33;
    public static final int MSG_NOTIFICATION = 6;
    public static final int NETSECTORS = 37;
    public static final int PROMPT_AUDIO = 5;
    public static final int SEND_AUDIO = 4;
    public static final int SORT_CONFIG = 30;
    public static final int USER_GROUP = 1;
    public static final int USER_NAME = 0;
    public static final int WT_GUIDE = 32;

    String Read(int i);

    boolean Reset();

    boolean Write(int i, String str);

    SharedPreferences getSharedPreferences();

    boolean readBoolean(int i, boolean z);

    boolean writeBoolean(int i, boolean z);
}

