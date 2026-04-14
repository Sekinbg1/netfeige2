package com.netfeige.protocol;

import com.netfeige.common.FileInformation;

/* JADX INFO: loaded from: classes.dex */
public interface IFileTransNotify {
    void reNamed(FileInformation fileInformation, FileInformation fileInformation2);

    void transDir(FileInformation fileInformation, long j, FileInformation fileInformation2, long j2, String str);

    void transException(FileInformation fileInformation, Exception exc);

    void transFile(FileInformation fileInformation, long j, String str);
}

