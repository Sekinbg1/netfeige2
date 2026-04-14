package com.netfeige.display.data;

import com.netfeige.common.FileInformation;
import com.netfeige.common.Public_Def;
import com.netfeige.kits.ScreenShot;
import java.util.ArrayList;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class MsgRecord {
    private String FileName;
    public String bodyStr;
    private String date;
    private long fileId;
    private FileInformation fileInfo;
    private Public_Def.TransStatus fileTranStatus;
    private String from;
    private String id;
    private int nFileAttr;
    private long nPackageID;
    private int nProgress;
    private long size;
    private String strCurrSpeed;
    private String FileFullPath = "";
    private boolean isSend = false;
    private boolean isFile = false;
    private boolean isFailed = false;
    public List<ScreenShot> screenShotList = new ArrayList();

    public MsgRecord(String str, String str2, String str3, String str4) {
        this.id = "";
        this.bodyStr = "";
        this.from = str;
        this.id = str2;
        this.bodyStr = str3;
        this.date = str4;
    }

    public long getnPackageID() {
        return this.nPackageID;
    }

    public void setnPackageID(long j) {
        this.nPackageID = j;
    }

    public long getFileId() {
        return this.fileId;
    }

    public void setFileId(long j) {
        this.fileId = j;
    }

    public String getFileFullPath() {
        return this.FileFullPath;
    }

    public void setFileFullPath(String str) {
        this.FileFullPath = str;
    }

    public String getFileName() {
        return this.FileName;
    }

    public void setFileName(String str) {
        this.FileName = str;
    }

    public long getSize() {
        return this.size;
    }

    public void setSize(long j) {
        this.size = j;
    }

    public boolean isFile() {
        return this.isFile;
    }

    public void setFile(boolean z) {
        this.isFile = z;
    }

    public String getFrom() {
        return this.from;
    }

    public void setFrom(String str) {
        this.from = str;
    }

    public String getDate() {
        return this.date;
    }

    public void setDate(String str) {
        this.date = str;
    }

    public boolean isSend() {
        return this.isSend;
    }

    public void setSend(boolean z) {
        this.isSend = z;
    }

    public Public_Def.TransStatus getFileTranStatus() {
        return this.fileTranStatus;
    }

    public void setFileTranStatus(Public_Def.TransStatus transStatus) {
        this.fileTranStatus = transStatus;
    }

    public int getnProgress() {
        return this.nProgress;
    }

    public void setnProgress(int i) {
        this.nProgress = i;
    }

    public String getStrCurrSpeed() {
        return this.strCurrSpeed;
    }

    public void setStrCurrSpeed(String str) {
        this.strCurrSpeed = str;
    }

    public FileInformation getFileInfo() {
        return this.fileInfo;
    }

    public void setFileInfo(FileInformation fileInformation) {
        this.fileInfo = fileInformation;
    }

    public int getnFileAttr() {
        return this.nFileAttr;
    }

    public void setnFileAttr(int i) {
        this.nFileAttr = i;
    }

    public boolean isFailed() {
        return this.isFailed;
    }

    public void setFailed(boolean z) {
        this.isFailed = z;
    }
}

