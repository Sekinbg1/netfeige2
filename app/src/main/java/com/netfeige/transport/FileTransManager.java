package com.netfeige.transport;

import android.content.Context;
import android.os.Handler;
import com.netfeige.common.FileInformation;
import com.netfeige.common.HostInformation;
import com.netfeige.protocol.ProPackage;
import com.netfeige.transport.TransFile;
import com.netfeige.transport.TransFile.FileRecvThread;
import com.netfeige.transport.TransFile.ReserveConnectThread;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

/* JADX INFO: loaded from: classes.dex */
public class FileTransManager extends Handler {
    private static FileTransManager m_FileTransManager;
    private final int m_nMaxTransNum = 3;
    private TransFile m_TCP = new TransFile(this);
    private Vector<TransFile.ReserveConnectThread> m_vecReserveConnectThread = new Vector<>();
    private Vector<TransFile.IFileTransThread> m_vecFileTransThread = new Vector<>();
    private Vector<TransFileParam> m_vecWaitingTransFile = new Vector<>();
    private HashMap<String, Vector<FileInformation>> m_mapRequestingFile = new HashMap<>();

    private class RecvFileParam {
        public FileInformation fileInfo;
        public ProPackage proPackage;

        public RecvFileParam(ProPackage proPackage, FileInformation fileInformation) {
            this.proPackage = proPackage;
            this.fileInfo = fileInformation;
        }
    }

    private class SendFileParam {
        public HostInformation hostInfo;
        public long nFileID;
        public long nPackageID;
        public long nStartPos;
        public Socket tcpSocket;

        public SendFileParam(Socket socket, long j, long j2, long j3, HostInformation hostInformation) {
            this.tcpSocket = socket;
            this.nPackageID = j;
            this.nFileID = j2;
            this.nStartPos = j3;
            this.hostInfo = hostInformation;
        }
    }

    private class TransFileParam {
        RecvFileParam recvFileParam;
        SendFileParam sendFileParam;

        TransFileParam(Socket socket, long j, long j2, long j3, HostInformation hostInformation) {
            this.recvFileParam = null;
            this.sendFileParam = FileTransManager.this.new SendFileParam(socket, j, j2, j3, hostInformation);
        }

        TransFileParam(ProPackage proPackage, FileInformation fileInformation) {
            this.recvFileParam = FileTransManager.this.new RecvFileParam(proPackage, fileInformation);
            this.sendFileParam = null;
        }
    }

    private synchronized boolean isRepeatFileTrans(long j, long j2) {
        if (this.m_vecWaitingTransFile != null) {
            for (int i = 0; i < this.m_vecWaitingTransFile.size(); i++) {
                RecvFileParam recvFileParam = this.m_vecWaitingTransFile.get(i).recvFileParam;
                SendFileParam sendFileParam = this.m_vecWaitingTransFile.get(i).sendFileParam;
                if (recvFileParam != null) {
                    if (recvFileParam.fileInfo.nPackageID == j && recvFileParam.fileInfo.Id == j2) {
                        return true;
                    }
                } else {
                    if (sendFileParam != null && sendFileParam.nPackageID == j && sendFileParam.nFileID == j2) {
                        return true;
                    }
                }
            }
        }
        if (this.m_vecFileTransThread != null) {
            for (int i2 = 0; i2 < this.m_vecFileTransThread.size(); i2++) {
                if (this.m_vecFileTransThread.get(i2).m_nMainFileInfo.Id == j2 && this.m_vecFileTransThread.get(i2).m_nMainFileInfo.nPackageID == j) {
                    return true;
                }
            }
        }
        return false;
    }

    public TransFile.IFileTransThread getFileTransThread(long j, long j2) {
        if (this.m_vecFileTransThread == null) {
            return null;
        }
        for (int i = 0; i < this.m_vecFileTransThread.size(); i++) {
            if (this.m_vecFileTransThread.get(i).m_nMainFileInfo.Id == j2 && this.m_vecFileTransThread.get(i).m_nMainFileInfo.nPackageID == j) {
                return this.m_vecFileTransThread.get(i);
            }
        }
        return null;
    }

