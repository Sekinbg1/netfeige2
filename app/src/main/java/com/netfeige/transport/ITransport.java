package com.netfeige.transport;

import android.content.Context;
import com.netfeige.common.FileInformation;
import com.netfeige.common.Public_Def;
import com.netfeige.protocol.ProPackage;
import java.io.IOException;
import java.util.Vector;

/* JADX INFO: loaded from: classes.dex */
public interface ITransport {
    boolean Init(Context context, ITransNotify iTransNotify) throws Public_Def.WifiConnectFailException, IOException;

    boolean SendMessage(ProPackage proPackage);

    void cancelFileTrans(String str, long j, long j2);

    boolean recvFile(ProPackage proPackage, FileInformation fileInformation) throws IOException;

    boolean sendFile(ProPackage proPackage, FileInformation fileInformation);

    boolean sendFile(ProPackage proPackage, Vector<FileInformation> vector);
}

