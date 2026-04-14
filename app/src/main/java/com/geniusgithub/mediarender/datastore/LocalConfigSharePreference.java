package com.geniusgithub.mediarender.datastore;

import android.content.Context;
import android.content.SharedPreferences;
import com.netfeige.kits.DataConfig;

/* JADX INFO: loaded from: classes.dex */
public class LocalConfigSharePreference {
    public static final String dev_name = "dev_name";
    public static final String preference_name = "LocalConfigSharePreference";

    public static boolean commintDevName(Context context, String str) {
        SharedPreferences.Editor editorEdit = context.getSharedPreferences(preference_name, 0).edit();
        editorEdit.putString(dev_name, str);
        editorEdit.commit();
        return true;
    }

    public static String getDevName(Context context) {
        return DataConfig.getInstance(context.getApplicationContext()).Read(0);
    }
}

