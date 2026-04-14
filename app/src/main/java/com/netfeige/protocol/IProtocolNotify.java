package com.netfeige.protocol;

import com.netfeige.common.DiscussInfo;
import com.netfeige.common.FileInformation;
import com.netfeige.common.HostInformation;
import com.netfeige.common.MsgInformation;
import java.util.HashMap;
import java.util.Vector;

/* JADX INFO: loaded from: classes.dex */
public interface IProtocolNotify {
    void notifyAnsEntry(HostInformation hostInformation);

    void notifyCancelRecvFile(HostInformation hostInformation, long j, long j2);

    void notifyCancelSendFile(HostInformation hostInformation, long j, long j2);

    void notifyEntryBroadcastFinish(boolean z);

    void notifyEntryService(HostInformation hostInformation);

    void notifyExitService(HostInformation hostInformation);

    void notifyFileShareListAns(HashMap<String, Object> map);

    void notifyPrintAnswer(HostInformation hostInformation);

    void notifyPrintFinish(HostInformation hostInformation);

    void notifyPrintRefused(HostInformation hostInformation);

    void notifyPrintTimeout(HostInformation hostInformation);

    void notifySendFile(HostInformation hostInformation, Vector<FileInformation> vector, boolean z);

    void notifySendFileFail(HostInformation hostInformation, long j);

    void notifySendMsg(HostInformation hostInformation, MsgInformation msgInformation);

    void notifySendMsgFail(HostInformation hostInformation, long j);

    void notifySubFileShareListAns(HashMap<String, Object> map);

    void onDiscussAdd(String str, String str2);

    void onDiscussExit(String str, String str2);

    void onDiscussInfo();

    void onInvite(HostInformation hostInformation, DiscussInfo discussInfo);

    void onReject(String str, HostInformation hostInformation);
}