    private synchronized void addWaitingTransFile(Socket socket, long j, long j2, long j3, HostInformation hostInformation) {
        this.m_vecWaitingTransFile.add(new TransFileParam(socket, j, j2, j3, hostInformation));
    }

    private synchronized void addWaitingTransFile(ProPackage proPackage, FileInformation fileInformation) {
        this.m_vecWaitingTransFile.add(new TransFileParam(proPackage, fileInformation));
    }

    private void startWaitingTransFile() {
        if (this.m_vecFileTransThread.size() >= 3 || this.m_vecWaitingTransFile.isEmpty()) {
            return;
        }
        TransFileParam transFileParamRemove = this.m_vecWaitingTransFile.remove(0);
        if (transFileParamRemove.recvFileParam != null) {
            try {
                recvFile(transFileParamRemove.recvFileParam.proPackage, transFileParamRemove.recvFileParam.fileInfo);
            } catch (Exception unused) {
            }
        }
        if (transFileParamRemove.sendFileParam != null) {
            sendFile(transFileParamRemove.sendFileParam.tcpSocket, transFileParamRemove.sendFileParam.nPackageID, transFileParamRemove.sendFileParam.nFileID, transFileParamRemove.sendFileParam.nStartPos, transFileParamRemove.sendFileParam.hostInfo);
        }
    }

    public static FileTransManager getInstance() {
        if (m_FileTransManager == null) {
            m_FileTransManager = new FileTransManager();
        }
        return m_FileTransManager;
    }

    private FileTransManager() {
    }

    public void start(Context context, ITransNotify iTransNotify) throws IOException {
        this.m_TCP.start(context, iTransNotify);
    }

    public void ReserveConnectClient(ProPackage proPackage, String[] strArr) {
        InetAddress inetAddress = proPackage.HostInfo.IpAddr.netAddr;
        int i = Integer.parseInt(strArr[0]);
        long j = Long.parseLong(strArr[1]);
        long j2 = strArr.length > 3 ? Long.parseLong(strArr[3]) : -1L;
        TransFile.ReserveConnectThread reserveConnectThread = null;
        for (int i2 = 0; i2 < this.m_vecReserveConnectThread.size(); i2++) {
            if (this.m_vecReserveConnectThread.get(i2).m_lFileId == j) {
                reserveConnectThread = this.m_vecReserveConnectThread.get(i2);
            }
        }
        if (reserveConnectThread != null) {
            return;
        }
        TransFile transFile = this.m_TCP;
        transFile.getClass();
        transFile.new ReserveConnectThread(j, j2, inetAddress, i).m_Thread.start();
    }

    public synchronized boolean recvFile(ProPackage proPackage, FileInformation fileInformation) throws IOException {
        if (proPackage == null || fileInformation == null) {
            return false;
        }
        if (isRepeatFileTrans(fileInformation.nPackageID, fileInformation.Id)) {
            return true;
        }
        if (this.m_vecFileTransThread.size() >= 3) {
            addWaitingTransFile(proPackage, fileInformation);
            return true;
        }
        TransFile transFile = this.m_TCP;
        transFile.getClass();
        transFile.new FileRecvThread(fileInformation, proPackage).start();
        return true;
    }

    public boolean sendFile(Socket socket, long j, long j2, long j3, HostInformation hostInformation) {
        if (socket != null || hostInformation != null) {
            if (this.m_vecFileTransThread.size() >= 3) {
                addWaitingTransFile(socket, j, j2, j3, hostInformation);
                return true;
            }
            TransFile transFile = this.m_TCP;
            transFile.getClass();
            new TransFile.FileSendThread(transFile, socket, j, j2, j3, hostInformation).start();
        }
        return true;
    }

