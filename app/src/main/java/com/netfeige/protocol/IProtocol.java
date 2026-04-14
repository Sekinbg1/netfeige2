package com.netfeige.protocol;

import android.content.Context;
import com.netfeige.common.FileInformation;
import com.netfeige.common.HostInformation;
import com.netfeige.common.IUpdateNotify;
import com.netfeige.common.LanSharedItem;
import com.netfeige.common.Public_Def;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Vector;

/* JADX INFO: loaded from: classes.dex */
public interface IProtocol {
    void Destroy();

    boolean Init(IProtocolNotify iProtocolNotify, Context context) throws Public_Def.WifiConnectFailException, IOException;

    boolean cancelRecvFile(FileInformation fileInformation, HostInformation hostInformation);

    boolean cancelSendFile(FileInformation fileInformation, HostInformation hostInformation);

    void entryService(ArrayList<HostInformation> arrayList, boolean z);

    void exitService();

    boolean isBroadcasting();

    void printQuery(String str, HostInformation hostInformation);

    boolean recvFile(FileInformation fileInformation, HostInformation hostInformation, IFileTransNotify iFileTransNotify) throws IOException;

    void sendFeedback(String str);

    long sendFile(String str, Vector<String> vector, HostInformation hostInformation, IFileTransNotify iFileTransNotify, Vector<String> vector2, int i);

    boolean sendFileListQuery(long j);

    boolean sendFileListQuery(HostInformation hostInformation, long j);

    long sendMsg(String str, HostInformation hostInformation);

    boolean sendReserveLinkReq(HostInformation hostInformation, int i, FileInformation fileInformation);

    boolean sendRootFileDLQuery(HostInformation hostInformation, Public_Def.DownloadCmd downloadCmd, ArrayList<LanSharedItem> arrayList);

    boolean sendSubFileDLQuery(HostInformation hostInformation, Public_Def.DownloadCmd downloadCmd, ArrayList<LanSharedItem> arrayList, String str);

    boolean sendSubFileListQuery(HostInformation hostInformation, long j, int i, String str);

    void update(IUpdateNotify iUpdateNotify);
}

