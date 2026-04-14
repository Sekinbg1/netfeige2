package com.netfeige.filemanager;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

/* JADX INFO: loaded from: classes.dex */
public class ApkManager implements IApkManager {
    private static IApkManager m_iApkManager;
    private PackageInfo m_packageInfo = null;
    private PackageManager m_packageManager;

    public ApkManager(Context context) {
        this.m_packageManager = null;
        this.m_packageManager = context.getPackageManager();
    }

    public static IApkManager getIApkManager(Context context) {
        if (m_iApkManager == null) {
            m_iApkManager = new ApkManager(context);
        }
        return m_iApkManager;
    }

    @Override // com.netfeige.filemanager.IApkManager
    public Drawable getApkIcon(String str) {
        PackageInfo packageArchiveInfo = this.m_packageManager.getPackageArchiveInfo(str, 1);
        this.m_packageInfo = packageArchiveInfo;
        if (packageArchiveInfo == null) {
            return null;
        }
        ApplicationInfo applicationInfo = packageArchiveInfo.applicationInfo;
        applicationInfo.sourceDir = str;
        applicationInfo.publicSourceDir = str;
        try {
            return applicationInfo.loadIcon(this.m_packageManager);
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override // com.netfeige.filemanager.IApkManager
    public String getAppName(String str, String str2) {
        PackageInfo packageArchiveInfo = this.m_packageManager.getPackageArchiveInfo(str, 1);
        this.m_packageInfo = packageArchiveInfo;
        if (packageArchiveInfo == null) {
            return str2;
        }
        ApplicationInfo applicationInfo = packageArchiveInfo.applicationInfo;
        applicationInfo.sourceDir = str;
        applicationInfo.publicSourceDir = str;
        return this.m_packageManager.getApplicationLabel(applicationInfo).toString();
    }

    @Override // com.netfeige.filemanager.IApkManager
    public String getVersion(String str) {
        PackageInfo packageArchiveInfo = this.m_packageManager.getPackageArchiveInfo(str, 1);
        this.m_packageInfo = packageArchiveInfo;
        if (packageArchiveInfo == null) {
            return "版本:未知";
        }
        return "版本:" + this.m_packageInfo.versionName;
    }
}