    public boolean addReserveLinkThread(TransFile.ReserveConnectThread reserveConnectThread) {
        if (reserveConnectThread == null) {
            return false;
        }
        return this.m_vecReserveConnectThread.add(reserveConnectThread);
    }

    public boolean removeReserveLinkThread(long j, long j2) {
        for (int i = 0; i < this.m_vecReserveConnectThread.size(); i++) {
            if (this.m_vecReserveConnectThread.get(i).m_lFileId == j2) {
                this.m_vecReserveConnectThread.remove(i);
                return true;
            }
        }
        return false;
    }

    public boolean removeReserveLinkThread(TransFile.ReserveConnectThread reserveConnectThread) {
        if (reserveConnectThread == null) {
            return false;
        }
        return this.m_vecReserveConnectThread.remove(reserveConnectThread);
    }

    public boolean addRequestingFile(String str, FileInformation fileInformation) {
        if (fileInformation == null) {
            return false;
        }
        Vector<FileInformation> vector = this.m_mapRequestingFile.get(str);
        if (vector == null) {
            vector = new Vector<>();
            this.m_mapRequestingFile.put(str, vector);
        }
        return vector.add(fileInformation);
    }

    public FileInformation removeRequestingFile(String str, long j, long j2, long j3) {
        Iterator<Map.Entry<String, Vector<FileInformation>>> it = this.m_mapRequestingFile.entrySet().iterator();
        while (it.hasNext()) {
            Vector<FileInformation> value = it.next().getValue();
            for (int i = 0; i < value.size(); i++) {
                if (value.get(i).Id == j2 && value.get(i).nPackageID == j) {
                    FileInformation fileInformationRemove = value.remove(i);
                    if (value.size() == 0) {
                        it.remove();
                    }
                    fileInformationRemove.startPos = j3;
                    return fileInformationRemove;
                }
            }
        }
        return null;
    }

    public void removeRequestingFile(String str, long j) {
        for (Map.Entry<String, Vector<FileInformation>> entry : this.m_mapRequestingFile.entrySet()) {
            Vector<FileInformation> value = entry.getValue();
            if (entry.getKey().equals(str)) {
                for (int i = 0; i < value.size(); i++) {
                    if (value.get(i).nPackageID == j) {
                        value.remove(i);
                        if (value.size() == 0) {
                            this.m_mapRequestingFile.remove(entry.getKey());
                        }
                    }
                }
            }
        }
    }

    public void removeRequestingFile(String str) {
        this.m_mapRequestingFile.remove(str);
    }

    public boolean addTransThread(TransFile.IFileTransThread iFileTransThread) {
        if (iFileTransThread == null) {
            return false;
        }
        return this.m_vecFileTransThread.add(iFileTransThread);
    }

    public boolean removeTransThread(TransFile.IFileTransThread iFileTransThread) {
        if (iFileTransThread == null) {
            return false;
        }
        iFileTransThread.stop();
        boolean zRemove = this.m_vecFileTransThread.remove(iFileTransThread);
        if (zRemove) {
            startWaitingTransFile();
        }
        return zRemove;
    }

    public FileInformation removeTransThread(String str, long j, long j2) {
        for (int i = 0; i < this.m_vecFileTransThread.size(); i++) {
            TransFile.IFileTransThread iFileTransThread = this.m_vecFileTransThread.get(i);
            FileInformation fileInformation = iFileTransThread.m_nMainFileInfo;
            HostInformation hostInformation = iFileTransThread.m_RemoteHostInformation;
            if (fileInformation != null && hostInformation != null && fileInformation.Id == j2 && fileInformation.nPackageID == j) {
                iFileTransThread.stop();
                this.m_vecFileTransThread.remove(i);
                startWaitingTransFile();
                return fileInformation;
            }
        }
        return null;
    }
}

