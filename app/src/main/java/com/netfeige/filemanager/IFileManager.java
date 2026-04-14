package com.netfeige.filemanager;

import android.content.Context;
import java.io.File;
import java.util.Vector;

/* JADX INFO: loaded from: classes.dex */
public interface IFileManager {
    boolean Rename(File file, String str);

    void addMyShareRecord(String str, Context context, Vector<String> vector);

    void compressFolder();

    void copyFile(File file);

    boolean createFolder(String str);

    void cutFile(File file);

    boolean deleteFile(File file);

    String[] getFileDetail(File file);

    void openFile(File file);

    boolean pasteFile(String str, String str2);
}

