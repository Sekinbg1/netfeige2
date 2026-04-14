package com.netfeige.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import androidx.core.content.FileProvider;
import com.netfeige.dlna.HttpServer;
import java.io.File;
import java.util.Locale;

/* JADX INFO: loaded from: classes.dex */
public class OpenFileUtil {
    private static Uri getUri(Context context, Intent intent, File file) {
        if (Build.VERSION.SDK_INT >= 24) {
            Uri uriForFile = FileProvider.getUriForFile(context, context.getPackageName() + ".provider", file);
            intent.addFlags(1);
            return uriForFile;
        }
        return Uri.fromFile(file);
    }

    public static Intent openFile(Context context, String str) {
        File file = new File(str);
        if (!file.exists()) {
            return null;
        }
        String lowerCase = file.getName().substring(file.getName().lastIndexOf(".") + 1, file.getName().length()).toLowerCase(Locale.getDefault());
        if (lowerCase.equals("m4a") || lowerCase.equals("mp3") || lowerCase.equals("mid") || lowerCase.equals("xmf") || lowerCase.equals("ogg") || lowerCase.equals("wav")) {
            return getAudioFileIntent(context, str);
        }
        if (lowerCase.equals("3gp") || lowerCase.equals("mp4")) {
            return getVideoFileIntent(context, str);
        }
        if (lowerCase.equals("jpg") || lowerCase.equals("gif") || lowerCase.equals("png") || lowerCase.equals("jpeg") || lowerCase.equals("bmp")) {
            return getImageFileIntent(context, str);
        }
        if (lowerCase.equals("apk")) {
            return getApkFileIntent(context, str);
        }
        if (lowerCase.equals("ppt") || lowerCase.equals("pptx")) {
            return getPptFileIntent(context, str);
        }
        if (lowerCase.equals("xls") || lowerCase.equals("xlsx")) {
            return getExcelFileIntent(context, str);
        }
        if (lowerCase.equals("doc") || lowerCase.equals("docx")) {
            return getWordFileIntent(context, str);
        }
        if (lowerCase.equals("pdf")) {
            return getPdfFileIntent(context, str);
        }
        if (lowerCase.equals("chm")) {
            return getChmFileIntent(context, str);
        }
        if (lowerCase.equals("txt")) {
            return getTextFileIntent(context, str, false);
        }
        return getAllIntent(context, str);
    }

    public static Intent getAllIntent(Context context, String str) {
        Intent intent = new Intent();
        intent.addFlags(268435456);
        intent.setAction("android.intent.action.VIEW");
        intent.setDataAndType(getUri(context, intent, new File(str)), "*/*");
        return intent;
    }

    public static Intent getApkFileIntent(Context context, String str) {
        Intent intent = new Intent();
        intent.addFlags(268435456);
        intent.setAction("android.intent.action.VIEW");
        intent.setDataAndType(getUri(context, intent, new File(str)), "application/vnd.android.package-archive");
        return intent;
    }

    public static Intent getVideoFileIntent(Context context, String str) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addFlags(67108864);
        intent.putExtra("oneshot", 0);
        intent.putExtra("configchange", 0);
        intent.setDataAndType(getUri(context, intent, new File(str)), "video/*");
        return intent;
    }

    public static Intent getAudioFileIntent(Context context, String str) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addFlags(67108864);
        intent.putExtra("oneshot", 0);
        intent.putExtra("configchange", 0);
        intent.setDataAndType(getUri(context, intent, new File(str)), "audio/*");
        return intent;
    }

    public static Intent getHtmlFileIntent(Context context, String str) {
        Uri uriBuild = Uri.parse(str).buildUpon().encodedAuthority("com.android.htmlfileprovider").scheme("content").encodedPath(str).build();
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.setDataAndType(uriBuild, HttpServer.MIME_HTML);
        return intent;
    }

    public static Intent getImageFileIntent(Context context, String str) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(268435456);
        intent.setDataAndType(getUri(context, intent, new File(str)), "image/*");
        return intent;
    }

    public static Intent getPptFileIntent(Context context, String str) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(268435456);
        intent.setDataAndType(getUri(context, intent, new File(str)), "application/vnd.ms-powerpoint");
        return intent;
    }

    public static Intent getExcelFileIntent(Context context, String str) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(268435456);
        intent.setDataAndType(getUri(context, intent, new File(str)), "application/vnd.ms-excel");
        return intent;
    }

    public static Intent getWordFileIntent(Context context, String str) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(268435456);
        intent.setDataAndType(getUri(context, intent, new File(str)), "application/msword");
        return intent;
    }

    public static Intent getChmFileIntent(Context context, String str) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(268435456);
        intent.setDataAndType(getUri(context, intent, new File(str)), "application/x-chm");
        return intent;
    }

    public static Intent getTextFileIntent(Context context, String str, boolean z) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(268435456);
        if (z) {
            intent.setDataAndType(Uri.parse(str), HttpServer.MIME_PLAINTEXT);
        } else {
            intent.setDataAndType(getUri(context, intent, new File(str)), HttpServer.MIME_PLAINTEXT);
        }
        return intent;
    }

    public static Intent getPdfFileIntent(Context context, String str) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(268435456);
        intent.setDataAndType(getUri(context, intent, new File(str)), "application/pdf");
        return intent;
    }
}

